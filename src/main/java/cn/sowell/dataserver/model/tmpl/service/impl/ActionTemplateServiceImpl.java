package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.EntityConstants;
import cn.sowell.datacenter.entityResolver.ImportCompositeField;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.ABCNodeProxy;
import cn.sowell.datacenter.entityResolver.impl.AbstractFusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.impl.ArrayItemPropertyParser;
import cn.sowell.dataserver.model.abc.service.EntityQueryParameter;
import cn.sowell.dataserver.model.abc.service.ModuleEntityService;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.tmpl.duplicator.impl.ActionTemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.manager.ActionTemplateManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionArrayEntity;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionArrayEntityField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupAction;
import cn.sowell.dataserver.model.tmpl.service.ActionTemplateService;
import cn.sowell.dataserver.model.tmpl.service.TemplateGroupService;

@Service
public class ActionTemplateServiceImpl extends AbstractRelateToGroupService<TemplateActionTemplate, ActionTemplateManager> implements ActionTemplateService{

	@Resource
	ActionTemplateDuplicator duplicator;
	
	@Autowired
	protected ActionTemplateServiceImpl(
			@Autowired ActionTemplateManager manager, 
			@Autowired TemplateGroupService tmplGroupService) {
		super(manager, tmplGroupService);
	}

	@Resource
	ModuleEntityService entityService;
	
	static Logger logger = Logger.getLogger(ActionTemplateServiceImpl.class);
	
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public int doAction(TemplateActionTemplate atmpl, Set<String> codes, boolean isTransaction,
			UserIdentifier currentUser) {
		int sucs = 0;
		for (String code : codes) {
			try {
				EntityQueryParameter param = new EntityQueryParameter(atmpl.getModule(), code, currentUser);
				ModuleEntityPropertyParser entity = entityService.getEntityParser(param);
				//ModuleEntityPropertyParser entity = mService.getEntity(atmpl.getModule(), code, null, currentUser);
				Map<String, Object> fieldValueMap = extendsFieldValueMap(entity, null, atmpl);
				fieldValueMap.put(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL, code);
				entityService.mergeEntity(param, fieldValueMap);
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


	private Map<String, Object> extendsFieldValueMap(ModuleEntityPropertyParser existEntity, Map<String, Object> sourceMap, TemplateActionTemplate atmpl) {
		List<TemplateActionFieldGroup> groups = atmpl.getGroups();
		Map<String, Object> entityMap = new HashMap<>();
		if(sourceMap != null) {
			entityMap.putAll(sourceMap);
		}
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
							
							//如果sourceMap中传入了该composite的数据，那么舍弃existEntity中的composite的所有值，
							//用sourceMap中的值为基础，atmpl中的值为覆盖
							List<String> sourceMapCompositeCodes = calculateCompositeLength(sourceMap, composite.getName());
							if(sourceMapCompositeCodes != null) {
								//传入的composite
								compositeEntityIndex = sourceMapCompositeCodes.size();
							}else {
								if(existEntity != null) {
									List<ArrayItemPropertyParser> existArrayEntities = existEntity.getCompositeArray(composite.getName());
									//放入原本存在的数组字段
									for (ArrayItemPropertyParser existArrayEntity : existArrayEntities) {
										entityMap.put(existArrayEntity.getCodeName(), existArrayEntity.getCode());
										if(isRelation) {
											String relationLabelKey = composite.getName() + "[" + existArrayEntity.getItemIndex() + "]." + EntityConstants.LABEL_KEY;
											entityMap.put(relationLabelKey, existEntity.getFormatedProperty(relationLabelKey));
										}
									}
									compositeEntityIndex = existArrayEntities.size();
								}
							}
							//将操作模板中的数组字段放入
							for (TemplateActionArrayEntity arrayEntity : entities) {
								int thisEntityIndex = compositeEntityIndex;
								if(arrayEntity.getRelationLabel() != null) {
									if(TextUtils.hasText(arrayEntity.getRelationEntityCode())) {
										if(sourceMapCompositeCodes != null) {
											int sourceMapCodeIndex = sourceMapCompositeCodes.indexOf(arrayEntity.getRelationEntityCode());
											if(sourceMapCodeIndex >= 0) {
												//在sourceMap中存在一样code的实体，进行覆盖
												thisEntityIndex = sourceMapCodeIndex;
											}
										}
										entityMap.put(composite.getName() + "[" + thisEntityIndex + "]." + ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL, arrayEntity.getRelationEntityCode());
									}
									//放入关系名称
									entityMap.put(composite.getName() + "[" + thisEntityIndex + "]." + EntityConstants.LABEL_KEY, arrayEntity.getRelationLabel());
								}
								if(!isRelation || !TextUtils.hasText(arrayEntity.getRelationEntityCode())) {
									List<TemplateActionArrayEntityField> eFields = arrayEntity.getFields();
									for (TemplateActionArrayEntityField eField : eFields) {
										DictionaryField field = fieldMap.get(eField.getFieldId());
										if(field != null) {
											String fieldName = field.getFieldPattern().replaceFirst(ImportCompositeField.REPLACE_INDEX, String.valueOf(thisEntityIndex));
											entityMap.put(fieldName, eField.getValue());
										}
									}
								}
								if(thisEntityIndex == compositeEntityIndex) {
									compositeEntityIndex++;
								}
							}
						}
					}
				}
			}
		}
		entityMap.remove(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL);
		return entityMap;
	}
	
	private List<String> calculateCompositeLength(Map<String, Object> sourceMap, String compositeName) {
		if(sourceMap != null) {
			if(sourceMap.containsKey(compositeName + AbstractFusionContextConfigResolver.PROP_FLAG)) {
				TreeMap<Integer, String> codeIndexMap = new TreeMap<>();
				Pattern regex = Pattern.compile("^\\[(\\d+)\\]$");
				sourceMap.entrySet().stream()
					.filter(entry->entry.getKey().startsWith(compositeName + '[') || entry.getKey().startsWith(compositeName + "."))
					.forEach(entry->{
						String fieldName = entry.getKey();
						if(fieldName.endsWith("." + ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL)) {
							fieldName = fieldName.substring(compositeName.length());
							fieldName = fieldName.substring(0, fieldName.length() - ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL.length() - 1);
							if(fieldName.isEmpty()) {
								codeIndexMap.put(0, FormatUtils.toString(entry.getValue()));
							}else {
								Matcher matcher = regex.matcher(fieldName);
								if(matcher.matches()) {
									codeIndexMap.put(Integer.valueOf(matcher.group(1)), FormatUtils.toString(entry.getValue()));
								}
							}
						}
					});
				List<String> codes = new ArrayList<>();
				if(codeIndexMap.size() > 0) {
					int bigestKey = codeIndexMap.lastEntry().getKey();
					for (int i = 0; i <= bigestKey; i++) {
						codes.add(codeIndexMap.get(i));
					}
				}
				return codes;
			}
		}
		return null;
	}


	@Override
	public Map<String, Object> coverActionFields(TemplateGroupAction groupAction, Map<String, Object> map) {
		TemplateActionTemplate atmpl = getTemplate(groupAction.getAtmplId());
		Map<String, Object> entityMap = extendsFieldValueMap(null, map, atmpl);
		if(TextUtils.hasText((String) map.get(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL))) {
			entityMap.put(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL, map.get(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL));
		}
		return entityMap;
	}


	@Override
	public void switchAllRelatedGroups(Long tmplId, Long targetTmplId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Long copy(Long atmplId, String targetModuleName) {
		Long newTmplId = duplicator.copy(atmplId, targetModuleName);
		return newTmplId;
	}


	@Override
	protected boolean isRelatedGroup(Long tmplId, TemplateGroup tmplGroup) {
		List<TemplateGroupAction> groupActions = tmplGroup.getActions();
		if(groupActions != null) {
			return groupActions.stream().anyMatch(groupAction->tmplId.equals(groupAction.getAtmplId()));
		}
		return false;
	}

}
