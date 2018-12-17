package cn.sowell.dataserver.model.tmpl.strategy;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.dict.dao.DictionaryDao;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionArrayEntity;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionArrayEntityField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionTemplate;
import cn.sowell.dataserver.model.tmpl.service.TemplateService;

public class TemplateActionUpdateStrategy implements TemplateUpdateStrategy<TemplateActionTemplate>{

	@Resource
	NormalOperateDao nDao;
	
	@Resource
	TemplateService tService;
	
	@Resource
	DictionaryDao dictDao;
	
	@Override
	public void update(TemplateActionTemplate template) {
		TemplateActionTemplate origin = tService.getActionTemplate(template.getId());
		if(origin != null){
			Set<Long> addFieldIds = new HashSet<Long>();
			template.getGroups().forEach(group->
				group.getFields().forEach(field->{
					if(field.getId() == null){
						addFieldIds.add(field.getFieldId());
					}
				})
			);
			Set<Long> originGroupFieldIds = new HashSet<Long>();
			origin.getGroups().forEach(group->group.getFields().forEach(field->originGroupFieldIds.add(field.getId())));
			Map<Long, DictionaryField> fieldMap = dictDao.getFieldMap(addFieldIds);
			
			Date now = new Date();
			origin.setTitle(template.getTitle());
			origin.setUpdateTime(now);
			nDao.update(origin);
			Set<Long> toDeleteGroupId = CollectionUtils.toSet(origin.getGroups(), group->group.getId());
			Set<Long> toDeleteFieldId = new HashSet<Long>(originGroupFieldIds);
			Map<Long, TemplateActionFieldGroup> originGroupMap = CollectionUtils.toMap(origin.getGroups(), group->group.getId());
			for (TemplateActionFieldGroup group : template.getGroups()) {
				if(group.getId() != null){
					if(originGroupMap.containsKey(group.getId())){
						TemplateActionFieldGroup originGroup = originGroupMap.get(group.getId());
						toDeleteGroupId.remove(group.getId());
						originGroup.setTitle(group.getTitle());
						originGroup.setUnallowedCreate(group.getUnallowedCreate());
						originGroup.setOrder(group.getOrder());
						originGroup.setSelectionTemplateId(group.getSelectionTemplateId());
						originGroup.setUpdateTime(now);
						nDao.update(originGroup);
						
						Map<Long, TemplateActionField> originFieldMap = CollectionUtils.toMap(originGroup.getFields(), field->field.getId());
						for (TemplateActionField field : group.getFields()) {
							if(field.getId() != null){
								if(originFieldMap.containsKey(field.getId())){
									toDeleteFieldId.remove(field.getId());
									TemplateActionField originField = originFieldMap.get(field.getId());
									originField.setTitle(field.getTitle());
									originField.setColNum(field.getColNum());
									originField.setOrder(field.getOrder());
									originField.setViewValue(field.getViewValue());
									originField.setValidators(field.getValidators());
									originField.setUpdateTime(now);
									nDao.update(originField);
								}else{
									throw new RuntimeException("字段组[id=" + group.getId() + "]不能修改字段[id=" + field.getId() + "，因为字段不存在，可能是所在模板已经被修改]");
								}
							}else{
								if(fieldMap.containsKey(field.getFieldId())){
									field.setFieldName(fieldMap.get(field.getFieldId()).getFullKey());
									field.setGroupId(group.getId());
									field.setUpdateTime(now);
									Long fieldId = nDao.save(field);
									field.setId(fieldId);
								}else{
									throw new RuntimeException("找不到fieldId为[" + field.getFieldId() + "]的字段");
								}
							}
						}
						handlerActionEntities(group, originGroup);
					}else{
						throw new RuntimeException("操作模板[id=" + origin.getId() + "]不能修改字段组[id=" + group.getId() + "，因为字段组不存在，可能是所在模板已经被修改]");
					}
				}else{
					group.setTmplId(origin.getId());
					group.setUpdateTime(now);
					Long groupId = nDao.save(group);
					group.setId(groupId);
					for (TemplateActionField field : group.getFields()) {
						if(fieldMap.containsKey(field.getFieldId())){
							field.setFieldName(fieldMap.get(field.getFieldId()).getFullKey());
							field.setGroupId(groupId);
							field.setUpdateTime(now);
							Long fieldId = nDao.save(field);
							field.setId(fieldId);
						}else{
							throw new RuntimeException("找不到fieldId为[" + field.getFieldId() + "]的字段");
						}
					}
					handlerActionEntities(group, null);
				}
			}
			nDao.remove(TemplateActionFieldGroup.class, toDeleteGroupId);
			nDao.remove(TemplateActionField.class, toDeleteFieldId);
		}else{
			throw new RuntimeException("找不到id为[" + template.getId() + "]的操作模板，无法更新");
		}
	}

	private void handlerActionEntities(TemplateActionFieldGroup group, TemplateActionFieldGroup originGroup) {
		if(originGroup != null) {
			Set<Long> toRemoveEntityIds = CollectionUtils.toSet(originGroup.getEntities(), TemplateActionArrayEntity::getId);
			Map<Long, TemplateActionArrayEntity> originEntityMap = CollectionUtils.toMap(originGroup.getEntities(), TemplateActionArrayEntity::getId);
			for(TemplateActionArrayEntity entity: group.getEntities()) {
				if(entity.getId() != null) {
					if(toRemoveEntityIds.contains(entity.getId())) {
						toRemoveEntityIds.remove(entity.getId());
						TemplateActionArrayEntity originEntity = originEntityMap.get(entity.getId());
						//修改实体
						originEntity.setIndex(entity.getIndex());
						originEntity.setRelationLabel(entity.getRelationLabel());
						nDao.update(originEntity);
						Set<Long> toRemoveFields = CollectionUtils.toSet(originEntity.getFields(), TemplateActionArrayEntityField::getId);
						Map<Long, TemplateActionArrayEntityField> originFieldMap = CollectionUtils.toMap(originEntity.getFields(), TemplateActionArrayEntityField::getId);
						for (TemplateActionArrayEntityField eField : entity.getFields()) {
							if(toRemoveFields.contains(eField.getId())) {
								//修改
								toRemoveFields.remove(eField.getId());
								TemplateActionArrayEntityField originEntityField = originFieldMap.get(eField.getId());
								originEntityField.setValue(eField.getValue());
								nDao.update(originEntityField);
							}else {
								//创建
								eField.setActionArrayEntityId(originEntity.getId());
								nDao.save(eField);
							}
						}
						//删除
						nDao.remove(TemplateActionArrayEntityField.class, toRemoveFields);
						continue;
					}
				}
				//实体原本不存在，需要当前实体，
				//实体的所对应的模板列
				//创建实体
				entity.setTmplFieldGroupId(originGroup.getId());
				entity.setId(null);
				Long entityId = nDao.save(entity);
				Set<TemplateActionArrayEntityField> eFieldSet = new HashSet<>(entity.getFields());
				for (TemplateActionArrayEntityField eField : entity.getFields()) {
					eField.setActionArrayEntityId(entityId);
				}
				for (TemplateActionField field : group.getFields()) {
					List<TemplateActionArrayEntityField> entityFelds = field.getArrayEntityFields();
					for (TemplateActionArrayEntityField eField : entityFelds) {
						if(eFieldSet.contains(eField)) {
							eField.setTmplFieldId(field.getId());
							nDao.save(eField);
							break;
						}
					}
				}
			}
			nDao.remove(TemplateActionArrayEntity.class, toRemoveEntityIds);
		}else {
			//原字段组不存在
			for (TemplateActionArrayEntity entity : group.getEntities()) {
				entity.setId(null);
				entity.setTmplFieldGroupId(group.getId());
				Long entityId = nDao.save(entity);
				for (TemplateActionArrayEntityField eField : entity.getFields()) {
					eField.setActionArrayEntityId(entityId);
				}
			}
			for (TemplateActionField field : group.getFields()) {
				List<TemplateActionArrayEntityField> entityFelds = field.getArrayEntityFields();
				for (TemplateActionArrayEntityField eField : entityFelds) {
					eField.setTmplFieldId(field.getId());
					nDao.save(eField);
				}
			}
		}
	}


	@Override
	public Long create(TemplateActionTemplate template) {
		Set<Long> fieldIds = new HashSet<Long>();
		template.getGroups().forEach(group->group.getFields().forEach(field->fieldIds.add(field.getFieldId())));
		Map<Long, DictionaryField> fieldMap = dictDao.getFieldMap(fieldIds);
		Date now = new Date();
		template.setCreateTime(now);
		template.setUpdateTime(now);
		Long tmplId = nDao.save(template);
		for (TemplateActionFieldGroup group : template.getGroups()) {
			group.setTmplId(tmplId);
			group.setUpdateTime(now);
			Long groupId = nDao.save(group);
			for (TemplateActionField field : group.getFields()) {
				if(fieldMap.containsKey(field.getFieldId())){
					field.setFieldName(fieldMap.get(field.getFieldId()).getFullKey());
					field.setGroupId(groupId);
					field.setUpdateTime(now);
					nDao.save(field);
				}else{
					throw new RuntimeException("找不到fieldId为[" + field.getFieldId() + "]的字段");
				}
			}
			handlerActionEntities(group, null);
		}
		return tmplId;
	}


}
