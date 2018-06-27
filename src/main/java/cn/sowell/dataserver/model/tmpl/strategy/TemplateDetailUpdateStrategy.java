package cn.sowell.dataserver.model.tmpl.strategy;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.dict.dao.DictionaryDao;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.service.TemplateService;

public class TemplateDetailUpdateStrategy implements TemplateUpdateStrategy<TemplateDetailTemplate>{

	@Resource
	NormalOperateDao nDao;
	
	@Resource
	TemplateService tService;
	
	@Resource
	DictionaryDao dictDao;
	
	@Override
	public void update(TemplateDetailTemplate template) {
		TemplateDetailTemplate origin = tService.getDetailTemplate(template.getId());
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
			Map<Long, TemplateDetailFieldGroup> originGroupMap = CollectionUtils.toMap(origin.getGroups(), group->group.getId());
			for (TemplateDetailFieldGroup group : template.getGroups()) {
				if(group.getId() != null){
					if(originGroupMap.containsKey(group.getId())){
						TemplateDetailFieldGroup originGroup = originGroupMap.get(group.getId());
						toDeleteGroupId.remove(group.getId());
						originGroup.setTitle(group.getTitle());
						originGroup.setOrder(group.getOrder());
						originGroup.setUpdateTime(now);
						nDao.update(originGroup);
						
						Map<Long, TemplateDetailField> originFieldMap = CollectionUtils.toMap(originGroup.getFields(), field->field.getId());
						for (TemplateDetailField field : group.getFields()) {
							if(field.getId() != null){
								if(originFieldMap.containsKey(field.getId())){
									toDeleteFieldId.remove(field.getId());
									TemplateDetailField originField = originFieldMap.get(field.getId());
									originField.setTitle(field.getTitle());
									originField.setColNum(field.getColNum());
									originField.setOrder(field.getOrder());
									originField.setViewValue(field.getViewValue());
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
									nDao.save(field);
								}else{
									throw new RuntimeException("找不到fieldId为[" + field.getFieldId() + "]的字段");
								}
							}
						}
					}else{
						throw new RuntimeException("模板[id=" + origin.getId() + "]不能修改字段组[id=" + group.getId() + "，因为字段组不存在，可能是所在模板已经被修改]");
					}
				}else{
					group.setTmplId(origin.getId());
					group.setUpdateTime(now);
					Long groupId = nDao.save(group);
					for (TemplateDetailField field : group.getFields()) {
						if(fieldMap.containsKey(field.getFieldId())){
							field.setFieldName(fieldMap.get(field.getFieldId()).getFullKey());
							field.setGroupId(groupId);
							field.setUpdateTime(now);
							nDao.save(field);
						}else{
							throw new RuntimeException("找不到fieldId为[" + field.getFieldId() + "]的字段");
						}
					}
				}
			}
			nDao.remove(TemplateDetailFieldGroup.class, toDeleteGroupId);
			nDao.remove(TemplateDetailField.class, toDeleteFieldId);
		}else{
			throw new RuntimeException("找不到id为[" + template.getId() + "]的模板，无法更新");
		}
	}

	@Override
	public Long create(TemplateDetailTemplate template) {
		Set<Long> fieldIds = new HashSet<Long>();
		template.getGroups().forEach(group->group.getFields().forEach(field->fieldIds.add(field.getFieldId())));
		Map<Long, DictionaryField> fieldMap = dictDao.getFieldMap(fieldIds);
		Date now = new Date();
		template.setCreateTime(now);
		template.setUpdateTime(now);
		Long tmplId = nDao.save(template);
		for (TemplateDetailFieldGroup group : template.getGroups()) {
			group.setTmplId(tmplId);
			group.setUpdateTime(now);
			Long groupId = nDao.save(group);
			for (TemplateDetailField field : group.getFields()) {
				if(fieldMap.containsKey(field.getFieldId())){
					field.setFieldName(fieldMap.get(field.getFieldId()).getFullKey());
					field.setGroupId(groupId);
					field.setUpdateTime(now);
					nDao.save(field);
				}else{
					throw new RuntimeException("找不到fieldId为[" + field.getFieldId() + "]的字段");
				}
			}
		}
		return tmplId;
	}


}
