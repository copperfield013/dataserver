package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.ImportCompositeField;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.ABCNodeProxy;
import cn.sowell.datacenter.entityResolver.impl.ArrayItemPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.RelationEntityProxy;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionArrayEntity;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionArrayEntityField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupAction;
import cn.sowell.dataserver.model.tmpl.service.ActionTemplateService;
import cn.sowell.dataserver.model.tmpl.service.TemplateService;

@Service
public class ActionTemplateServiceImpl implements ActionTemplateService{

	@Resource
	ModulesService mService;
	
	@Resource
	TemplateService tService;
	
	static Logger logger = Logger.getLogger(ActionTemplateServiceImpl.class);
	
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public int doAction(TemplateActionTemplate atmpl, Set<String> codes, boolean isTransaction,
			UserIdentifier currentUser) {
		int sucs = 0;
		for (String code : codes) {
			try {
				ModuleEntityPropertyParser entity = mService.getEntity(atmpl.getModule(), code, null, currentUser);
				Map<String, Object> fieldValueMap = generateFieldValueMap(entity, atmpl);
				fieldValueMap.put(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL, code);
				mService.mergeEntity(atmpl.getModule(), fieldValueMap, currentUser);
				if(!isTransaction) {
					//如果是非事务型，那么每修改一个实体都提交一次
					TransactionAspectSupport.currentTransactionStatus().flush();
				}
				sucs++;
			} catch (Exception e) {
				if(isTransaction) {
					throw new RuntimeException("操作实体[code=" + code + "]时发生错误", e);
				}else {
					logger.error("操作实体[code=" + code + "]时发生错误", e);
				}
			}
		}
		if(sucs == 0) {
			throw new RuntimeException("对实体[codes=" + codes + "]执行多选操作[id=" + atmpl.getId() + ", title=" + atmpl.getTitle() + "]全部失败");
		}
		return sucs;
	}


	private Map<String, Object> generateFieldValueMap(ModuleEntityPropertyParser existEntity, TemplateActionTemplate atmpl) {
		List<TemplateActionFieldGroup> groups = atmpl.getGroups();
		Map<String, Object> entityMap = new HashMap<String, Object>();
		if(groups != null) {
			for (TemplateActionFieldGroup group : groups) {
				//普通字段
				if(!Integer.valueOf(1).equals(group.getIsArray())) {
					List<TemplateActionField> actFields = group.getFields();
					for (TemplateActionField aField : actFields) {
						if(aField.getFieldAvailable()) {
							entityMap.put(aField.getFieldName(), aField.getViewValue());
						}
					}
				}else {
					//一对多的字段
					List<TemplateActionArrayEntity> entities = group.getEntities();
					if(entities != null && !entities.isEmpty()) {
						DictionaryComposite composite = group.getComposite();
						if(composite != null) {
							boolean isRelation = DictionaryComposite.RELATION_ADD_TYPE.equals(composite.getAddType());
							Map<Long, DictionaryField> fieldMap = CollectionUtils.toMap(composite.getFields(), DictionaryField::getId);
							int compositeEntityIndex = 0;
							if(existEntity != null) {
								List<ArrayItemPropertyParser> existArrayEntities = existEntity.getCompositeArray(composite.getName());
								//放入原本存在的数组字段
								for (ArrayItemPropertyParser existArrayEntity : existArrayEntities) {
									entityMap.put(existArrayEntity.getCodeName(), existArrayEntity.getCode());
									if(isRelation) {
										String relationLabelKey = composite.getName() + "[" + existArrayEntity.getItemIndex() + "]." + RelationEntityProxy.LABEL_KEY;
										entityMap.put(relationLabelKey, existEntity.getFormatedProperty(relationLabelKey));
									}
								}
								compositeEntityIndex = existArrayEntities.size();
							}
							//将操作模板中的数组字段放入
							for (TemplateActionArrayEntity arrayEntity : entities) {
								if(arrayEntity.getRelationLabel() != null) {
									entityMap.put(composite.getName() + "[" + compositeEntityIndex + "]." + RelationEntityProxy.LABEL_KEY, arrayEntity.getRelationLabel());
									if(TextUtils.hasText(arrayEntity.getRelationEntityCode())) {
										entityMap.put(composite.getName() + "[" + compositeEntityIndex + "]." + ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL, arrayEntity.getRelationEntityCode());
									}
								}
								if(!isRelation || !TextUtils.hasText(arrayEntity.getRelationEntityCode())) {
									List<TemplateActionArrayEntityField> eFields = arrayEntity.getFields();
									for (TemplateActionArrayEntityField eField : eFields) {
										DictionaryField field = fieldMap.get(eField.getFieldId());
										if(field != null) {
											String fieldName = field.getFieldPattern().replaceFirst(ImportCompositeField.REPLACE_INDEX, String.valueOf(compositeEntityIndex));
											entityMap.put(fieldName, eField.getValue());
										}
									}
								}
								compositeEntityIndex++;
							}
						}
					}
				}
			}
		}
		entityMap.remove(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL);
		return entityMap;
	}
	
	@Override
	public void coverActionFields(TemplateGroupAction groupAction, Map<String, Object> map) {
		TemplateActionTemplate atmpl = tService.getActionTemplate(groupAction.getAtmplId());
		map.putAll(generateFieldValueMap(null, atmpl));
	}

}
