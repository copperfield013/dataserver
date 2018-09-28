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
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.tmpl.dao.DetailTemplateDao;
import cn.sowell.dataserver.model.tmpl.dao.ListTemplateDao;
import cn.sowell.dataserver.model.tmpl.dao.SelectionTemplateDao;
import cn.sowell.dataserver.model.tmpl.dao.TemplateGroupDao;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
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
	DictionaryService dictService;
	
	@Resource
	ModulesService mService;
	
	@Resource
	TemplateUpdateStrategyFactory tmplUpdateStrategyFactory;
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	Map<Long, TemplateGroup> tmplGroupMap;
	
	Map<Long, TemplateDetailTemplate> dtmplMap;
	
	Map<Long, TemplateListTemplate> ltmplMap;
	
	Map<Long, TemplateSelectionTemplate> stmplMap;
	
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
					handlerListTemplate(ltmpl, columns, criterias);
					ltmplMap.put(ltmplId, ltmpl);
					getListTemplateRelatedGroups(ltmplId).forEach(group->{
						handlerTmplGroup(group, null);
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
			Set<TemplateListCriteria> criterias) {
		Map<Long, DictionaryField> fieldMap = CollectionUtils.toMap(dictService.getAllFields(ltmpl.getModule()), DictionaryField::getId);
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
						if(supportFieldInputType(criteria.getInputType(), field.getType())) {
							criteria.setFieldKey(field.getFullKey());
							//只有字段存在并且字段当前类型支持当前条件的表单类型，该条件字段才可用
							//(因为条件的表单类型是创建模板时选择的，与字段类型不同，防止字段修改了类型但与条件表单类型不匹配)
							return;
						}
					}
					criteria.setFieldUnavailable();
				}else if(criteria.getCompositeId() != null) {
					criteria.setComposite(dictService.getComposite(ltmpl.getModule(), criteria.getCompositeId()));
				}
			});
			ltmpl.setCriterias(criterias);
		}
	}

	
	
	private boolean supportFieldInputType(String criteriaInputType, String fieldType) {
		Map<String, Set<String>> fieldInputTypeMap = dictService.getFieldInputTypeMap();
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
				ltmpls.forEach(ltmpl->{
					List<TemplateListColumn> columns = columnsMap.get(ltmpl.getId());
					Set<TemplateListCriteria> criterias = criteriasMap.get(ltmpl.getId());
					handlerListTemplate(ltmpl, columns, criterias);
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
					handlerDetailTemplate(dtmpl, fieldGroups, groupFieldsMap);
					dtmplMap.put(dtmplId, dtmpl);
					getDetailTemplateRelatedGroups(dtmplId).forEach(group->{
						handlerTmplGroup(group, null);
					});
					logger.debug("列表模板[" + dtmplId + "]缓存数据重新加载完成, 值为" + dtmpl);
				}else {
					dtmplMap.remove(dtmplId);
					logger.debug("从缓存数据中移除列表模板[id=" + dtmplId + "]");
				}
			}
		}
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
	
	private String getAdditionRelationLabelAccess(DictionaryComposite composite, String moduleName) {
		final String READ = NodeOpsType.READ.getName();
		if(!mService.getModuleEntityWritable(moduleName)) {
			return READ;
		}
		if(READ.equals(composite.getAccess()) || NodeOpsType.SUPPLEMENT.getName().equals(composite.getAccess())) {
			return READ;
		}else {
			return composite.getRelationLabelAccess();
		}
	}

	private String getRelationLabelAccess(DictionaryComposite composite, String moduleName) {
		final String READ = NodeOpsType.READ.getName();
		if(!mService.getModuleEntityWritable(moduleName)) {
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
	
	
	private void handlerDetailTemplate(TemplateDetailTemplate dtmpl, List<TemplateDetailFieldGroup> fieldGroups,
			Map<Long, List<TemplateDetailField>> groupFieldsMap) {
		FusionContextConfig config = fFactory.getModuleConfig(dtmpl.getModule());
		FusionContextConfigResolver resolver = config.getConfigResolver();
		dtmpl.setGroups(fieldGroups);
		if(fieldGroups != null) {
			Map<Long, DictionaryComposite> compositeMap = CollectionUtils.toMap(dictService.getAllComposites(dtmpl.getModule()), DictionaryComposite::getId);
			Map<Long, DictionaryField> fieldMap = CollectionUtils.toMap(dictService.getAllFields(dtmpl.getModule()), DictionaryField::getId);
			boolean moduleEntityWritable = mService.getModuleEntityWritable(dtmpl.getModule());
			fieldGroups.forEach(fieldGroup->{
				List<TemplateDetailField> groupFields = groupFieldsMap.get(fieldGroup.getId());
				fieldGroup.setFields(groupFields);
				if(fieldGroup.getCompositeId() != null) {
					DictionaryComposite composite = compositeMap.get(fieldGroup.getCompositeId());
					fieldGroup.setComposite(composite);
					if(composite != null) {
						fieldGroup.setRelationLabelAccess(getRelationLabelAccess(composite, dtmpl.getModule()));
						fieldGroup.setAdditionRelationLabelAccess(getAdditionRelationLabelAccess(composite, dtmpl.getModule()));
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
				dtmpls.forEach(dtmpl->{
					List<TemplateDetailFieldGroup> fieldGroups = tmplFieldGroupsMap.get(dtmpl.getId());
					handlerDetailTemplate(dtmpl, fieldGroups, groupFieldsMap);
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
		}
		return tmplId;
	}
	
	
	

	Map<Long, TemplateGroup> getTemplateGroupMap(){
		synchronized (this) {
			if(tmplGroupMap == null) {
				logger.debug("开始初始化所有模板组合缓存数据...");
				List<TemplateGroup> groups = gDao.queryGroups().stream().filter(group->checkModuleUsable(group.getModule())).collect(Collectors.toList());
				Map<Long, List<TemplateGroupPremise>> premisesMap = CollectionUtils.toListMap(gDao.queryPremises(), premise->premise.getGroupId());
				for (TemplateGroup group : groups) {
					handlerTmplGroup(group, premisesMap.get(group.getId()));
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
					handlerTmplGroup(group, premises);
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

	private void handlerTmplGroup(TemplateGroup group, List<TemplateGroupPremise> premises) {
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
					handlerSelectionTemplate(ltmpl, columns, criterias);
				});
				stmplMap = CollectionUtils.toMap(stmpls, ltmpl->ltmpl.getId());
				logger.debug(ltmplMap);
				logger.debug("初始化选择模板缓存数据完成，共缓存" + ltmplMap.size() + "个列表模板");
			}
			return stmplMap;
		}
	}
	
	private void handlerSelectionTemplate(TemplateSelectionTemplate stmpl, List<TemplateSelectionColumn> columns,
			Set<TemplateSelectionCriteria> criterias) {
		Assert.notNull(stmpl.getCompositeId());
		DictionaryComposite composite = dictService.getComposite(stmpl.getModule(), stmpl.getCompositeId());
		Map<Long, DictionaryField> fieldMap = CollectionUtils.toMap(dictService.getAllFields(stmpl.getModule()), DictionaryField::getId);
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
					if(supportFieldInputType(criteria.getInputType(), field.getType())) {
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
					handlerSelectionTemplate(stmpl, columns, criterias);
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
							if(supportFieldInputType(criteria.getInputType(), field.getType())) {
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
						if(supportFieldInputType(criteria.getInputType(), field.getType())) {
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
						newGroup.setTitle("(复制自" + sourceModule.getTitle() + "-" + tmplGroup.getTitle() + ")");
						newGroup.setModule(targetModuleName);
						newGroup.setDisabled(tmplGroup.getDisabled());
						newGroup.setHideCreateButton(tmplGroup.getHideCreateButton());
						newGroup.setHideExportButton(tmplGroup.getHideExportButton());
						newGroup.setHideImportButton(tmplGroup.getHideImportButton());
						newGroup.setListTemplateId(newListTmplId);
						newGroup.setDetailTemplateId(newDetailTmplId);
						return saveGroup(newGroup, user);
					}
				}
			}
		}
		return null;
		
	}

}
