package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.Composite;
import cn.sowell.datacenter.entityResolver.FieldConfigure;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.RelationFieldConfigure;
import cn.sowell.datacenter.entityResolver.config.UnconfiuredFusionException;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
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
	TemplateUpdateStrategyFactory tmplUpdateStrategyFactory;
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	Map<Long, TemplateGroup> tmplGroupMap;
	
	Map<Long, TemplateDetailTemplate> dtmplMap;
	
	Map<Long, TemplateListTemplate> ltmplMap;
	
	Map<Long, TemplateSelectionTemplate> stmplMap;
	
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
		if(columns != null) {
			columns.forEach(column->{
				if(column.getSpecialField() == null) {
					DictionaryField field = dictService.getField(ltmpl.getModule(), column.getFieldId());
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
				DictionaryField field = dictService.getField(ltmpl.getModule(), criteria.getFieldId());
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
					logger.debug("列表模板[" + dtmplId + "]缓存数据重新加载完成, 值为" + dtmpl);
				}else {
					dtmplMap.remove(dtmplId);
					logger.debug("从缓存数据中移除列表模板[id=" + dtmplId + "]");
				}
			}
		}
	}
	
	private void handlerDetailTemplate(TemplateDetailTemplate dtmpl, List<TemplateDetailFieldGroup> fieldGroups,
			Map<Long, List<TemplateDetailField>> groupFieldsMap) {
		FusionContextConfigResolver resolver = fFactory.getModuleResolver(dtmpl.getModule());
		dtmpl.setGroups(fieldGroups);
		if(fieldGroups != null) {
			fieldGroups.forEach(fieldGroup->{
				List<TemplateDetailField> groupFields = groupFieldsMap.get(fieldGroup.getId());
				fieldGroup.setFields(groupFields);
				if(fieldGroup.getCompositeId() != null) {
					DictionaryComposite composite = dictService.getComposite(dtmpl.getModule(), fieldGroup.getCompositeId());
					fieldGroup.setComposite(composite);
				}
				if(groupFields != null) {
					groupFields.forEach(groupField->{
						//通过fieldId来获得对应的字段数据
						DictionaryField field = dictService.getField(dtmpl.getModule(), groupField.getFieldId());
						if(field != null) {
							groupField.setFieldName(field.getFullKey());
							groupField.setType(field.getType());
							groupField.setOptionGroupId(field.getOptionGroupId());
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
					logger.debug("模板组合[" + tmplGroupId + "]缓存数据重新加载完成, 值为" + group);
				}else {
					tmplGroupMap.remove(tmplGroupId);
					logger.debug("从缓存中移除模板组合[id=" + tmplGroupId + "]");
				}
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
	public void saveGroup(TemplateGroup group, UserIdentifier user) {
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
		if(composite != null && Composite.RELATION_ADD_TYPE.equals(composite.getAddType())) {
			stmpl.setRelationName(composite.getName());
		}
		if(columns != null) {
			columns.forEach(column->{
				if(column.getSpecialField() == null) {
					DictionaryField field = dictService.getField(stmpl.getModule(), column.getFieldId());
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
				DictionaryField field = dictService.getField(stmpl.getModule(), criteria.getFieldId());
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

}
