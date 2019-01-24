package cn.sowell.dataserver.model.tmpl.manager.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.abc.service.ABCExecuteService;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.tmpl.dao.ActionTemplateDao;
import cn.sowell.dataserver.model.tmpl.manager.ActionTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.TemplateGroupManager;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToDetailTemplate;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToDetailTemplate.PreparedToDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.ArrayEntityProxy;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionArrayEntity;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionArrayEntityField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionTemplate;

@Component
public class ActionTemplateManagerImpl 
	extends AbstractDetailTemplateManager<TemplateActionTemplate, TemplateActionFieldGroup, TemplateActionField, ActionTemplateDao>
	implements ActionTemplateManager{

	@Resource
	ABCExecuteService abcService;
	
	@Autowired
	public ActionTemplateManagerImpl(
			@Autowired ActionTemplateDao dao,
			@Autowired ModuleCachableMetaSupportor metaSupportor, 
			@Autowired DictionaryService dictService,
			@Autowired TemplateGroupManager tmplGroupManager) {
		super(dao, metaSupportor, dictService, tmplGroupManager);
	}

	@Override
	protected TemplateActionTemplate createCachablePojo() {
		return new TemplateActionTemplate();
	}
	
	@Override
	protected GlobalPreparedToDetailTemplate<TemplateActionFieldGroup, TemplateActionField> getGlobalPreparedToCache() {
		GlobalPreparedToDetailTemplate<TemplateActionFieldGroup, TemplateActionField> gp = super.getGlobalPreparedToCache();
		List<TemplateActionArrayEntity> arrayEntities = getDao().queryArrayEntities();
		List<TemplateActionArrayEntityField> eFields = getDao().queryArrayEntityFields();
		
		Map<Long, List<TemplateActionArrayEntity>> entitiesMap = CollectionUtils.toListMap(arrayEntities , TemplateActionArrayEntity::getTmplFieldGroupId);
		Map<Long, List<TemplateActionArrayEntityField>> arrayEntityFieldsMap = CollectionUtils.toListMap(eFields, TemplateActionArrayEntityField::getActionArrayEntityId);
		gp.set("entitiesMap", entitiesMap);
		gp.set("arrayEntityFieldsMap", arrayEntityFieldsMap);
		return gp;
	}
	
	@Override
	protected PreparedToDetailTemplate<TemplateActionFieldGroup, TemplateActionField> extractPrepare(
			GlobalPreparedToDetailTemplate<TemplateActionFieldGroup, TemplateActionField> globalPreparedToCache,
			TemplateActionTemplate cachable) {
		PreparedToDetailTemplate<TemplateActionFieldGroup, TemplateActionField> prepared = super.extractPrepare(globalPreparedToCache, cachable);
		prepared.set("entitiesMap", globalPreparedToCache.get("entitiesMap"));
		prepared.set("arrayEntityFieldsMap", globalPreparedToCache.get("arrayEntityFieldsMap"));
		return prepared;
	}
	
	@Override
	protected PreparedToDetailTemplate<TemplateActionFieldGroup, TemplateActionField> getPreparedToCache(
			TemplateActionTemplate atmpl) {
		PreparedToDetailTemplate<TemplateActionFieldGroup, TemplateActionField> prepared = super.getPreparedToCache(atmpl);
		List<TemplateActionArrayEntity> arrayEntities = getDao().queryArrayEntities(atmpl.getId());
		Map<Long, List<TemplateActionArrayEntity>> entitiesMap = CollectionUtils.toListMap(arrayEntities , TemplateActionArrayEntity::getTmplFieldGroupId);
		
		Map<Long, List<TemplateActionArrayEntityField>> arrayEntityFieldsMap = getDao().queryArrayEntityFields(CollectionUtils.toSet(arrayEntities, TemplateActionArrayEntity::getId));
		
		prepared.set("entitiesMap", entitiesMap);
		prepared.set("arrayEntityFieldsMap", arrayEntityFieldsMap);
		return prepared;
	}
	
	@Override
	protected void handleFieldGroup(TemplateActionFieldGroup fieldGroup,
			PreparedToDetailTemplate<TemplateActionFieldGroup, TemplateActionField> prepareToCache) {
		@SuppressWarnings("unchecked")
		Map<Long, List<TemplateActionArrayEntity>> entitiesMap = (Map<Long, List<TemplateActionArrayEntity>>) prepareToCache.get("entitiesMap");
		@SuppressWarnings("unchecked")
		Map<Long, List<TemplateActionArrayEntityField>> arrayEntityFieldsMap = (Map<Long, List<TemplateActionArrayEntityField>>) prepareToCache.get("arrayEntityFieldsMap");
		List<TemplateActionArrayEntity> entities = entitiesMap.get(fieldGroup.getId());
		
		if(entities != null) {
			entities.forEach(entity->{
				if(TextUtils.hasText(entity.getRelationEntityCode())) {
					ArrayEntityProxy arrayEntityProxy = createArrayEntityProxy(prepareToCache.getReferData().getModuleName(), fieldGroup.getComposite().getName(), entity.getRelationEntityCode());
					entity.setArrayEntityProxy(arrayEntityProxy);
				}
				List<TemplateActionArrayEntityField> eFields = arrayEntityFieldsMap.get(entity.getId());
				if(eFields != null) {
					entity.setFields(eFields);
					for (TemplateActionArrayEntityField eField : eFields) {
						for (TemplateActionField field : fieldGroup.getFields()) {
							if(field.getId().equals(eField.getTmplFieldId())){
								field.getArrayEntityFields().add(eField);
								eField.setFieldId(field.getFieldId());
								eField.setFieldName(field.getFieldName());
								eField.setArrayEntity(entity);
								break;
							}
								
						}
					}
				}
			});
			fieldGroup.setEntities(entities);
		}
	}
	
	@Override
	protected void afterReloadCache(TemplateActionTemplate atmpl) {
		tmplGroupManager.updateActionTemplateRelatedGroups(atmpl.getId());
	}
	
	private ArrayEntityProxy createArrayEntityProxy(String moduleName, String relationName,
			String relationEntityCode) {
		return new ArrayEntityProxy(abcService, moduleName, relationName, relationEntityCode);
	}

}
