package cn.sowell.dataserver.model.tmpl.manager.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.Assert;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.FieldConfigure;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.RelationFieldConfigure;
import cn.sowell.dataserver.model.cachable.manager.AbstractModuleCacheManager;
import cn.sowell.dataserver.model.cachable.prepare.ModuleTemplateReferData;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.tmpl.dao.OpenDetailTemplateDao;
import cn.sowell.dataserver.model.tmpl.manager.TemplateGroupManager;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToDetailTemplate;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToDetailTemplate.PreparedToDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;

public abstract class AbstractDetailTemplateManager<DT extends AbstractDetailTemplate<GT, FT>, GT extends AbstractDetailFieldGroup<FT>, FT extends AbstractDetailField, Dao extends OpenDetailTemplateDao<DT, GT, FT>>
	extends AbstractModuleCacheManager<DT, Dao, GlobalPreparedToDetailTemplate<GT, FT>, PreparedToDetailTemplate<GT, FT>> {

	final DictionaryService dictService;
	
	final TemplateGroupManager tmplGroupManager;
	

	public AbstractDetailTemplateManager(Dao dao,
			ModuleCachableMetaSupportor metaSupportor, DictionaryService dictService,
			TemplateGroupManager tmplGroupManager) {
		super(dao, metaSupportor);
		Assert.notNull(dictService);
		Assert.notNull(tmplGroupManager);
		this.dictService = dictService;
		this.tmplGroupManager = tmplGroupManager;
	}

	@Override
	protected GlobalPreparedToDetailTemplate<GT, FT> getGlobalPreparedToCache() {
		Map<Long, List<GT>> tmplFieldGroupsMap = 
				CollectionUtils.toListMap(getDao().queryFieldGroups(), group->group.getTmplId());
		Map<Long, List<FT>> groupFieldsMap = 
				CollectionUtils.toListMap(getDao().queryTemplateFields(), field->field.getGroupId());
		GlobalPreparedToDetailTemplate<GT, FT> gp = new GlobalPreparedToDetailTemplate<GT, FT>();
		gp.setTmplFieldGroupsMap(tmplFieldGroupsMap);
		gp.setGroupFieldsMap(groupFieldsMap);
		return gp;
	}

	@Override
	protected PreparedToDetailTemplate<GT, FT> extractPrepare(GlobalPreparedToDetailTemplate<GT, FT> globalPreparedToCache, DT cachable) {
		Map<Long, List<GT>> tmplFieldGroupsMap = globalPreparedToCache.getTmplFieldGroupsMap();
		Map<Long, List<FT>> groupFieldsMap = globalPreparedToCache.getGroupFieldsMap();
		PreparedToDetailTemplate<GT, FT> prepared = new PreparedToDetailTemplate<GT, FT>();
		prepared.setFieldGroups(tmplFieldGroupsMap.get(cachable.getId()));
		prepared.setGroupFieldsMap(groupFieldsMap);
		return prepared;
	}
	
	@Override
	protected PreparedToDetailTemplate<GT, FT> getPreparedToCache(DT dtmpl) {
		PreparedToDetailTemplate<GT, FT> prepared = new PreparedToDetailTemplate<GT, FT>();
		List<GT> fieldGroups = getDao().getTemplateGroups(dtmpl.getId());
		Map<Long, List<FT>> groupFieldsMap = getDao().getTemplateFieldsMap(CollectionUtils.toSet(fieldGroups, group->group.getId()));
		prepared.setFieldGroups(fieldGroups);
		prepared.setGroupFieldsMap(groupFieldsMap);
		return prepared;
	}

	@Override
	protected void handlerCache(DT dtmpl, PreparedToDetailTemplate<GT, FT> prepareToCache) {
		ModuleTemplateReferData referData = prepareToCache.getReferData();
		FusionContextConfig config = referData.getFusionContextConfig();
		FusionContextConfigResolver resolver = config.getConfigResolver();
		List<GT> fieldGroups = (List<GT>) prepareToCache.getFieldGroups();
		Map<Long, List<FT>> groupFieldsMap = prepareToCache.getGroupFieldsMap();
		if(fieldGroups != null) {
			dtmpl.setGroups(fieldGroups);
			Map<Long, DictionaryComposite> compositeMap = referData.getCompositeMap();
			Map<Long, DictionaryField> fieldMap = referData.getFieldMap();
			boolean moduleEntityWritable = referData.getEntityWriatble();
			fieldGroups.forEach(fieldGroup->{
				List<FT> groupFields = groupFieldsMap.get(fieldGroup.getId());
				if(groupFields != null) {
					fieldGroup.setFields(groupFields);
				}
				if(fieldGroup.getCompositeId() != null) {
					DictionaryComposite composite = compositeMap.get(fieldGroup.getCompositeId());
					fieldGroup.setComposite(composite);
					if(composite != null) {
						fieldGroup.setRelationLabelAccess(getMetaSupportor().getRelationLabelAccess(composite, referData.getEntityWriatble()));
						fieldGroup.setAdditionRelationLabelAccess(getMetaSupportor().getAdditionRelationLabelAccess(composite, referData.getEntityWriatble()));
					}
				}
				if(groupFields != null) {
					groupFields.forEach(groupField->{
						//通过fieldId来获得对应的字段数据
						DictionaryField field = fieldMap.get(groupField.getFieldId());
						if(field != null) {
							groupField.setFieldAccess(getMetaSupportor().getFieldAccess(field, moduleEntityWritable));
							groupField.setAdditionAccess(getMetaSupportor().getFieldAdditionAccess(field, moduleEntityWritable));
							groupField.setFieldName(field.getFullKey());
							groupField.setType(field.getType());
							groupField.setOptionGroupId(field.getOptionGroupId());
							if(field.getCasLevel() != null) {
								groupField.setOptionGroupKey(field.getOptionGroupId() + "@" + field.getCasLevel());
							}else {
								groupField.setOptionGroupKey(FormatUtils.toString(field.getOptionGroupId()));
							}
						}else {
							groupField.setFieldUnavailable();
						}
					});
					
					//设置数组关联的label选项
					if(Integer.valueOf(1).equals(fieldGroup.getIsArray())) {
						DictionaryComposite composite = fieldGroup.getComposite();
						if(composite != null && TextUtils.hasText(composite.getName()) && composite.getRelationSubdomain() == null) {
							FieldConfigure conf = resolver.getFieldConfigure(composite.getName());
							if(conf instanceof RelationFieldConfigure) {
								composite.setRelationSubdomain(((RelationFieldConfigure) conf).getLabelDomain());
							}
						}
					}
				}
				
				handleFieldGroup(fieldGroup, prepareToCache);
				
			});
		}
	}
	
	
	protected void handleFieldGroup(GT fieldGroup, PreparedToDetailTemplate<GT, FT> prepareToCache) {
	}


	@Override
	protected Long doCreate(DT template) {
		Set<Long> fieldIds = new HashSet<Long>();
		template.getGroups().forEach(group->group.getFields().forEach(field->fieldIds.add(field.getFieldId())));
		Map<Long, DictionaryField> fieldMap = dictService.getFieldMap(template.getModule(), fieldIds);
		Date now = new Date();
		template.setCreateTime(now);
		template.setUpdateTime(now);
		Long tmplId = getDao().getNormalOperateDao().save(template);
		for (GT group : template.getGroups()) {
			group.setTmplId(tmplId);
			group.setUpdateTime(now);
			Long groupId = getDao().getNormalOperateDao().save(group);
			for (FT field : group.getFields()) {
				if(fieldMap.containsKey(field.getFieldId())){
					field.setFieldName(fieldMap.get(field.getFieldId()).getFullKey());
					field.setGroupId(groupId);
					field.setUpdateTime(now);
					getDao().getNormalOperateDao().save(field);
				}else{
					throw new RuntimeException("找不到fieldId为[" + field.getFieldId() + "]的字段");
				}
			}
		}
		return tmplId;
	}

	@Override
	protected void doUpdate(DT template) {
		DT origin = get(template.getId());
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
			Map<Long, DictionaryField> fieldMap = dictService.getFieldMap(template.getModule(), addFieldIds);
			
			Date now = new Date();
			origin.setTitle(template.getTitle());
			origin.setUpdateTime(now);
			getDao().getNormalOperateDao().update(origin);
			Set<Long> toDeleteGroupId = CollectionUtils.toSet(origin.getGroups(), group->group.getId());
			Set<Long> toDeleteFieldId = new HashSet<Long>(originGroupFieldIds);
			Map<Long, GT> originGroupMap = CollectionUtils.toMap(origin.getGroups(), group->group.getId());
			for (GT group : template.getGroups()) {
				if(group.getId() != null){
					if(originGroupMap.containsKey(group.getId())){
						GT originGroup = originGroupMap.get(group.getId());
						toDeleteGroupId.remove(group.getId());
						originGroup.setTitle(group.getTitle());
						originGroup.setUnallowedCreate(group.getUnallowedCreate());
						originGroup.setOrder(group.getOrder());
						originGroup.setSelectionTemplateId(group.getSelectionTemplateId());
						originGroup.setUpdateTime(now);
						doUpdateFieldGroup(originGroup, group);
						getDao().getNormalOperateDao().update(originGroup);
						
						Map<Long, FT> originFieldMap = CollectionUtils.toMap(originGroup.getFields(), field->field.getId());
						for (FT field : group.getFields()) {
							if(field.getId() != null){
								if(originFieldMap.containsKey(field.getId())){
									toDeleteFieldId.remove(field.getId());
									FT originField = originFieldMap.get(field.getId());
									originField.setTitle(field.getTitle());
									originField.setColNum(field.getColNum());
									originField.setOrder(field.getOrder());
									originField.setViewValue(field.getViewValue());
									originField.setValidators(field.getValidators());
									originField.setUpdateTime(now);
									getDao().getNormalOperateDao().update(originField);
								}else{
									throw new RuntimeException("字段组[id=" + group.getId() + "]不能修改字段[id=" + field.getId() + "，因为字段不存在，可能是所在模板已经被修改]");
								}
							}else{
								if(fieldMap.containsKey(field.getFieldId())){
									field.setFieldName(fieldMap.get(field.getFieldId()).getFullKey());
									field.setGroupId(group.getId());
									field.setUpdateTime(now);
									getDao().getNormalOperateDao().save(field);
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
					Long groupId = getDao().getNormalOperateDao().save(group);
					for (FT field : group.getFields()) {
						if(fieldMap.containsKey(field.getFieldId())){
							field.setFieldName(fieldMap.get(field.getFieldId()).getFullKey());
							field.setGroupId(groupId);
							field.setUpdateTime(now);
							getDao().getNormalOperateDao().save(field);
						}else{
							throw new RuntimeException("找不到fieldId为[" + field.getFieldId() + "]的字段");
						}
					}
				}
			}
			getDao().getNormalOperateDao().remove(TemplateDetailFieldGroup.class, toDeleteGroupId);
			getDao().getNormalOperateDao().remove(TemplateDetailField.class, toDeleteFieldId);
		}else{
			throw new RuntimeException("找不到id为[" + template.getId() + "]的模板，无法更新");
		}
	}
	
	protected void doUpdateFieldGroup(GT originGroup, GT group) {}
}
