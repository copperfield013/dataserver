package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;

import com.abc.mapping.node.NodeOpsType;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.Composite;
import cn.sowell.datacenter.entityResolver.FieldConfigure;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.RelationFieldConfigure;
import cn.sowell.datacenter.entityResolver.config.UnconfiuredFusionException;
import cn.sowell.dataserver.model.abc.service.ABCExecuteService;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.tmpl.dao.ActionTemplateDao;
import cn.sowell.dataserver.model.tmpl.dao.DetailTemplateDao;
import cn.sowell.dataserver.model.tmpl.dao.ListTemplateDao;
import cn.sowell.dataserver.model.tmpl.dao.SelectionTemplateDao;
import cn.sowell.dataserver.model.tmpl.dao.TemplateGroupDao;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.ArrayEntityProxy;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionArrayEntity;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionArrayEntityField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupAction;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupPremise;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;
import cn.sowell.dataserver.model.tmpl.service.TemplateService;
import cn.sowell.dataserver.model.tmpl.strategy.NormalDaoSetUpdateStrategy;
import cn.sowell.dataserver.model.tmpl.strategy.TemplateUpdateStrategy;
import cn.sowell.dataserver.model.tmpl.strategy.TemplateUpdateStrategyFactory;

@Service
public class TemplateServiceImpl implements TemplateService, InitializingBean{

	@Resource
	NormalOperateDao nDao;
	
	@Resource
	ListTemplateDao lDao;
	
	@Resource
	DetailTemplateDao dDao;
	
	@Resource
	TemplateGroupDao gDao;
	
	@Resource
	SelectionTemplateDao sDao;
	
	@Resource
	ActionTemplateDao aDao;

	@Resource
	DictionaryService dictService;
	
	@Resource
	ModulesService mService;
	
	@Resource
	TemplateUpdateStrategyFactory tmplUpdateStrategyFactory;
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	@Resource
	ABCExecuteService abcService;
	
	Map<Long, TemplateGroup> tmplGroupMap;
	
	Map<Long, TemplateGroupAction> groupActionMap = new HashMap<>();
	
	Map<Long, TemplateDetailTemplate> dtmplMap;
	
	Map<Long, TemplateListTemplate> ltmplMap;
	
	Map<Long, TemplateSelectionTemplate> stmplMap;
	
	Map<Long, TemplateActionTemplate> atmplMap;
	
	
	
	//关联到列表模板的模板组合id集合
	//Map<Long, Set<Long>> ltmplGroupIdMap;
	
	//关联到详情模板的模板组合id集合
	//Map<Long, Set<Long>> dtmplGroupIdMap;
	
	Logger logger = Logger.getLogger(TemplateServiceImpl.class);

	

	@Override
	public void afterPropertiesSet() throws Exception {
	}
	
	void reloadListTemplate(Long ltmplId) {
		if(ltmplMap != null) {
			synchronized (ltmplMap) {
				logger.debug("重新加载列表模板[id=" + ltmplId + "]缓存数据...");
				TemplateListTemplate ltmpl = nDao.get(TemplateListTemplate.class, ltmplId);
				if(ltmpl != null && checkModuleUsable(ltmpl.getModule())) {
					List<TemplateListColumn> columns = lDao.getColumnsByTmplId(ltmplId);
					Set<TemplateListCriteria> criterias = lDao.getCriteriaByTmplId(ltmplId);
					handlerListTemplate(ltmpl, columns, criterias, getModuleTemplateReferData(ltmpl.getModule()));
					ltmplMap.put(ltmplId, ltmpl);
					getListTemplateRelatedGroups(ltmplId).forEach(group->{
						handlerTmplGroup(group, null, null);
					});
					logger.debug("列表模板[" + ltmplId + "]缓存数据重新加载完成, 值为" + ltmpl);
				}else {
					ltmplMap.remove(ltmplId);
					logger.debug("从缓存中移除列表模板[id=" + ltmplId + "]");
				}
			}
		}
	}
	

	private boolean checkModuleUsable(String moduleName) {
		try {
			return fFactory.getModuleConfig(moduleName) != null;
		} catch (UnconfiuredFusionException e) {
			return false;
		}
	}

	private void handlerListTemplate(TemplateListTemplate ltmpl, List<TemplateListColumn> columns,
			Set<TemplateListCriteria> criterias, ModuleTemplateReferData referData) {
		Map<Long, DictionaryField> fieldMap = referData.getFieldMap();
		Map<Long, DictionaryComposite> compositeMap = referData.getCompositeMap();
		if(columns != null) {
			columns.forEach(column->{
				if(column.getSpecialField() == null) {
					DictionaryField field = fieldMap.get(column.getFieldId());
					if(field != null) {
						column.setFieldKey(field.getFullKey());
					}else {
						column.setFieldUnavailable();
					}
				}
			});
			ltmpl.setColumns(columns);
		}
		if(criterias != null) {
			criterias.forEach(criteria->{
				if(criteria.getFieldId() != null) {
					DictionaryField field = fieldMap.get(criteria.getFieldId());
					if(field != null) {
						if(supportFieldInputType(criteria.getInputType(), field.getType(), referData.getFieldInputTypeMap())) {
							criteria.setFieldKey(field.getFullKey());
							//只有字段存在并且字段当前类型支持当前条件的表单类型，该条件字段才可用
							//(因为条件的表单类型是创建模板时选择的，与字段类型不同，防止字段修改了类型但与条件表单类型不匹配)
							return;
						}
					}
					criteria.setFieldUnavailable();
				}else if(criteria.getCompositeId() != null) {
					criteria.setComposite(compositeMap.get(criteria.getCompositeId()));
				}
			});
			ltmpl.setCriterias(criterias);
		}
	}

	
	
	private boolean supportFieldInputType(String criteriaInputType, String fieldType, Map<String, Set<String>> fieldInputTypeMap) {
		Set<String> fieldInputTypeSet = fieldInputTypeMap.get(fieldType);
		if(fieldInputTypeSet != null) {
			return fieldInputTypeSet.contains(criteriaInputType);
		}
		return false;
	}

	Map<Long, TemplateListTemplate> getListTemplateMap(){
		synchronized (this) {
			if(ltmplMap == null) {
				logger.debug("开始初始化所有列表模板缓存数据...");
				List<TemplateListTemplate> ltmpls = lDao.queryTemplates();
				Map<Long, List<TemplateListColumn>> columnsMap = lDao.queryColumnsMap();
				Map<Long, Set<TemplateListCriteria>> criteriasMap = lDao.queryCriteriasMap();
				ltmpls = ltmpls.stream().filter(ltmpl->checkModuleUsable(ltmpl.getModule())).collect(Collectors.toList());
				Map<String, List<TemplateListTemplate>> moduleTmplMap = CollectionUtils.toListMap(ltmpls, TemplateListTemplate::getModule);
				moduleTmplMap.forEach((module, moduleLtmpls)->{
					ModuleTemplateReferData referData = getModuleTemplateReferData(module);
					for (TemplateListTemplate ltmpl : moduleLtmpls) {
						List<TemplateListColumn> columns = columnsMap.get(ltmpl.getId());
						Set<TemplateListCriteria> criterias = criteriasMap.get(ltmpl.getId());
						handlerListTemplate(ltmpl, columns, criterias, referData);
					}
				});
				ltmplMap = CollectionUtils.toMap(ltmpls, ltmpl->ltmpl.getId());
				logger.debug(ltmplMap);
				logger.debug("初始化列表模板缓存数据完成，共缓存" + ltmplMap.size() + "个列表模板");
			}
			return ltmplMap;
		}
	}
	
	
	void reloadDetailTemplate(Long dtmplId) {
		if(dtmplMap != null) {
			synchronized (dtmplMap) {
				logger.debug("重新加载详情模板[id=" + dtmplId + "]缓存数据...");
				TemplateDetailTemplate dtmpl = nDao.get(TemplateDetailTemplate.class, dtmplId);
				if(dtmpl != null && checkModuleUsable(dtmpl.getModule())) {
					List<TemplateDetailFieldGroup> fieldGroups = dDao.getTemplateGroups(dtmplId);
					Map<Long, List<TemplateDetailField>> groupFieldsMap = dDao.getTemplateFieldsMap(CollectionUtils.toSet(fieldGroups, group->group.getId()));
					handlerDetailTemplate(dtmpl, fieldGroups, groupFieldsMap, getModuleTemplateReferData(dtmpl.getModule()));
					dtmplMap.put(dtmplId, dtmpl);
					getDetailTemplateRelatedGroups(dtmplId).forEach(group->{
						handlerTmplGroup(group, null, null);
					});
					logger.debug("列表模板[" + dtmplId + "]缓存数据重新加载完成, 值为" + dtmpl);
				}else {
					dtmplMap.remove(dtmplId);
					logger.debug("从缓存数据中移除列表模板[id=" + dtmplId + "]");
				}
			}
		}
	}
	
	
	private ModuleTemplateReferData getModuleTemplateReferData(String module) {
		ModuleTemplateReferData referData = new ModuleTemplateReferData();
		referData.setCompositeMap(FormatUtils.coalesce(CollectionUtils.toMap(dictService.getAllComposites(module), DictionaryComposite::getId), new HashMap<>()));
		referData.setFieldMap(FormatUtils.coalesce(CollectionUtils.toMap(dictService.getAllFields(module), DictionaryField::getId), new HashMap<>()));
		referData.setEntityWritable(mService.getModuleEntityWritable(module));
		referData.setFusionContextConfig(fFactory.getModuleConfig(module));
		referData.setFieldInputTypeMap(dictService.getFieldInputTypeMap());
		return referData;
	}

	private String getFieldAccess(DictionaryField field, boolean moduleEntityWritable) {
		Assert.notNull(field);
		String fAccess = field.getFieldAccess();
		DictionaryComposite composite = field.getComposite();
		final NodeOpsType READ = NodeOpsType.READ;
		if(!moduleEntityWritable) {
			return READ.getName();
		}else {
			if(READ.getName().equals(fAccess) || composite == null) {
				return fAccess;
			}else {
				String cAccess = composite.getAccess();
				if(READ.getName().equals(cAccess)) {
					return cAccess;
				}else if(NodeOpsType.ADD.getName().equals(cAccess)) {
					//为增的话，已有记录的字段为只读
					return READ.getName();
				}else if(NodeOpsType.SUPPLEMENT.getName().equals(cAccess)) {
					//为补的话，已有记录的字段为只读
					return READ.getName();
				}else if(NodeOpsType.MERGE.getName().equals(cAccess)) {
					//为并的话，已有记录的字段根据其配置
					return fAccess;
				}else {
					return fAccess;
				}
			}
		}
	}
	
	private String getFieldAdditionAccess(DictionaryField field, boolean moduleEntityWritable) {
		Assert.notNull(field);
		String fAccess = field.getFieldAccess();
		DictionaryComposite composite = field.getComposite();
		final NodeOpsType READ = NodeOpsType.READ;
		if(!moduleEntityWritable) {
			return READ.getName();
		}
		if(composite != null && READ.getName().equals(composite.getAccess())) {
			return READ.getName();
		}else {
			return fAccess;
		}
	}
	
	private String getAdditionRelationLabelAccess(DictionaryComposite composite, boolean moduleEntityWritable) {
		final String READ = NodeOpsType.READ.getName();
		if(!moduleEntityWritable) {
			return READ;
		}
		if(READ.equals(composite.getAccess()) || NodeOpsType.SUPPLEMENT.getName().equals(composite.getAccess())) {
			return READ;
		}else {
			return composite.getRelationLabelAccess();
		}
	}

	private String getRelationLabelAccess(DictionaryComposite composite, boolean moduleEntityWritable) {
		final String READ = NodeOpsType.READ.getName();
		if(!moduleEntityWritable) {
			return READ;
		}
		if(READ.equals(composite.getAccess()) 
				|| NodeOpsType.ADD.getName().equals(composite.getAccess())
				|| NodeOpsType.SUPPLEMENT.getName().equals(composite.getAccess())) {
			return READ;
		}else {
			return composite.getRelationLabelAccess();
		}
	}
	
	
	static class ModuleTemplateReferData{
		private Map<Long, DictionaryComposite> compositeMap;
		private Map<Long, DictionaryField> fieldMap;
		private boolean entityWritable;
		private FusionContextConfig fusionContextConfig;
		private Map<String, Set<String>> fieldInputTypeMap;
		public Map<Long, DictionaryComposite> getCompositeMap() {
			return this.compositeMap;
		}
		public Map<String, Set<String>> getFieldInputTypeMap() {
			return this.fieldInputTypeMap;
		}
		public Map<Long, DictionaryField> getFieldMap() {
			return this.fieldMap;
		}
		public boolean getEntityWriatble() {
			return this.entityWritable;
		}
		public FusionContextConfig getFusionContextConfig() {
			return this.fusionContextConfig;
		}
		public void setCompositeMap(Map<Long, DictionaryComposite> compositeMap) {
			this.compositeMap = compositeMap;
		}
		public void setFieldMap(Map<Long, DictionaryField> fieldMap) {
			this.fieldMap = fieldMap;
		}
		public void setEntityWritable(boolean entityWritable) {
			this.entityWritable = entityWritable;
		}
		public void setFusionContextConfig(FusionContextConfig fusionContextConfig) {
			this.fusionContextConfig = fusionContextConfig;
		}
		public void setFieldInputTypeMap(Map<String, Set<String>> fieldInputTypeMap) {
			this.fieldInputTypeMap = fieldInputTypeMap;
		}
		
		
	}
	
	private void handlerDetailTemplate(TemplateDetailTemplate dtmpl, List<TemplateDetailFieldGroup> fieldGroups,
			Map<Long, List<TemplateDetailField>> groupFieldsMap, ModuleTemplateReferData referData) {
		FusionContextConfig config = referData.getFusionContextConfig();
		FusionContextConfigResolver resolver = config.getConfigResolver();
		if(fieldGroups != null) {
			dtmpl.setGroups(fieldGroups);
			Map<Long, DictionaryComposite> compositeMap = referData.getCompositeMap();
			Map<Long, DictionaryField> fieldMap = referData.getFieldMap();
			boolean moduleEntityWritable = referData.getEntityWriatble();
			fieldGroups.forEach(fieldGroup->{
				List<TemplateDetailField> groupFields = groupFieldsMap.get(fieldGroup.getId());
				if(groupFields != null) {
					fieldGroup.setFields(groupFields);
				}
				if(fieldGroup.getCompositeId() != null) {
					DictionaryComposite composite = compositeMap.get(fieldGroup.getCompositeId());
					fieldGroup.setComposite(composite);
					if(composite != null) {
						fieldGroup.setRelationLabelAccess(getRelationLabelAccess(composite, referData.getEntityWriatble()));
						fieldGroup.setAdditionRelationLabelAccess(getAdditionRelationLabelAccess(composite, referData.getEntityWriatble()));
					}
				}
				if(groupFields != null) {
					groupFields.forEach(groupField->{
						//通过fieldId来获得对应的字段数据
						DictionaryField field = fieldMap.get(groupField.getFieldId());
						if(field != null) {
							groupField.setFieldAccess(getFieldAccess(field, moduleEntityWritable));
							groupField.setAdditionAccess(getFieldAdditionAccess(field, moduleEntityWritable));
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
			});
		}
	}

	

	

	Map<Long, TemplateDetailTemplate> getDetailTemplateMap(){
		synchronized (this) {
			if(dtmplMap == null) {
				logger.debug("开始初始化所有详情模板缓存数据...");
				List<TemplateDetailTemplate> dtmpls = dDao.queryTemplates().stream().filter(dtmpl->checkModuleUsable(dtmpl.getModule())).collect(Collectors.toList());
				Map<Long, List<TemplateDetailFieldGroup>> tmplFieldGroupsMap = 
							CollectionUtils.toListMap(dDao.queryFieldGroups(), group->group.getTmplId());
				Map<Long, List<TemplateDetailField>> groupFieldsMap = 
							CollectionUtils.toListMap(dDao.queryTemplateFields(), field->field.getGroupId());
				Map<String, List<TemplateDetailTemplate>> moduleTmplMap = CollectionUtils.toListMap(dtmpls, TemplateDetailTemplate::getModule);
				moduleTmplMap.forEach((module, moduleDtmpls)->{
					ModuleTemplateReferData referData = getModuleTemplateReferData(module);
					for (TemplateDetailTemplate dtmpl : moduleDtmpls) {
						List<TemplateDetailFieldGroup> fieldGroups = tmplFieldGroupsMap.get(dtmpl.getId());
						handlerDetailTemplate(dtmpl, fieldGroups, groupFieldsMap, referData);
					}
				});
				dtmplMap = CollectionUtils.toMap(dtmpls, dtmpl->dtmpl.getId());
				logger.debug(dtmplMap);
				logger.debug("初始化详情模板缓存数据完成，共缓存" + dtmplMap.size() + "个详情模板");
			}
			return dtmplMap;
		}
	}
	
	@Override
	public List<TemplateListTemplate> queryListTemplateList(String module, UserIdentifier user) {
		return getListTemplateMap().values().stream()
			.filter(ltmpl->module.equals(ltmpl.getModule()))
			.collect(Collectors.toList())
		;
	}
	
	@Override
	public List<TemplateDetailTemplate> queryDetailTemplates(String module) {
		return getDetailTemplateMap().values().stream()
				.filter(dtmpl->module.equals(dtmpl.getModule()))
				.collect(Collectors.toList())
			;
	}
	
	@Override
	public TemplateDetailTemplate getDetailTemplate(long tmplId) {
		return getDetailTemplateMap().get(tmplId);
	}

	@Override
	public TemplateListTemplate getListTemplate(long tmplId) {
		return getListTemplateMap().get(tmplId);
	}


	@Override
	public void removeDetailTemplate(Long dtmplId) {
		TemplateDetailTemplate dtmpl = new TemplateDetailTemplate();
		dtmpl.setId(dtmplId);
		nDao.remove(dtmpl);
		reloadDetailTemplate(dtmplId);
	}
	
	@Override
	public void removeListTemplate(Long ltmplId) {
		TemplateListTemplate ltmpl = new TemplateListTemplate();
		ltmpl.setId(ltmplId);
		nDao.remove(ltmpl);
		reloadListTemplate(ltmplId);
	}
	
	
	@Override
	@Transactional
	public <T extends AbstractTemplate> Long mergeTemplate(T template) {
		TemplateUpdateStrategy<T> strategy = tmplUpdateStrategyFactory.getStrategy(template);
		Long tmplId = null;
		if(template.getId() != null) {
			strategy.update(template);
			tmplId = template.getId();
		}else {
			tmplId = strategy.create(template);
		}
		TransactionAspectSupport.currentTransactionStatus().flush();
		nDao.clear();
		if(template instanceof TemplateDetailTemplate) {
			reloadDetailTemplate(tmplId);
		}else if(template instanceof TemplateListTemplate) {
			reloadListTemplate(tmplId);
		}else if(template instanceof TemplateSelectionTemplate) {
			reloadSelectionTemplate(tmplId);
		}else if(template instanceof TemplateActionTemplate) {
			reloadActionTemplate(tmplId);
		}
		return tmplId;
	}
	
	
	

	Map<Long, TemplateGroup> getTemplateGroupMap(){
		synchronized (this) {
			if(tmplGroupMap == null) {
				logger.debug("开始初始化所有模板组合缓存数据...");
				List<TemplateGroup> groups = gDao.queryGroups().stream().filter(group->checkModuleUsable(group.getModule())).collect(Collectors.toList());
				Map<Long, List<TemplateGroupPremise>> premisesMap = CollectionUtils.toListMap(gDao.queryPremises(), premise->premise.getGroupId());
				Map<Long, List<TemplateGroupAction>> groupActionsMap = CollectionUtils.toListMap(gDao.queryActions(), action->action.getGroupId());
				
				for (TemplateGroup group : groups) {
					handlerTmplGroup(group, premisesMap.get(group.getId()), groupActionsMap.get(group.getId()));
				}
				tmplGroupMap = CollectionUtils.toMap(groups, group->group.getId());
				logger.debug(tmplGroupMap);
				logger.debug("初始化模板组合缓存数据完成，共缓存" + tmplGroupMap.size() + "个模板组合");
			}
			return tmplGroupMap;
		}
	}
	
	void reloadTemplateGroup(Long tmplGroupId){
		if(tmplGroupMap != null) {
			synchronized (tmplGroupMap) {
				logger.debug("重新加载模板组合[id=" + tmplGroupId + "]缓存数据...");
				TemplateGroup group = nDao.get(TemplateGroup.class, tmplGroupId);
				if(group != null) {
					List<TemplateGroupPremise> premises = gDao.queryPremises(group.getId());
					List<TemplateGroupAction> actions = gDao.queryActions(group.getId());
					handlerTmplGroup(group, premises, actions);
					tmplGroupMap.put(tmplGroupId, group);
					triggerTemplateGroupReloadEvent(group);
					logger.debug("模板组合[" + tmplGroupId + "]缓存数据重新加载完成, 值为" + group);
				}else {
					tmplGroupMap.remove(tmplGroupId);
					logger.debug("从缓存中移除模板组合[id=" + tmplGroupId + "]");
				}
			}
		}
	}
	
	Set<Consumer<TemplateGroup>> consumers = new LinkedHashSet<>();

	
	@Override
	public void bindTemplateGroupReloadEvent(Consumer<TemplateGroup> consumer) {
		consumers.add(consumer);
	}
	
	private void triggerTemplateGroupReloadEvent(TemplateGroup group) {
		for (Consumer<TemplateGroup> consumer : consumers) {
			try {
				consumer.accept(group);
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

	private void handlerTmplGroup(TemplateGroup group, List<TemplateGroupPremise> premises, List<TemplateGroupAction> actions) {
		Long ltmplId = group.getListTemplateId(),
				dtmplId = group.getDetailTemplateId();
		TemplateListTemplate ltmpl = getListTemplate(ltmplId);
		Assert.notNull(ltmpl, "模板组合[id=" + group.getId() + "]的列表模板[id=" + ltmplId + "]不存在");
		TemplateDetailTemplate dtmpl = getDetailTemplate(dtmplId);
		Assert.notNull(dtmpl, "模板组合[id=" + group.getId() + "]的详情模板[id=" + dtmplId + "]不存在");
		group.setListTemplateTitle(ltmpl.getTitle());
		group.setDetailTemplateTitle(dtmpl.getTitle());
		if(premises != null) {
			group.setPremises(premises);
			premises.forEach(premise->{
				DictionaryField field = dictService.getField(group.getModule(), premise.getFieldId());
				if(field != null) {
					premise.setFieldTitle(field.getTitle());
					premise.setFieldName(field.getFullKey());
				}
			});
		}
		if(actions != null) {
			group.setActions(actions);
			this.groupActionMap .putAll(CollectionUtils.toMap(actions, TemplateGroupAction::getId));
		}
	}

	@Override
	public List<TemplateGroup> queryTemplateGroups(String module) {
		return getTemplateGroupMap().values().stream()
				.filter(group->module.equals(group.getModule()))
				.collect(Collectors.toList());
	}
	
	@Override
	public Long saveGroup(TemplateGroup group, UserIdentifier user) {
		if(group.getId() != null) {
			//修改模板组合
			List<TemplateGroupPremise> originPremises = gDao.queryPremises(group.getId());
			List<TemplateGroupAction> originActions = gDao.queryActions(group.getId());
			
			nDao.update(group);
			
			NormalDaoSetUpdateStrategy.build(
					TemplateGroupPremise.class, nDao,
					premise->premise.getId(),
					(oPremise, premise)->{
						oPremise.setFieldValue(premise.getFieldValue());
						oPremise.setOrder(premise.getOrder());
					},premise->{
						premise.setGroupId(group.getId());
					})
				.doUpdate(new HashSet<>(originPremises ), new HashSet<>(group.getPremises()));
			
			
			NormalDaoSetUpdateStrategy.build(
					TemplateGroupAction.class, nDao, 
					TemplateGroupAction::getId, 
					(oAction, action)->{
						oAction.setTitle(action.getTitle());
						oAction.setMultiple(action.getMultiple());
				}, action->{
						action.setGroupId(group.getId());
				})
				.doUpdate(new HashSet<>(originActions), new HashSet<>(group.getActions()));
			
			TransactionAspectSupport.currentTransactionStatus().flush();
			nDao.clear();
			reloadTemplateGroup(group.getId());
			return group.getId();
		}else {
			//创建模板组合
			group.setCreateUserCode((String) user.getId());
			group.setCreateTime(group.getUpdateTime());
			group.setKey(TextUtils.hasText(group.getKey())? group.getKey(): TextUtils.uuid(5, 36));
			Long groupId = nDao.save(group);
			if(group.getPremises() != null) {
				group.getPremises().forEach(premise->{
					premise.setGroupId(groupId);
					nDao.save(premise);
				});
			}
			if(group.getActions() != null) {
				group.getActions().forEach(action->{
					action.setGroupId(groupId);
					nDao.save(action);
				});
			}
			reloadTemplateGroup(groupId);
			return groupId;
		}
		
	}
	
	@Override
	public TemplateGroup getTemplateGroup(Long groupId) {
		return getTemplateGroupMap().get(groupId);
	}
	
	@Override
	public void removeTemplateGroup(Long groupId) {
		TemplateGroup group = new TemplateGroup();
		group.setId(groupId);
		nDao.remove(group);
		reloadTemplateGroup(groupId);
	}
	
	@Override
	public TemplateGroup getTemplateGroup(String module, String templateGroupKey) {
		return getTemplateGroupMap().values().stream()
			.filter(group->module.equals(group.getModule())&& templateGroupKey.equals(group.getKey()))
			.findFirst().orElse(null);
	}
	
	@Override
	public Map<String, List<TemplateGroup>> queryTemplateGroups(Set<String> moduleNames) {
		Set<TemplateGroup> set = getTemplateGroupMap().values().stream().filter(group->moduleNames.contains(group.getModule())).collect(Collectors.toSet());
		return CollectionUtils.toListMap(set, group->group.getModule());
	}
	
	@Override
	public TemplateDetailTemplate getDetailTemplateByGroupId(Long templateGroupId) {
		TemplateGroup group = getTemplateGroup(templateGroupId);
		if(group != null) {
			return getDetailTemplate(group.getDetailTemplateId());
		}
		return null;
	}
	
	@Override
	public void clearCache() {
		synchronized (this) {
			if(dtmplMap != null) {
				synchronized (dtmplMap) {
					dtmplMap = null;
				}
			}
			if(ltmplMap != null) {
				synchronized (ltmplMap) {
					ltmplMap = null;
				}
			}
			if(atmplMap != null) {
				synchronized (atmplMap) {
					atmplMap = null;
				}
			}
			if(stmplMap != null) {
				synchronized (stmplMap) {
					stmplMap = null;
				}
			}
			if(tmplGroupMap != null) {
				synchronized (tmplGroupMap) {
					tmplGroupMap = null;
				}
			}
		}
	}
	
	@Override
	public void loadCache() {
		getDetailTemplateMap();
		getListTemplateMap();
		getTemplateGroupMap();
		getActionTemplateMap();
	}
	
	@Override
	public Set<TemplateGroup> getListTemplateRelatedGroups(Long ltmplId) {
		return getTemplateGroupMap().values().stream()
			.filter(group -> ltmplId.equals(group.getListTemplateId())).collect(Collectors.toSet());
	}
	
	@Override
	public Set<TemplateGroup> getDetailTemplateRelatedGroups(Long dtmplId) {
		return getTemplateGroupMap().values().stream()
			.filter(group -> dtmplId.equals(group.getDetailTemplateId())).collect(Collectors.toSet());
	}
	
	
	@Override
	public Map<Long, Set<TemplateGroup>> getDetailTemplateRelatedGroupsMap(Set<Long> dtmplIds) {
		Map<Long, Set<TemplateGroup>> map = new HashMap<>();
		if(dtmplIds != null) {
			for (Long dtmplId : dtmplIds) {
				map.put(dtmplId, getDetailTemplateRelatedGroups(dtmplId));
			}
		}
		return map;
	}
	
	
	@Override
	public Map<Long, Set<TemplateGroup>> getListTemplateRelatedGroupsMap(Set<Long> ltmplIds) {
		Map<Long, Set<TemplateGroup>> map = new HashMap<>();
		if(ltmplIds != null) {
			for (Long ltmplId : ltmplIds) {
				map.put(ltmplId, getListTemplateRelatedGroups(ltmplId));
			}
		}
		return map;
	}
	
	@Override
	public void switchAllGroupsDetailTemplate(Long dtmplId, Long targetDtmplId) {
		gDao.updateAllGroupsDetailTemplate(dtmplId, targetDtmplId);
		clearCache();
	}
	
	@Override
	public void switchAllGroupsListTemplate(Long ltmplId, Long targetLtmplId) {
		gDao.updateAllGroupsDetailTemplate(ltmplId, targetLtmplId);
		clearCache();
	}
	
	@Override
	public TemplateSelectionTemplate getSelectionTemplate(Long stmplId) {
		return getSelectionTemplateMap().get(stmplId);
	}
	
	Map<Long, TemplateSelectionTemplate> getSelectionTemplateMap(){
		synchronized (this) {
			if(stmplMap == null) {
				logger.debug("开始初始化所有选择模板缓存数据...");
				List<TemplateSelectionTemplate> stmpls = sDao.queryTemplates();
				Map<Long, List<TemplateSelectionColumn>> columnsMap = sDao.queryColumnsMap();
				Map<Long, Set<TemplateSelectionCriteria>> criteriasMap = sDao.queryCriteriasMap();
				stmpls = stmpls.stream().filter(stmpl->checkModuleUsable(stmpl.getModule())).collect(Collectors.toList());
				stmpls.forEach(ltmpl->{
					List<TemplateSelectionColumn> columns = columnsMap.get(ltmpl.getId());
					Set<TemplateSelectionCriteria> criterias = criteriasMap.get(ltmpl.getId());
					handlerSelectionTemplate(ltmpl, columns, criterias, getModuleTemplateReferData(ltmpl.getModule()));
				});
				stmplMap = CollectionUtils.toMap(stmpls, ltmpl->ltmpl.getId());
				logger.debug(ltmplMap);
				logger.debug("初始化选择模板缓存数据完成，共缓存" + ltmplMap.size() + "个列表模板");
			}
			return stmplMap;
		}
	}
	
	private void handlerSelectionTemplate(TemplateSelectionTemplate stmpl, List<TemplateSelectionColumn> columns,
			Set<TemplateSelectionCriteria> criterias, ModuleTemplateReferData referData) {
		Assert.notNull(stmpl.getCompositeId());
		DictionaryComposite composite = referData.getCompositeMap().get(stmpl.getCompositeId());
		Map<Long, DictionaryField> fieldMap = referData.getFieldMap();
		if(composite != null && Composite.RELATION_ADD_TYPE.equals(composite.getAddType())) {
			stmpl.setRelationName(composite.getName());
		}
		if(columns != null) {
			columns.forEach(column->{
				if(column.getSpecialField() == null) {
					DictionaryField field = fieldMap.get(column.getFieldId());
					if(field != null) {
						column.setFieldKey(field.getFullKey());
					}else {
						column.setFieldUnavailable();
					}
				}
			});
			stmpl.setColumns(columns);
		}
		if(criterias != null) {
			criterias.forEach(criteria->{
				DictionaryField field = fieldMap.get(criteria.getFieldId());
				if(field != null) {
					if(supportFieldInputType(criteria.getInputType(), field.getType(), referData.getFieldInputTypeMap())) {
						criteria.setFieldKey(field.getFullKey());
						//只有字段存在并且字段当前类型支持当前条件的表单类型，该条件字段才可用
						//(因为条件的表单类型是创建模板时选择的，与字段类型不同，防止字段修改了类型但与条件表单类型不匹配)
						return;
					}
				}
				criteria.setFieldUnavailable();
			});
			stmpl.setCriterias(criterias);
		}
	}
	@Override
	public void reloadSelectionTemplate(Long tmplId) {
		if(stmplMap != null) {
			synchronized (ltmplMap) {
				logger.debug("重新加载选择模板[id=" + tmplId + "]缓存数据...");
				TemplateSelectionTemplate stmpl = nDao.get(TemplateSelectionTemplate.class, tmplId);
				if(stmpl != null && checkModuleUsable(stmpl.getModule())) {
					List<TemplateSelectionColumn> columns = sDao.getColumnsByTmplId(tmplId);
					Set<TemplateSelectionCriteria> criterias = sDao.getCriteriaByTmplId(tmplId);
					handlerSelectionTemplate(stmpl, columns, criterias, getModuleTemplateReferData(stmpl.getModule()));
					stmplMap.put(tmplId, stmpl);
					logger.debug("列表模板[" + tmplId + "]缓存数据重新加载完成, 值为" + stmpl);
				}else {
					ltmplMap.remove(tmplId);
					logger.debug("从缓存中移除选择模板[id=" + tmplId + "]");
				}
			}
		}
	}
	
	
	/**
	 * 复制一个列表模板到某个模块当中
	 * @param ltmplId
	 * @param targerModuleName
	 * @return
	 */
	@Override
	public Long copyListTemplate(Long ltmplId, String targetModuleName) {
		Assert.notNull(ltmplId);
		Assert.hasText(targetModuleName);
		TemplateListTemplate ltmpl = getListTemplate(ltmplId);
		ModuleMeta sourceModule = mService.getModule(ltmpl.getModule());
		if(ltmpl != null) {
			FusionContextConfig config = fFactory.getModuleConfig(targetModuleName);
			if(config != null) {
				//复制一个列表模板对象
				TemplateListTemplate newTmpl = new TemplateListTemplate();
				newTmpl.setTitle("(复制自" + sourceModule.getTitle() + "-" + ltmpl.getTitle() + ")");
				newTmpl.setModule(targetModuleName);
				newTmpl.setCreateTime(new Date());
				newTmpl.setUpdateTime(newTmpl.getCreateTime());
				newTmpl.setDefaultPageSize(ltmpl.getDefaultPageSize());
				newTmpl.setUnmodifiable(ltmpl.getUnmodifiable());
				if(ltmpl.getDefaultOrderFieldId() != null) {
					DictionaryField targetOrderField = dictService.mapModuleField(targetModuleName, dictService.getField(ltmpl.getModule(), ltmpl.getDefaultOrderFieldId()));
					if(targetOrderField != null) {
						newTmpl.setDefaultOrderFieldId(targetOrderField.getId());
						newTmpl.setDefaultOrderDirection(ltmpl.getDefaultOrderDirection());
					}
				}
				copyListTemplateCriterias(ltmpl, newTmpl);
				copyListTemplateColumns(ltmpl, newTmpl);
				Long tmplId = tmplUpdateStrategyFactory.getStrategy(newTmpl).create(newTmpl);
				if(tmplId != null) {
					reloadListTemplate(tmplId);
				}
				return tmplId;
			}
		}
		return null;
	}
	
	private void copyListTemplateColumns(TemplateListTemplate ltmpl, TemplateListTemplate newTmpl) {
		String targetModuleName = newTmpl.getModule();
		List<TemplateListColumn> columns = ltmpl.getColumns();
		if(columns != null) {
			for (TemplateListColumn column : columns) {
				TemplateListColumn nColumn = new TemplateListColumn();
				if(nColumn.getFieldAvailable()) {
					if(column.getSpecialField() == null) {
						if(column.getFieldId() != null) {
							DictionaryField field = dictService.mapModuleField(targetModuleName, dictService.getField(ltmpl.getModule(), column.getFieldId()));
							if(field != null) {
								nColumn.setFieldId(field.getId());
							}else {
								logger.error("展示字段[" + column.getTitle() + "]的fieldId属性[" 
											+ column.getFieldId() + "]无法在模块[" + targetModuleName + "]中找到对应的字段");
								continue;
							}
						}else {
							logger.error("展示字段[" + column.getTitle() + "]的specialField属性和fieldId属性不能均为空");
							continue;
						}
					}else {
						nColumn.setSpecialField(column.getSpecialField());
					}
					nColumn.setTitle(column.getTitle());
					nColumn.setOrder(column.getOrder());
					nColumn.setOrderable(column.getOrderable());
					nColumn.setViewOption(column.getViewOption());
					nColumn.setCreateTime(newTmpl.getCreateTime());
					nColumn.setUpdateTime(newTmpl.getUpdateTime());
					newTmpl.getColumns().add(nColumn);
				}
			}
		}
		
	}

	private void copyListTemplateCriterias(TemplateListTemplate ltmpl, TemplateListTemplate newTmpl) {
		String targetModuleName = newTmpl.getModule();
		Set<TemplateListCriteria> criterias = ltmpl.getCriterias();
		if(criterias != null) {
			Map<String, Set<String>> fieldInputTypeMap = dictService.getFieldInputTypeMap();
			for (TemplateListCriteria criteria : criterias) {
				TemplateListCriteria nCriteria = new TemplateListCriteria();
				if(criteria.getComposite() == null) {
					//字段条件
					if(criteria.getFieldAvailable()) {
						DictionaryField field = dictService.mapModuleField(targetModuleName, dictService.getField(ltmpl.getModule(), criteria.getFieldId()));
						if(field != null) {
							if(criteria.getRelationLabel() != null) {
								DictionaryComposite composite = field.getComposite();
								if(composite.getRelationSubdomain() == null
										|| !composite.getRelationSubdomain().contains(criteria.getRelation())) {
									logger.error("关系字段条件的label[" + criteria.getRelationLabel() 
									+ "]在目标模块[" + targetModuleName + "]对应的composite[" 
									+ composite.getRelationSubdomain() + "]中不存在");
									continue;
								}
								nCriteria.setRelationLabel(criteria.getRelationLabel());
							}
							if(supportFieldInputType(criteria.getInputType(), field.getType(), fieldInputTypeMap)) {
								nCriteria.setInputType(criteria.getInputType());
							}else {
								nCriteria.setInputType("text");
								logger.error("条件字段[" + criteria.getTitle() + "]匹配到模块[" 
										+ targetModuleName + "]的字段[" + field.getFullKey() + "]的类型[" + field.getType() 
										+ "]不支持原表单类型[" + criteria.getInputType() + "]，将替换为文本类型");
							}
							nCriteria.setFieldId(field.getId());
						}else {
							logger.error("找不到条件字段[" 
									+ criteria.getTitle() + "]在目标模块[" + targetModuleName + "]中匹配的字段");
							continue;
						}
					}
				}else {
					//composite条件
					DictionaryComposite targetComposite = dictService.mapModuleComposite(targetModuleName, criteria.getComposite());
					if(targetComposite != null) {
						nCriteria.setCompositeId(targetComposite.getId());
						nCriteria.setComposite(targetComposite);
						nCriteria.setInputType(criteria.getInputType());
					}else {
						logger.error("找不到条件关系[" +  criteria.getComposite().getTitle() + "]在目标模块[" + targetModuleName + "]中匹配的的关系");
						continue;
					}
				}
				nCriteria.setTitle(criteria.getTitle());
				nCriteria.setRelation(criteria.getRelation());
				nCriteria.setQueryShow(criteria.getQueryShow());
				nCriteria.setComparator(criteria.getComparator());
				nCriteria.setOrder(criteria.getOrder());
				nCriteria.setViewOption(criteria.getViewOption());
				nCriteria.setDefaultValue(criteria.getDefaultValue());
				nCriteria.setPlaceholder(criteria.getPlaceholder());
				nCriteria.setCreateTime(newTmpl.getCreateTime());
				
				newTmpl.getCriterias().add(nCriteria);
			}
		}
	}

	/**
	 * 复制一个详情模板到某个模块当中
	 * @param dtmplId
	 * @param targetModuleName
	 * @return
	 */
	@Override
	public Long copyDetailTemplate(Long dtmplId, String targetModuleName) {
		Assert.notNull(dtmplId);
		Assert.hasText(targetModuleName);
		TemplateDetailTemplate dtmpl = getDetailTemplate(dtmplId);
		ModuleMeta sourceModule = mService.getModule(dtmpl.getModule());
		if(dtmpl != null) {
			FusionContextConfig config = fFactory.getModuleConfig(targetModuleName);
			if(config != null) {
				TemplateDetailTemplate newTmpl = new TemplateDetailTemplate();
				//复制
				newTmpl.setModule(targetModuleName);
				newTmpl.setTitle("(复制自" + sourceModule.getTitle() + "-" + dtmpl.getTitle() + ")");
				newTmpl.setCreateTime(new Date());
				newTmpl.setUpdateTime(newTmpl.getCreateTime());
				List<TemplateDetailFieldGroup> groups = dtmpl.getGroups();
				if(groups != null) {
					//遍历所有字段组
					for (TemplateDetailFieldGroup group : groups) {
						TemplateDetailFieldGroup nGroup = new TemplateDetailFieldGroup();
						if(group.getFields() != null) {
							//遍历字段组内的所有字段
							for (TemplateDetailField field : group.getFields()) {
								//只有字段是可用的情况下才复制该字段
								if(field.getFieldAvailable()) {
									//映射获得目标模块对应的字段
									DictionaryField targetField = dictService.mapModuleField(targetModuleName, dictService.getField(dtmpl.getModule(), field.getFieldId()));
									if(targetField != null) {
										TemplateDetailField nField = new TemplateDetailField();
										nField.setFieldId(targetField.getId());
										nField.setFieldName(targetField.getFullKey());
										nField.setTitle(field.getTitle());
										nField.setOrder(field.getOrder());
										nField.setColNum(field.getColNum());
										nField.setUnmodifiable(field.getUnmodifiable());
										nField.setViewValue(field.getViewValue());
										nField.setType(field.getType());
										nField.setValidators(field.getValidators());
										nField.setOptionGroupId(field.getOptionGroupId());
										nGroup.getFields().add(nField);
									}
								}
							}
						}
						if(group.getComposite() != null) {
							try {
								//如果字段组的composite不为空的话，那么就要根据新模板中已经匹配好的fields推断出其对应的composite
								DictionaryComposite targetGroupComposite = analyzeModuleMapComposite(targetModuleName, nGroup.getFields(), group);
								if(targetGroupComposite != null) {
									nGroup.setIsArray(targetGroupComposite.getIsArray());
									nGroup.setCompositeId(targetGroupComposite.getId());
								}
								if(group.getSelectionTemplateId() != null) {
									//复制并创建一个新的选择模板
									Long stmplId = copySeletionTemplate(targetGroupComposite, group.getSelectionTemplateId());
									if(stmplId != null) {
										nGroup.setSelectionTemplateId(stmplId);
									}
								}
							} catch (Exception e) {
								logger.error("解析字段组[" + group.getTitle() + "]的composite时发生错误，将不复制该字段组", e);
								continue;
							}
						}
						nGroup.setTitle(group.getTitle());
						nGroup.setOrder(group.getOrder());
						nGroup.setUnallowedCreate(group.getUnallowedCreate());
						nGroup.setUnmodifiable(group.getUnmodifiable());
						nGroup.setUpdateTime(newTmpl.getUpdateTime());
						newTmpl.getGroups().add(nGroup);
					}
				}
				Long newTmplId = tmplUpdateStrategyFactory.getStrategy(newTmpl).create(newTmpl);
				if(newTmplId != null) {
					reloadDetailTemplate(newTmplId);
				}
				return newTmplId;
			}
		}
		return null;
	}

	private Long copySeletionTemplate(DictionaryComposite targetGroupComposite,
			Long selectionTemplateId) {
		if(Composite.RELATION_ADD_TYPE.equals(targetGroupComposite.getAddType())) {
			TemplateSelectionTemplate stmpl = getSelectionTemplate(selectionTemplateId);
			if(stmpl != null && stmpl.getRelationName() != null) {
				String targetModuleName = stmpl.getModule();
				ModuleMeta sourceModule = mService.getModule(targetModuleName);
				if(sourceModule != null) {
					//复制一个选择模板对象
					TemplateSelectionTemplate newTmpl = new TemplateSelectionTemplate();
					newTmpl.setCompositeId(targetGroupComposite.getId());
					newTmpl.setRelationName(targetGroupComposite.getName());
					newTmpl.setTitle("(复制自" + sourceModule.getTitle() + "-" + stmpl.getTitle() + ")");
					newTmpl.setModule(targetModuleName);
					newTmpl.setCreateTime(new Date());
					newTmpl.setUpdateTime(newTmpl.getCreateTime());
					newTmpl.setDefaultPageSize(stmpl.getDefaultPageSize());
					newTmpl.setMultiple(stmpl.getMultiple());
					newTmpl.setNonunique(stmpl.getNonunique());
					if(stmpl.getDefaultOrderFieldId() != null) {
						DictionaryField targetOrderField = dictService.mapModuleField(targetModuleName, dictService.getField(stmpl.getModule(), stmpl.getDefaultOrderFieldId()));
						if(targetOrderField != null) {
							newTmpl.setDefaultOrderFieldId(targetOrderField.getId());
							newTmpl.setDefaultOrderDirection(stmpl.getDefaultOrderDirection());
						}
					}
					copySelectionTemplateCriterias(stmpl, newTmpl);
					copySelectionTemplateColumns(stmpl, newTmpl);
					Long tmplId = tmplUpdateStrategyFactory.getStrategy(newTmpl).create(newTmpl);
					if(tmplId != null) {
						reloadSelectionTemplate(tmplId);
					}
					return tmplId;
				}else {
					logger.error("没有找到目标模块[" + targetModuleName + "]");
				}
			}else {
				logger.error("找不到原选择模板，或者原选择模板的RelationName为空");
			}
		}
		return null;
	}

	private void copySelectionTemplateColumns(TemplateSelectionTemplate stmpl, TemplateSelectionTemplate newTmpl) {
		String targetModuleName = newTmpl.getModule();
		List<TemplateSelectionColumn> columns = stmpl.getColumns();
		if(columns != null) {
			for (TemplateSelectionColumn column : columns) {
				TemplateSelectionColumn nColumn = new TemplateSelectionColumn();
				if(nColumn.getFieldAvailable()) {
					if(column.getSpecialField() == null) {
						if(column.getFieldId() != null) {
							DictionaryField field = dictService.mapModuleField(targetModuleName, dictService.getField(stmpl.getModule(), column.getFieldId()));
							if(field != null) {
								nColumn.setFieldId(field.getId());
							}else {
								logger.error("展示字段[" + column.getTitle() + "]的fieldId属性[" 
											+ column.getFieldId() + "]无法在模块[" + targetModuleName + "]中找到对应的字段");
								continue;
							}
						}else {
							logger.error("展示字段[" + column.getTitle() + "]的specialField属性和fieldId属性不能均为空");
							continue;
						}
					}else {
						nColumn.setSpecialField(column.getSpecialField());
					}
					nColumn.setTitle(column.getTitle());
					nColumn.setOrder(column.getOrder());
					nColumn.setOrderable(column.getOrderable());
					nColumn.setViewOption(column.getViewOption());
					nColumn.setCreateTime(newTmpl.getCreateTime());
					nColumn.setUpdateTime(newTmpl.getUpdateTime());
					newTmpl.getColumns().add(nColumn);
				}
			}
		}
		
	}

	private void copySelectionTemplateCriterias(TemplateSelectionTemplate stmpl, TemplateSelectionTemplate newTmpl) {
		String targetModuleName = newTmpl.getModule();
		Set<TemplateSelectionCriteria> criterias = stmpl.getCriterias();
		if(criterias != null) {
			Map<String, Set<String>> fieldInputTypeMap = dictService.getFieldInputTypeMap();
			for (TemplateSelectionCriteria criteria : criterias) {
				TemplateSelectionCriteria nCriteria = new TemplateSelectionCriteria();
				//字段条件
				if(criteria.getFieldAvailable()) {
					DictionaryField field = dictService.mapModuleField(targetModuleName, dictService.getField(stmpl.getModule(), criteria.getFieldId()));
					if(field != null) {
						if(criteria.getRelationLabel() != null) {
							DictionaryComposite composite = field.getComposite();
							if(composite.getRelationSubdomain() == null
									|| !composite.getRelationSubdomain().contains(criteria.getRelation())) {
								logger.error("关系字段条件的label[" + criteria.getRelationLabel() 
								+ "]在目标模块[" + targetModuleName + "]对应的composite[" 
								+ composite.getRelationSubdomain() + "]中不存在");
								continue;
							}
							nCriteria.setRelationLabel(criteria.getRelationLabel());
						}
						if(supportFieldInputType(criteria.getInputType(), field.getType(), fieldInputTypeMap)) {
							nCriteria.setInputType(criteria.getInputType());
						}else {
							nCriteria.setInputType("text");
							logger.error("条件字段[" + criteria.getTitle() + "]匹配到模块[" 
									+ targetModuleName + "]的字段[" + field.getFullKey() + "]的类型[" + field.getType() 
									+ "]不支持原表单类型[" + criteria.getInputType() + "]，将替换为文本类型");
						}
						nCriteria.setFieldId(field.getId());
					}else {
						logger.error("找不到条件字段[" 
								+ criteria.getTitle() + "]在目标模块[" + targetModuleName + "]中匹配的字段");
						continue;
					}
				}
				nCriteria.setTitle(criteria.getTitle());
				nCriteria.setRelation(criteria.getRelation());
				nCriteria.setQueryShow(criteria.getQueryShow());
				nCriteria.setComparator(criteria.getComparator());
				nCriteria.setOrder(criteria.getOrder());
				nCriteria.setViewOption(criteria.getViewOption());
				nCriteria.setDefaultValue(criteria.getDefaultValue());
				nCriteria.setPlaceholder(criteria.getPlaceholder());
				nCriteria.setCreateTime(newTmpl.getCreateTime());
				newTmpl.getCriterias().add(nCriteria);
			}
		}
	}

	/**
	 * 根据目标模块名和
	 * @param targetModuleName
	 * @param fields
	 * @param group
	 * @return
	 */
	private DictionaryComposite analyzeModuleMapComposite(String targetModuleName, List<TemplateDetailField> tfields,
			TemplateDetailFieldGroup group) {
		Assert.notEmpty(tfields);
		//获得目标模块下的所有字段
		Map<Long, DictionaryField> fieldMap = CollectionUtils.toMap(dictService.getAllFields(targetModuleName), f->f.getId());
		Set<DictionaryComposite> composites = new LinkedHashSet<>();
		Set<TemplateDetailField> toRemoves = new HashSet<>();
		for (TemplateDetailField tfield : tfields) {
			DictionaryField field = fieldMap.get(tfield.getFieldId());
			if(field != null) {
				composites.add(field.getComposite());
			}else {
				//field不存在，则删除该字段
				toRemoves.add(tfield);
			}
		}
		for (TemplateDetailField toRemove : toRemoves) {
			tfields.remove(toRemove);
		}
		
		if(composites.isEmpty()) {
			//所有的字段都没有composite，那么肯定有问题
			throw new RuntimeException("要复制的字段组[" + group.getTitle() + "]的composite都为空");
		}else if(composites.size() == 1) {
			//字段都是在同一个composite里，直接返回该composite
			return composites.iterator().next();
		}
		//字段属于多个composite，则要判断字段
		Set<DictionaryComposite> arrayComposite = composites.stream().filter(composite->Integer.valueOf(1).equals(composite.getIsArray())).collect(Collectors.toSet());
		if(arrayComposite.size() == 0) {
			//全部都是非数组composite，返回空
			return null;
		}else {
			//选出field最多的composite，其他的field去除
			Map<DictionaryComposite, Integer> compositeFieldCount = new HashMap<>();
			for (TemplateDetailField tfield : tfields) {
				DictionaryField field = fieldMap.get(tfield.getFieldId());
				if(compositeFieldCount.containsKey(field.getComposite())) {
					compositeFieldCount.put(field.getComposite(), compositeFieldCount.get(field.getComposite()) + 1);
				}else {
					compositeFieldCount.put(field.getComposite(), 1);
				}
			}
			DictionaryComposite maxComposite = null;
			int max = 0;
			for (Entry<DictionaryComposite, Integer> entry : compositeFieldCount.entrySet()) {
				if(entry.getValue() > max) {
					max = entry.getValue();
					maxComposite = entry.getKey();
				}
			}
			Iterator<TemplateDetailField> itr = tfields.iterator();
			//移除弱势群体
			while(itr.hasNext()) {
				TemplateDetailField tfield = itr.next();
				DictionaryField field = fieldMap.get(tfield.getFieldId());
				if(field.getComposite() != maxComposite) {
					itr.remove();
				}
			}
			return maxComposite;
		}
	}
	
	/**
	 * 根据目标模块名和
	 * @param targetModuleName
	 * @param fields
	 * @param group
	 * @return
	 */
	private DictionaryComposite analyzeModuleMapComposite(String targetModuleName, List<TemplateActionField> tfields,
			TemplateActionFieldGroup group) {
		Assert.notEmpty(tfields);
		//获得目标模块下的所有字段
		Map<Long, DictionaryField> fieldMap = CollectionUtils.toMap(dictService.getAllFields(targetModuleName), f->f.getId());
		Set<DictionaryComposite> composites = new LinkedHashSet<>();
		Set<TemplateActionField> toRemoves = new HashSet<>();
		for (TemplateActionField tfield : tfields) {
			DictionaryField field = fieldMap.get(tfield.getFieldId());
			if(field != null) {
				composites.add(field.getComposite());
			}else {
				//field不存在，则删除该字段
				toRemoves.add(tfield);
			}
		}
		for (TemplateActionField toRemove : toRemoves) {
			tfields.remove(toRemove);
		}
		
		if(composites.isEmpty()) {
			//所有的字段都没有composite，那么肯定有问题
			throw new RuntimeException("要复制的字段组[" + group.getTitle() + "]的composite都为空");
		}else if(composites.size() == 1) {
			//字段都是在同一个composite里，直接返回该composite
			return composites.iterator().next();
		}
		//字段属于多个composite，则要判断字段
		Set<DictionaryComposite> arrayComposite = composites.stream().filter(composite->Integer.valueOf(1).equals(composite.getIsArray())).collect(Collectors.toSet());
		if(arrayComposite.size() == 0) {
			//全部都是非数组composite，返回空
			return null;
		}else {
			//选出field最多的composite，其他的field去除
			Map<DictionaryComposite, Integer> compositeFieldCount = new HashMap<>();
			for (TemplateActionField tfield : tfields) {
				DictionaryField field = fieldMap.get(tfield.getFieldId());
				if(compositeFieldCount.containsKey(field.getComposite())) {
					compositeFieldCount.put(field.getComposite(), compositeFieldCount.get(field.getComposite()) + 1);
				}else {
					compositeFieldCount.put(field.getComposite(), 1);
				}
			}
			DictionaryComposite maxComposite = null;
			int max = 0;
			for (Entry<DictionaryComposite, Integer> entry : compositeFieldCount.entrySet()) {
				if(entry.getValue() > max) {
					max = entry.getValue();
					maxComposite = entry.getKey();
				}
			}
			Iterator<TemplateActionField> itr = tfields.iterator();
			//移除弱势群体
			while(itr.hasNext()) {
				TemplateActionField tfield = itr.next();
				DictionaryField field = fieldMap.get(tfield.getFieldId());
				if(field.getComposite() != maxComposite) {
					itr.remove();
				}
			}
			return maxComposite;
		}
	}
	
	
	@Override
	public Long copyTemplateGroup(Long tmplGroupId, String targetModuleName, UserIdentifier user) {
		Assert.notNull(tmplGroupId);
		Assert.hasText(targetModuleName);
		
		TemplateGroup tmplGroup = getTemplateGroup(tmplGroupId);
		if(tmplGroup != null) {
			ModuleMeta sourceModule = mService.getModule(tmplGroup.getModule());
			FusionContextConfig config = fFactory.getModuleConfig(targetModuleName);
			if(config != null) {
				Long newListTmplId = copyListTemplate(tmplGroup.getListTemplateId(), targetModuleName);
				if(newListTmplId != null) {
					Long newDetailTmplId = copyDetailTemplate(tmplGroup.getDetailTemplateId(), targetModuleName);
					if(newDetailTmplId != null) {
						TemplateGroup newGroup = new TemplateGroup();
						List<TemplateGroupPremise> premises = tmplGroup.getPremises();
						if(premises != null && !premises.isEmpty()) {
							for (TemplateGroupPremise premise : premises) {
								if(premise.getFieldId() != null) {
									DictionaryField targetPremiseField = dictService.mapModuleField(targetModuleName, dictService.getField(tmplGroup.getModule(), premise.getFieldId()));
									if(targetPremiseField != null) {
										TemplateGroupPremise newPremise = new TemplateGroupPremise();
										newPremise.setFieldId(targetPremiseField.getId());
										newPremise.setFieldValue(premise.getFieldValue());
										newPremise.setOrder(premise.getOrder());
										newGroup.getPremises().add(newPremise);
									}else {
										logger.error("复制模板组合[" + sourceModule.getName() 
											+ "]的前提字段[" + premise.getFieldTitle() + "]失败，在目标模块[" 
											+ targetModuleName + "]找不到对应的字段");
									}
								}else {
									logger.error("复制模板组合[" + sourceModule.getName() 
										+ "]的前提字段[" + premise.getFieldTitle() + "]失败，fieldId为空");
								}
							}
						}
						
						List<TemplateGroupAction> actions = tmplGroup.getActions();
						Map<Long, Long> atmplIdMap = new HashMap<>();
						if(actions != null && !actions.isEmpty()) {
							for (TemplateGroupAction action : actions) {
								TemplateGroupAction nAction = new TemplateGroupAction();
								if(!atmplIdMap.containsKey(action.getAtmplId())) {
									Long newAtmplId = copyActionTemplate(action.getAtmplId(), targetModuleName);
									atmplIdMap.put(action.getAtmplId(), newAtmplId);
								}
								nAction.setAtmplId(atmplIdMap.get(action.getAtmplId()));
								nAction.setFace(action.getFace());
								nAction.setMultiple(action.getMultiple());
								nAction.setOrder(action.getOrder());
								nAction.setTitle(action.getTitle());
								nAction.setType(action.getType());
								newGroup.getActions().add(nAction);
							}
						}
						
						newGroup.setTitle("(复制自" + sourceModule.getTitle() + "-" + tmplGroup.getTitle() + ")");
						newGroup.setModule(targetModuleName);
						newGroup.setDisabled(tmplGroup.getDisabled());
						newGroup.setHideCreateButton(tmplGroup.getHideCreateButton());
						newGroup.setHideExportButton(tmplGroup.getHideExportButton());
						newGroup.setHideImportButton(tmplGroup.getHideImportButton());
						newGroup.setHideQueryButton(tmplGroup.getHideQueryButton());
						newGroup.setHideDeleteButton(tmplGroup.getHideDeleteButton());
						newGroup.setListTemplateId(newListTmplId);
						newGroup.setDetailTemplateId(newDetailTmplId);
						return saveGroup(newGroup, user);
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public List<TemplateActionTemplate> queryActionTemplates(String moduleName) {
		return getActionTemplateMap().values().stream()
				.filter(atmpl->moduleName.equals(atmpl.getModule()))
				.collect(Collectors.toList())
			;
	}

	private Map<Long, TemplateActionTemplate> getActionTemplateMap(){
		synchronized (this) {
			if(atmplMap == null) {
				logger.debug("开始初始化所有操作模板缓存数据...");
				List<TemplateActionTemplate> atmpls = aDao.queryTemplates().stream().filter(atmpl->checkModuleUsable(atmpl.getModule())).collect(Collectors.toList());
				Map<Long, List<TemplateActionFieldGroup>> tmplFieldGroupsMap = 
							CollectionUtils.toListMap(aDao.queryFieldGroups(), group->group.getTmplId());
				Map<Long, List<TemplateActionField>> groupFieldsMap = 
							CollectionUtils.toListMap(aDao.queryTemplateFields(), field->field.getGroupId());
				Map<String, List<TemplateActionTemplate>> moduleTmplMap = CollectionUtils.toListMap(atmpls, TemplateActionTemplate::getModule);
				List<TemplateActionArrayEntity> arrayEntities = aDao.queryArrayEntities();
				List<TemplateActionArrayEntityField> eFields = aDao.queryArrayEntityFields();
				
				Map<Long, List<TemplateActionArrayEntity>> entitiesMap = CollectionUtils.toListMap(arrayEntities , TemplateActionArrayEntity::getTmplFieldGroupId);
				Map<Long, List<TemplateActionArrayEntityField>> arrayEntityFieldsMap = CollectionUtils.toListMap(eFields, TemplateActionArrayEntityField::getActionArrayEntityId);
				moduleTmplMap.forEach((module, moduleAtmpls)->{
					ModuleTemplateReferData referData = getModuleTemplateReferData(module);
					for (TemplateActionTemplate atmpl : moduleAtmpls) {
						List<TemplateActionFieldGroup> fieldGroups = tmplFieldGroupsMap.get(atmpl.getId());
						HandlerActionTemplateParam data = new HandlerActionTemplateParam(fieldGroups, groupFieldsMap, entitiesMap, arrayEntityFieldsMap);
						handlerActionTemplate(atmpl, data, referData);
					}
				});
				atmplMap = CollectionUtils.toMap(atmpls, atmpl->atmpl.getId());
				logger.debug(atmplMap);
				logger.debug("初始化操作模板缓存数据完成，共缓存" + atmplMap.size() + "个操作模板");
			}
			return atmplMap;
		}
	}
	
	private static class HandlerActionTemplateParam{
		private List<TemplateActionFieldGroup> fieldGroups;
		private Map<Long, List<TemplateActionField>> groupFieldsMap;
		private Map<Long, List<TemplateActionArrayEntity>> entitiesMap;
		private Map<Long, List<TemplateActionArrayEntityField>> arrayEntityFieldsMap;
		public HandlerActionTemplateParam(List<TemplateActionFieldGroup> fieldGroups,
				Map<Long, List<TemplateActionField>> groupFieldsMap, Map<Long, List<TemplateActionArrayEntity>> entitiesMap,
				Map<Long, List<TemplateActionArrayEntityField>> arrayEntityFieldsMap) {
			super();
			this.fieldGroups = fieldGroups;
			this.groupFieldsMap = groupFieldsMap;
			this.entitiesMap = entitiesMap;
			this.arrayEntityFieldsMap = arrayEntityFieldsMap;
		}
		public List<TemplateActionFieldGroup> getFieldGroups() {
			return fieldGroups;
		}
		public Map<Long, List<TemplateActionField>> getGroupFieldsMap() {
			return groupFieldsMap;
		}
		public Map<Long, List<TemplateActionArrayEntityField>> getArrayEntityFieldsMap() {
			return arrayEntityFieldsMap;
		}
		public Map<Long, List<TemplateActionArrayEntity>> getEntitiesMap() {
			return entitiesMap;
		}
	}
	
	private void handlerActionTemplate(TemplateActionTemplate atmpl, HandlerActionTemplateParam atmplParam, ModuleTemplateReferData referData) {
		FusionContextConfig config = referData.getFusionContextConfig();
		FusionContextConfigResolver resolver = config.getConfigResolver();
		if(atmplParam.getFieldGroups() != null) {
			atmpl.setGroups(atmplParam.getFieldGroups());
			Map<Long, DictionaryComposite> compositeMap = referData.getCompositeMap();
			Map<Long, DictionaryField> fieldMap = referData.getFieldMap();
			boolean moduleEntityWritable = referData.getEntityWriatble();
			for (TemplateActionFieldGroup fieldGroup : atmplParam.getFieldGroups()) {
				List<TemplateActionField> groupFields = atmplParam.getGroupFieldsMap().get(fieldGroup.getId());
				if(groupFields != null) {
					fieldGroup.setFields(groupFields);
				}
				if(fieldGroup.getCompositeId() != null) {
					DictionaryComposite composite = compositeMap.get(fieldGroup.getCompositeId());
					fieldGroup.setComposite(composite);
					if(composite != null) {
						fieldGroup.setRelationLabelAccess(getRelationLabelAccess(composite, referData.getEntityWriatble()));
						fieldGroup.setAdditionRelationLabelAccess(getAdditionRelationLabelAccess(composite, referData.getEntityWriatble()));
					}
				}
				if(groupFields != null) {
					groupFields.forEach(groupField->{
						//通过fieldId来获得对应的字段数据
						DictionaryField field = fieldMap.get(groupField.getFieldId());
						if(field != null) {
							groupField.setFieldAccess(getFieldAccess(field, moduleEntityWritable));
							groupField.setAdditionAccess(getFieldAdditionAccess(field, moduleEntityWritable));
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
				
				List<TemplateActionArrayEntity> entities = atmplParam.getEntitiesMap().get(fieldGroup.getId());
				
				if(entities != null) {
					entities.forEach(entity->{
						if(TextUtils.hasText(entity.getRelationEntityCode())) {
							ArrayEntityProxy arrayEntityProxy = createArrayEntityProxy(atmpl.getModule(), fieldGroup.getComposite().getName(), entity.getRelationEntityCode());
							entity.setArrayEntityProxy(arrayEntityProxy);
						}
						List<TemplateActionArrayEntityField> eFields = atmplParam.getArrayEntityFieldsMap().get(entity.getId());
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
		}
	}
	
	
	
	private ArrayEntityProxy createArrayEntityProxy(String moduleName, String relationName,
			String relationEntityCode) {
		return new ArrayEntityProxy(abcService, moduleName, relationName, relationEntityCode);
	}

	@Override
	public Map<Long, Set<TemplateGroup>> getActionTemplateRelatedGroupsMap(Set<Long> atmplIds) {
		Map<Long, Set<TemplateGroup>> map = new HashMap<>();
		if(atmplIds != null) {
			for (Long atmplId : atmplIds) {
				map.put(atmplId, getActionTemplateRelatedGroups(atmplId));
			}
		}
		return map;
	}

	private Set<TemplateGroup> getActionTemplateRelatedGroups(Long atmplId) {
		return getTemplateGroupMap().values().stream()
				.filter(group -> {
					
					return false;
				}).collect(Collectors.toSet());
	}
	
	@Override
	public TemplateActionTemplate getActionTemplate(Long atmplId) {
		return getActionTemplateMap().get(atmplId);
	}
	
	void reloadActionTemplate(Long atmplId) {
		if(atmplId != null && atmplMap != null) {
			synchronized (atmplMap) {
				logger.debug("重新加载操作模板[id=" + atmplId + "]缓存数据...");
				TemplateActionTemplate atmpl = nDao.get(TemplateActionTemplate.class, atmplId);
				if(atmpl != null && checkModuleUsable(atmpl.getModule())) {
					List<TemplateActionFieldGroup> fieldGroups = aDao.getTemplateGroups(atmplId);
					Map<Long, List<TemplateActionField>> groupFieldsMap = aDao.getTemplateFieldsMap(CollectionUtils.toSet(fieldGroups, group->group.getId()));
					List<TemplateActionArrayEntity> entities = aDao.queryArrayEntities(atmplId);
					Map<Long, List<TemplateActionArrayEntity>> groupEntitiesMap = CollectionUtils.toListMap(entities, TemplateActionArrayEntity::getTmplFieldGroupId);
					Map<Long, List<TemplateActionArrayEntityField>> arrayEntityFieldsMap = aDao.queryArrayEntityFields(CollectionUtils.toSet(entities, TemplateActionArrayEntity::getId));
					HandlerActionTemplateParam data = new HandlerActionTemplateParam(fieldGroups, groupFieldsMap, groupEntitiesMap, arrayEntityFieldsMap);
					handlerActionTemplate(atmpl, data, getModuleTemplateReferData(atmpl.getModule()));
					atmplMap.put(atmplId, atmpl);
					getActionTemplateRelatedGroups(atmplId).forEach(group->{
						handlerTmplGroup(group, null, null);
					});
					logger.debug("操作模板[" + atmplId + "]缓存数据重新加载完成, 值为" + atmplId);
				}else {
					atmplMap.remove(atmplId);
					//移除缓存中引用该操作模板的模板组合
					getTemplateGroupMap().values().forEach(group->{
						List<TemplateGroupAction> actions = group.getActions();
						Iterator<TemplateGroupAction> itr = actions.iterator();
						while(itr.hasNext()) {
							TemplateGroupAction action = itr.next();
							if(atmplId.equals(action.getAtmplId())) {
								itr.remove();
							}
						}
					});
					logger.debug("从缓存数据中移除列表模板[id=" + atmplId + "]");
				}
			}
		}
	}
	
	@Override
	public void removeActionTemplate(Long tmplId) {
		TemplateActionTemplate atmpl = new TemplateActionTemplate();
		atmpl.setId(tmplId);
		nDao.remove(atmpl);
		reloadActionTemplate(tmplId);
	}
	
	@Override
	public List<TemplateActionTemplate> getModuleActionTemplates(String moduleName) {
		return getActionTemplateMap().values().stream()
				.filter(atmpl->moduleName.equals(atmpl.getModule())).collect(Collectors.toList());
	}
	
	
	@Override
	public TemplateGroupAction getTempateGroupAction(Long actionId) {
		return getGroupActionMap().get(actionId);
	}

	private Map<Long, TemplateGroupAction> getGroupActionMap() {
		getTemplateGroupMap();
		return this.groupActionMap;
	}

	@Override
	public Long copyActionTemplate(Long atmplId, String targetModuleName) {
		Assert.notNull(atmplId);
		Assert.hasText(targetModuleName);
		TemplateActionTemplate atmpl = getActionTemplate(atmplId);
		ModuleMeta sourceModule = mService.getModule(atmpl.getModule());
		if(atmpl != null) {
			FusionContextConfig config = fFactory.getModuleConfig(targetModuleName);
			if(config != null) {
				TemplateActionTemplate newTmpl = new TemplateActionTemplate();
				//复制
				newTmpl.setModule(targetModuleName);
				newTmpl.setTitle("(复制自" + sourceModule.getTitle() + "-" + atmpl.getTitle() + ")");
				newTmpl.setCreateTime(new Date());
				newTmpl.setUpdateTime(newTmpl.getCreateTime());
				List<TemplateActionFieldGroup> groups = atmpl.getGroups();
				if(groups != null) {
					//遍历所有字段组
					for (TemplateActionFieldGroup group : groups) {
						TemplateActionFieldGroup nGroup = new TemplateActionFieldGroup();
						Map<TemplateActionField, List<TemplateActionArrayEntityField>> fieldEntityFieldListMap = new HashMap<>();
						if(group.getFields() != null) {
							//遍历字段组内的所有字段
							for (TemplateActionField field : group.getFields()) {
								//只有字段是可用的情况下才复制该字段
								if(field.getFieldAvailable()) {
									//映射获得目标模块对应的字段
									DictionaryField targetField = dictService.mapModuleField(targetModuleName, dictService.getField(atmpl.getModule(), field.getFieldId()));
									if(targetField != null) {
										TemplateActionField nField = new TemplateActionField();
										fieldEntityFieldListMap.put(nField, field.getArrayEntityFields());
										nField.setFieldId(targetField.getId());
										nField.setFieldName(targetField.getFullKey());
										nField.setTitle(field.getTitle());
										nField.setOrder(field.getOrder());
										nField.setColNum(field.getColNum());
										nField.setUnmodifiable(field.getUnmodifiable());
										nField.setViewValue(field.getViewValue());
										nField.setType(field.getType());
										nField.setValidators(field.getValidators());
										nField.setOptionGroupId(field.getOptionGroupId());
										nGroup.getFields().add(nField);
									}
								}
							}
						}
						if(group.getEntities() != null) {
							for (TemplateActionArrayEntity entity : group.getEntities()) {
								TemplateActionArrayEntity nEntity = new TemplateActionArrayEntity();
								nEntity.setIndex(entity.getIndex());
								nEntity.setRelationEntityCode(entity.getRelationEntityCode());
								nEntity.setRelationLabel(entity.getRelationLabel());
								nGroup.getEntities().add(nEntity);
							}
							if(nGroup.getFields() != null) {
								for (TemplateActionField nField : nGroup.getFields()) {
									List<TemplateActionArrayEntityField> entityFields = fieldEntityFieldListMap.get(nField);
									if(entityFields != null) {
										for (TemplateActionArrayEntityField entityField : entityFields) {
											TemplateActionArrayEntityField nEntityField = new TemplateActionArrayEntityField();
											nEntityField.setValue(entityField.getValue());
											nEntityField.setFieldId(nField.getFieldId());
											nField.getArrayEntityFields().add(nEntityField);
										}
									}
								}
							}
						}
						if(group.getComposite() != null) {
							try {
								//如果字段组的composite不为空的话，那么就要根据新模板中已经匹配好的fields推断出其对应的composite
								DictionaryComposite targetGroupComposite = analyzeModuleMapComposite(targetModuleName, nGroup.getFields(), group);
								if(targetGroupComposite != null) {
									nGroup.setIsArray(targetGroupComposite.getIsArray());
									nGroup.setCompositeId(targetGroupComposite.getId());
								}
								if(group.getSelectionTemplateId() != null) {
									//复制并创建一个新的选择模板
									Long stmplId = copySeletionTemplate(targetGroupComposite, group.getSelectionTemplateId());
									if(stmplId != null) {
										nGroup.setSelectionTemplateId(stmplId);
									}
								}
							} catch (Exception e) {
								logger.error("解析字段组[" + group.getTitle() + "]的composite时发生错误，将不复制该字段组", e);
								continue;
							}
						}
						nGroup.setTitle(group.getTitle());
						nGroup.setOrder(group.getOrder());
						nGroup.setUnallowedCreate(group.getUnallowedCreate());
						nGroup.setUnmodifiable(group.getUnmodifiable());
						nGroup.setUpdateTime(newTmpl.getUpdateTime());
						newTmpl.getGroups().add(nGroup);
					}
				}
				Long newTmplId = tmplUpdateStrategyFactory.getStrategy(newTmpl).create(newTmpl);
				if(newTmplId != null) {
					reloadActionTemplate(newTmplId);
				}
				return newTmplId;
			}
		}
		return null;
	}
	
}
