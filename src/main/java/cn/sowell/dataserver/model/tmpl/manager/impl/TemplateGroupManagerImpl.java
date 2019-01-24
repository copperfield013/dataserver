package cn.sowell.dataserver.model.tmpl.manager.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.cachable.manager.AbstractModuleCacheManager;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.tmpl.dao.TemplateGroupDao;
import cn.sowell.dataserver.model.tmpl.manager.DetailTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.DictionaryFilterManager;
import cn.sowell.dataserver.model.tmpl.manager.ListTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.TemplateGroupManager;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToTemplateGroup;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToTemplateGroup.PreparedToTemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupAction;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupDictionaryFilter;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupPremise;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;
import cn.sowell.dataserver.model.tmpl.strategy.NormalDaoSetUpdateStrategy;

@Component
public class TemplateGroupManagerImpl 
	extends AbstractModuleCacheManager<TemplateGroup, TemplateGroupDao, GlobalPreparedToTemplateGroup, PreparedToTemplateGroup> 
	implements TemplateGroupManager{
	
	@Resource
	ListTemplateManager lManager;
	
	@Resource
	DetailTemplateManager dManager;
	
	@Resource
	DictionaryFilterManager difilterManager;
	
	private Map<Long, TemplateGroupAction> groupActionMap = new HashMap<>();
	
	private Map<Long, TemplateGroupDictionaryFilter> tmplFilterMap = new HashMap<>();
	
	static Logger logger = Logger.getLogger(TemplateGroupManagerImpl.class);
	
	
	@Autowired
	protected TemplateGroupManagerImpl(@Autowired TemplateGroupDao dao, @Autowired ModuleCachableMetaSupportor metaSupportor) {
		super(dao, metaSupportor);
	}

	@Override
	public TemplateGroupDictionaryFilter getDictionaryFilter(long tmplFilterId) {
		getCachableMap();
		return tmplFilterMap.get(tmplFilterId);
		
	}

	@Override
	protected GlobalPreparedToTemplateGroup getGlobalPreparedToCache() {
		Map<Long, List<TemplateGroupPremise>> premisesMap = CollectionUtils.toListMap(getDao().queryPremises(), TemplateGroupPremise::getGroupId);
		Map<Long, List<TemplateGroupAction>> groupActionsMap = CollectionUtils.toListMap(getDao().queryActions(), TemplateGroupAction::getGroupId);
		Map<Long, TemplateGroupDictionaryFilter> templateFilterMap = CollectionUtils.toMap(getDao().queryGroupDictionaryFilters(), TemplateGroupDictionaryFilter::getId);
		GlobalPreparedToTemplateGroup gp = new GlobalPreparedToTemplateGroup();
		gp.setPremisesMap(premisesMap);
		gp.setGroupActionsMap(groupActionsMap);
		gp.setTemplateFilterMap(templateFilterMap);
		return gp;
	}

	@Override
	protected PreparedToTemplateGroup extractPrepare(GlobalPreparedToTemplateGroup globalPreparedToCache,
			TemplateGroup group) {
		PreparedToTemplateGroup prepare = new PreparedToTemplateGroup();
		prepare.setPremises(globalPreparedToCache.getPremisesMap().get(group.getId()));
		prepare.setActions(globalPreparedToCache.getGroupActionsMap().get(group.getId()));
		globalPreparedToCache.getTemplateFilterMap().values().forEach(tFilter->{
			if(group.getImportDictionaryFilterId() != null && group.getImportDictionaryFilterId().equals(tFilter.getId())) {
				prepare.setImportDictionaryFilter(tFilter);
			}else if(group.getExportDictionaryFilterId() != null && group.getExportDictionaryFilterId().equals(tFilter.getId())) {
				prepare.setExportDictionaryFilter(tFilter);
			}
		});
		return prepare; 
	}
	
	@Override
	protected PreparedToTemplateGroup getPreparedToCache(TemplateGroup group) {
		List<TemplateGroupPremise> premises = getDao().queryPremises(group.getId());
		List<TemplateGroupAction> actions = getDao().queryActions(group.getId());
		PreparedToTemplateGroup prepared = new PreparedToTemplateGroup();
		prepared.setPremises(premises);
		prepared.setActions(actions);
		if(group.getImportDictionaryFilterId() != null) {
			TemplateGroupDictionaryFilter tFilter = getDao().getNormalOperateDao().get(TemplateGroupDictionaryFilter.class, group.getImportDictionaryFilterId());
			if(tFilter != null) {
				prepared.setImportDictionaryFilter(tFilter);
			}
		}
		if(group.getExportDictionaryFilterId() != null) {
			TemplateGroupDictionaryFilter tFilter = getDao().getNormalOperateDao().get(TemplateGroupDictionaryFilter.class, group.getExportDictionaryFilterId());
			if(tFilter != null) {
				prepared.setExportDictionaryFilter(tFilter);
			}
		}
		return prepared;
	}

	@Override
	protected void handlerCache(TemplateGroup group, PreparedToTemplateGroup prepare) {
		Long ltmplId = group.getListTemplateId(),
				dtmplId = group.getDetailTemplateId();
		TemplateListTemplate ltmpl = lManager.get(ltmplId);
		Assert.notNull(ltmpl, "模板组合[id=" + group.getId() + "]的列表模板[id=" + ltmplId + "]不存在");
		TemplateDetailTemplate dtmpl = dManager.get(dtmplId);
		Assert.notNull(dtmpl, "模板组合[id=" + group.getId() + "]的详情模板[id=" + dtmplId + "]不存在");
		group.setListTemplateTitle(ltmpl.getTitle());
		group.setDetailTemplateTitle(dtmpl.getTitle());
		if(prepare != null) {
			if(prepare.getPremises() != null) {
				group.setPremises(prepare.getPremises());
				prepare.getPremises().forEach(premise->{
					DictionaryField field = prepare.getReferData().getFieldMap().get(premise.getFieldId());
					if(field != null) {
						premise.setFieldTitle(field.getTitle());
						premise.setFieldName(field.getFullKey());
					}
				});
			}
			if(prepare.getActions() != null) {
				group.setActions(prepare.getActions());
				this.groupActionMap.putAll(CollectionUtils.toMap(prepare.getActions(), TemplateGroupAction::getId));
			}
			if(prepare.getImportDictionaryFilter() != null) {
				group.setImportDictionaryFilter(prepare.getImportDictionaryFilter());
				group.getImportDictionaryFilter().setTmplGroupId(group.getId());
				if(group.getImportDictionaryFilter().getFilterId() != null) {
					group.getImportDictionaryFilter().setFilter(difilterManager.get(group.getImportDictionaryFilter().getFilterId()));
				}
				this.tmplFilterMap.put(prepare.getImportDictionaryFilter().getId(), prepare.getImportDictionaryFilter());
			}
			if(prepare.getExportDictionaryFilter() != null) {
				group.setExportDictionaryFilter(prepare.getExportDictionaryFilter());
				group.getExportDictionaryFilter().setTmplGroupId(group.getId());
				if(group.getExportDictionaryFilter().getFilterId() != null) {
					group.getExportDictionaryFilter().setFilter(difilterManager.get(group.getExportDictionaryFilter().getFilterId()));
				}
				this.tmplFilterMap.put(prepare.getExportDictionaryFilter().getId(), prepare.getExportDictionaryFilter());
			}
		}
	}
	
	@Override
	protected void afterReloadCache(TemplateGroup group) {
		triggerTemplateGroupReloadEvent(group);
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

	@Override
	protected TemplateGroup createCachablePojo() {
		return new TemplateGroup();
	}

	@Override
	protected Long doCreate(TemplateGroup group) {
		group.setImportDictionaryFilterId(mergeDifilter(group.getImportDictionaryFilter()));
		group.setExportDictionaryFilterId(mergeDifilter(group.getExportDictionaryFilter()));
		//创建模板组合
		group.setCreateTime(group.getUpdateTime());
		group.setKey(TextUtils.hasText(group.getKey())? group.getKey(): TextUtils.uuid(5, 36));
		Long groupId = getDao().getNormalOperateDao().save(group);
		
		if(group.getPremises() != null) {
			group.getPremises().forEach(premise->{
				premise.setGroupId(groupId);
				getDao().getNormalOperateDao().save(premise);
			});
		}
		if(group.getActions() != null) {
			group.getActions().forEach(action->{
				action.setGroupId(groupId);
				getDao().getNormalOperateDao().save(action);
			});
		}
		return groupId;
	}
	
	private Long mergeDifilter(TemplateGroupDictionaryFilter difilter) {
		if(difilter != null) {
			if(difilter.getId() != null) {
				getDao().getNormalOperateDao().update(difilter);
				return difilter.getId();
			}else {
				return getDao().getNormalOperateDao().save(difilter);
			}
		}
		return null;
	}

	@Override
	protected void doUpdate(TemplateGroup group) {
		group.setImportDictionaryFilterId(mergeDifilter(group.getImportDictionaryFilter()));
		group.setExportDictionaryFilterId(mergeDifilter(group.getExportDictionaryFilter()));
		//修改模板组合
		List<TemplateGroupPremise> originPremises = getDao().queryPremises(group.getId());
		List<TemplateGroupAction> originActions = getDao().queryActions(group.getId());
		
		getDao().getNormalOperateDao().update(group);
		
		
		NormalDaoSetUpdateStrategy.build(
				TemplateGroupPremise.class, getDao().getNormalOperateDao(),
				premise->premise.getId(),
				(oPremise, premise)->{
					oPremise.setFieldValue(premise.getFieldValue());
					oPremise.setOrder(premise.getOrder());
				},premise->{
					premise.setGroupId(group.getId());
				})
			.doUpdate(new HashSet<>(originPremises ), new HashSet<>(group.getPremises()));
		
		
		NormalDaoSetUpdateStrategy.build(
				TemplateGroupAction.class, getDao().getNormalOperateDao(), 
				TemplateGroupAction::getId, 
				(oAction, action)->{
					oAction.setTitle(action.getTitle());
					oAction.setIconClass(action.getIconClass());
					oAction.setOutgoing(action.getOutgoing());
					oAction.setMultiple(action.getMultiple());
			}, action->{
					action.setGroupId(group.getId());
			})
			.doUpdate(new HashSet<>(originActions), new HashSet<>(group.getActions()));
	}
	
	@Override
	public void updateListTemplateRelatedGroups(Long ltmplId) {
		getListTemplateRelatedGroups(ltmplId).forEach(tmplGroup->{
			handlerCache(tmplGroup, null);
		});
	}
	
	@Override
	public void updateDetailTemplateRelatedGroups(Long dtmplId) {
		getDetailTemplateRelatedGroups(dtmplId).forEach(tmplGroup->{
			handlerCache(tmplGroup, null);
		});
	}
	
	@Override
	public void updateActionTemplateRelatedGroups(Long atmplId) {
		getActionTemplateRelatedGroups(atmplId).forEach(tmplGroup->{
			handlerCache(tmplGroup, null);
		});
	}
	
	

	@Override
	public Set<TemplateGroup> getDetailTemplateRelatedGroups(Long dtmplId) {
		return getCachableMap().values().stream()
				.filter(group -> dtmplId.equals(group.getDetailTemplateId())).collect(Collectors.toSet());
	}

	@Override
	public Set<TemplateGroup> getListTemplateRelatedGroups(Long ltmplId) {
		return getCachableMap().values().stream()
			.filter(group -> ltmplId.equals(group.getListTemplateId())).collect(Collectors.toSet());
	}
	
	@Override
	public Set<TemplateGroup> getActionTemplateRelatedGroups(Long atmplId) {
		return getCachableMap().values().stream()
				.filter(group -> {
					return group.getActions().stream().anyMatch(groupAction->atmplId.equals(groupAction.getAtmplId()));
				}).collect(Collectors.toSet());
	}
	
	@Override
	public Stream<TemplateGroup> allStream() {
		return getCachableMap().values().stream();
	}

	@Override
	public void updateAllGroupsListTemplate(Long listTemplateId, Long targetListTemplateId) {
		getDao().updateAllGroupsListTemplate(listTemplateId, targetListTemplateId);
		clearCache();
	}
	
	@Override
	public void updateAllGroupsDetailTemplate(Long detailTemplateId, Long targetDetailTemplateId) {
		getDao().updateAllGroupsDetailTemplate(detailTemplateId, targetDetailTemplateId);
		clearCache();
	}
	
	
	@Override
	public TemplateGroupAction getGroupAction(Long tmplActionId) {
		getCachableMap();
		return this.groupActionMap.get(tmplActionId);
	}
}
