package cn.sowell.dataserver.model.tmpl.manager.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.tmpl.dao.DetailTemplateDao;
import cn.sowell.dataserver.model.tmpl.manager.DetailTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.TemplateGroupManager;
import cn.sowell.dataserver.model.tmpl.manager.TreeTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToDetailTemplate;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToDetailTemplate.PreparedToDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroupTreeNode;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeTemplate;
import cn.sowell.dataserver.model.tmpl.strategy.NormalDaoSetUpdateStrategy;

@Component
public class DetailTemplateManagerImpl 
		extends AbstractDetailTemplateManager<TemplateDetailTemplate, TemplateDetailFieldGroup, TemplateDetailField, DetailTemplateDao>
		implements DetailTemplateManager {

	private static final String FIELD_GROUP_TREE_NODES_MAP = "fieldGroupTreeNodesMap";
	@Resource
	TreeTemplateManager treeManager;
	
	@Autowired
	public DetailTemplateManagerImpl(
			@Autowired DetailTemplateDao dao,
			@Autowired ModuleCachableMetaSupportor metaSupportor, 
			@Autowired DictionaryService dictService,
			@Autowired TemplateGroupManager tmplGroupManager) {
		super(dao, metaSupportor, dictService, tmplGroupManager);
	}
	
	@Override
	protected GlobalPreparedToDetailTemplate<TemplateDetailFieldGroup, TemplateDetailField> getGlobalPreparedToCache() {
		GlobalPreparedToDetailTemplate<TemplateDetailFieldGroup, TemplateDetailField> globalPrepared = super.getGlobalPreparedToCache();
		List<TemplateDetailFieldGroupTreeNode> treeNodes = getDao().queryAllFieldGroupTreeNodes();
		globalPrepared.set(FIELD_GROUP_TREE_NODES_MAP, CollectionUtils.toListMap(treeNodes, TemplateDetailFieldGroupTreeNode::getFieldGroupId));
		return globalPrepared;
	}
	
	@Override
	protected PreparedToDetailTemplate<TemplateDetailFieldGroup, TemplateDetailField> extractPrepare(
			GlobalPreparedToDetailTemplate<TemplateDetailFieldGroup, TemplateDetailField> globalPreparedToCache,
			TemplateDetailTemplate cachable) {
		PreparedToDetailTemplate<TemplateDetailFieldGroup, TemplateDetailField> prepared = super.extractPrepare(globalPreparedToCache, cachable);
		prepared.set(FIELD_GROUP_TREE_NODES_MAP, globalPreparedToCache.get(FIELD_GROUP_TREE_NODES_MAP));;
		return prepared;
	}
	
	@Override
	protected PreparedToDetailTemplate<TemplateDetailFieldGroup, TemplateDetailField> getPreparedToCache(
			TemplateDetailTemplate dtmpl) {
		PreparedToDetailTemplate<TemplateDetailFieldGroup, TemplateDetailField> prepared = super.getPreparedToCache(dtmpl);
		List<TemplateDetailFieldGroupTreeNode> treeNodes = getDao().queryFieldGroupTreeNodes(dtmpl.getId());
		prepared.set(FIELD_GROUP_TREE_NODES_MAP, CollectionUtils.toListMap(treeNodes, TemplateDetailFieldGroupTreeNode::getFieldGroupId));
		return prepared;
	}
	
	@Override
	protected void handleFieldGroup(TemplateDetailFieldGroup fieldGroup,
			PreparedToDetailTemplate<TemplateDetailFieldGroup, TemplateDetailField> prepareToCache) {
		@SuppressWarnings("unchecked")
		Map<Long, List<TemplateDetailFieldGroupTreeNode>> fieldGroupTreeNodesMap = 
				(Map<Long, List<TemplateDetailFieldGroupTreeNode>>) prepareToCache.get(FIELD_GROUP_TREE_NODES_MAP);
		if(fieldGroupTreeNodesMap != null) {
			fieldGroup.setRabcTreeNodes(fieldGroupTreeNodesMap.get(fieldGroup.getId()));
		}
	}

	@Override
	protected void handlerCacheAfterAllLoaded(TemplateDetailTemplate dtmpl) {
		List<TemplateDetailFieldGroup> fieldGroups = dtmpl.getGroups();
		if(fieldGroups != null) {
			for (TemplateDetailFieldGroup fieldGroup : fieldGroups) {
				if(fieldGroup.getRabcTemplateGroupId() != null) {
					TemplateGroup tmplGroup = tmplGroupManager.get(fieldGroup.getRabcTemplateGroupId());
					if(tmplGroup != null) {
						fieldGroup.setRabcTemplateGroupTitle(tmplGroup.getTitle());
					}
				}
				if(fieldGroup.getRabcTreeTemplateId() != null) {
					TemplateTreeTemplate ttmpl = treeManager.get(fieldGroup.getRabcTreeTemplateId());
					if(ttmpl != null) {
						fieldGroup.setRabcTreeTemplateTitle(ttmpl.getTitle());
					}
				}
			}
		}
	}
	
	
	
	@Override
	protected TemplateDetailTemplate createCachablePojo() {
		return new TemplateDetailTemplate();
	}
	
	@Override
	protected void afterReloadCache(TemplateDetailTemplate dtmpl) {
		tmplGroupManager.updateDetailTemplateRelatedGroups(dtmpl.getId());
	}
	
	
	@Override
	protected void doUpdateFieldGroup(TemplateDetailFieldGroup originGroup, TemplateDetailFieldGroup group) {
		originGroup.setDialogSelectType(group.getDialogSelectType());
		originGroup.setRabcTreeTemplateId(group.getRabcTreeTemplateId());
		originGroup.setRabcTemplateGroupId(group.getRabcTemplateGroupId());
		originGroup.setRabcUncreatable(group.getRabcUncreatable());
		originGroup.setRabcUnupdatable(group.getRabcUnupdatable());
		originGroup.setArrayItemFilterId(group.getArrayItemFilterId());
	}
	
	@Override
	protected void handleFieldGroupAfterSave(TemplateDetailFieldGroup originGroup, TemplateDetailFieldGroup group) {
		LinkedHashSet<TemplateDetailFieldGroupTreeNode> nodes = new LinkedHashSet<>();
		if(group.getRabcTreeNodes() != null) {
			nodes.addAll(group.getRabcTreeNodes());
		}
		if(originGroup != null) {
			LinkedHashSet<TemplateDetailFieldGroupTreeNode> originNodes = new LinkedHashSet<>();
			if(originGroup.getRabcTreeNodes() != null) {
				originNodes.addAll(originGroup.getRabcTreeNodes());
			}
							
			NormalDaoSetUpdateStrategy.build(TemplateDetailFieldGroupTreeNode.class, 
					getDao().getNormalOperateDao(), TemplateDetailFieldGroupTreeNode::getNodeTemplateId, 
					(oNode, node)->{
						oNode.setOrder(node.getOrder());
					}, node->{
						node.setId(null);
						node.setFieldGroupId(originGroup.getId());
					}).doUpdate(originNodes, nodes);;
		}else {
			nodes.forEach(node->{
				node.setFieldGroupId(group.getId());
				getDao().getNormalOperateDao().save(node);
			});
		}
	}

	@Override
	public TemplateDetailFieldGroup getFieldGroup(Long groupId) {
		if(groupId != null) {
			for (TemplateDetailTemplate dtmpl : getCachableMap().values()) {
				if(dtmpl.getGroups() != null) {
					for (TemplateDetailFieldGroup group : dtmpl.getGroups()) {
						if(groupId.equals(group.getId())) {
							return group;
						}
					}
				}
			}
		}
		return null;
	}
	
}
