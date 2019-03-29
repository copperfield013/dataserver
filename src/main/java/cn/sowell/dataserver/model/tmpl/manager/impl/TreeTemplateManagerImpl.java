package cn.sowell.dataserver.model.tmpl.manager.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.config.ModuleConfigStructure;
import cn.sowell.dataserver.model.cachable.manager.AbstractModuleCacheManager;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.tmpl.dao.TreeTemplateDao;
import cn.sowell.dataserver.model.tmpl.manager.TemplateGroupManager;
import cn.sowell.dataserver.model.tmpl.manager.TreeTemplateManager;
import cn.sowell.dataserver.model.tmpl.param.GlobalPreparedToTree;
import cn.sowell.dataserver.model.tmpl.param.GlobalPreparedToTree.PreparedToTree;
import cn.sowell.dataserver.model.tmpl.pojo.SuperTemplateListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeNode;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeNodeCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeRelation;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeRelationCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeTemplate;
import cn.sowell.dataserver.model.tmpl.service.ListCriteriaFactory;
import cn.sowell.dataserver.model.tmpl.strategy.NormalDaoSetUpdateStrategy;

@Component
public class TreeTemplateManagerImpl 
		extends AbstractModuleCacheManager<TemplateTreeTemplate, TreeTemplateDao, GlobalPreparedToTree, PreparedToTree>
		implements TreeTemplateManager{

	@Resource
	DictionaryService dictService;
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	@Resource
	ListCriteriaFactory lcFactory;
	
	@Resource
	TemplateGroupManager tmplGroupManager;
	
	
	@Autowired
	protected TreeTemplateManagerImpl(@Autowired TreeTemplateDao dao, 
			@Autowired ModuleCachableMetaSupportor metaSupportor) {
		super(dao, metaSupportor);
	}
	

	private Map<String, Map<Long, DictionaryField>> getModuleFieldsMap(Map<Long, List<TemplateTreeNode>> nodeListMap){
		Set<String> moduleNames = new HashSet<>();
		nodeListMap.values().forEach(nodes->{
			for (TemplateTreeNode node : nodes) {
				moduleNames.add(node.getModuleName());
			}
		});
		Map<String, List<DictionaryField>> fieldsMap = dictService.getAllFields(moduleNames);
		Map<String, Map<Long, DictionaryField>> moduleFieldsMap = new HashMap<>();
		fieldsMap.forEach((moduleName, fields)->{
			moduleFieldsMap.put(moduleName, CollectionUtils.toMap(fields, DictionaryField::getId));
		});
		
		return moduleFieldsMap;
	}
	
	@Override
	protected GlobalPreparedToTree getGlobalPreparedToCache() {
		GlobalPreparedToTree gp = new GlobalPreparedToTree();
		Map<Long, List<TemplateTreeNode>> nodeListMap = CollectionUtils.toListMap(getDao().queryAllNodes(), TemplateTreeNode::getTreeTemplateId);
		Map<Long, List<TemplateTreeRelation>> nodeRelationsMap = CollectionUtils.toListMap(getDao().queryAllRelation(), TemplateTreeRelation::getNodeId);
		Map<Long, List<TemplateTreeRelationCriteria>> relationCriteriasMap = CollectionUtils.toListMap(getDao().queryAllCriterias(), TemplateTreeRelationCriteria::getTemplateId);
		Map<Long, List<TemplateTreeNodeCriteria>> nodeCriteriasMap = CollectionUtils.toListMap(getDao().queryAllNodeCriterias(), TemplateTreeNodeCriteria::getTemplateId);
		
		gp.setModuleFieldsMap(getModuleFieldsMap(nodeListMap));
		
		gp.setNodeListMap(nodeListMap);
		gp.setNodeRelationsMap(nodeRelationsMap);
		gp.setRelationCriteriasMap(relationCriteriasMap);
		gp.setNodeCriteriasMap(nodeCriteriasMap);
		
		return gp;
	}

	@Override
	protected PreparedToTree extractPrepare(GlobalPreparedToTree globalPreparedToCache, TemplateTreeTemplate cachable) {
		PreparedToTree prepared = new PreparedToTree();
		List<TemplateTreeNode> nodeList = globalPreparedToCache.getNodeListMap().get(cachable.getId());
		if(nodeList != null) {
			prepared.setNodeList(nodeList);
			for (TemplateTreeNode node : nodeList) {
				List<TemplateTreeRelation> relations = globalPreparedToCache.getNodeRelationsMap().get(node.getId());
				if(relations != null) {
					prepared.getNodeRelationsMap().put(node.getId(), relations);
					for (TemplateTreeRelation relation : relations) {
						List<TemplateTreeRelationCriteria> criterias = globalPreparedToCache.getRelationCriteriasMap().get(relation.getId());
						prepared.getRelationCriteriasMap().put(relation.getId(), criterias);
					}
				}
			}
			if(!globalPreparedToCache.getModuleFlagSet().contains(cachable.getModule())) {
				ModuleConfigStructure structure = fFactory.getConfigStructure(cachable.getModule());
				if(structure != null) {
					Map<String, Map<String, String>> moduleRelationModuleMap = structure.analyzeModuleRelationModuleMap();
					globalPreparedToCache.getModuleRelationModuleMap().putAll(moduleRelationModuleMap);
					Map<String, List<DictionaryField>> moduleFieldMap = dictService.getAllFields(moduleRelationModuleMap.keySet());
					moduleFieldMap.forEach((module, fields)->{
						globalPreparedToCache.getModuleFieldsMap().put(module, CollectionUtils.toMap(fields, DictionaryField::getId));
					});
				}
				globalPreparedToCache.getModuleFlagSet().add(cachable.getModule());
			}
			prepared.setNodeCriteriasMap(globalPreparedToCache.getNodeCriteriasMap());
			prepared.setModuleRelationModuleMap(globalPreparedToCache.getModuleRelationModuleMap());
			prepared.setModuleFieldsMap(globalPreparedToCache.getModuleFieldsMap());
		}
		
		
		return prepared;
	}

	@Override
	protected PreparedToTree getPreparedToCache(TemplateTreeTemplate cachable) {
		PreparedToTree preared = new PreparedToTree();
		List<TemplateTreeNode> nodeList = getDao().queryNodeList(cachable.getId());
		Map<Long, List<TemplateTreeNode>> tMap = new HashMap<>();
		tMap.put(cachable.getId(), nodeList);
		preared.setModuleFieldsMap(getModuleFieldsMap(tMap));
		Set<Long> nodeIds = CollectionUtils.toSet(nodeList, TemplateTreeNode::getId);
		Map<Long, List<TemplateTreeRelation>> nodeRelationsMap = 
				getDao().queryRelationsMapByNodes(nodeIds);
		
		Set<Long> nodeRelationIds = new HashSet<>();
		for (List<TemplateTreeRelation> relations : nodeRelationsMap.values()) {
			for (TemplateTreeRelation relation : relations) {
				nodeRelationIds.add(relation.getId());
			}
		}
		Map<Long, List<TemplateTreeRelationCriteria>> relationCriteriasMap =
				getDao().queryCriteriasMapByRelations(nodeRelationIds);
		preared.setNodeList(nodeList);
		preared.setNodeCriteriasMap(getDao().queryNodeCriterias(nodeIds));
		preared.setNodeRelationsMap(nodeRelationsMap);
		preared.setRelationCriteriasMap(relationCriteriasMap);
		ModuleConfigStructure structure = fFactory.getConfigStructure(cachable.getModule());
		Map<String, Map<String, String>> moduleRelationModuleMap = structure.analyzeModuleRelationModuleMap();
		preared.setModuleRelationModuleMap(moduleRelationModuleMap);
		Map<String, List<DictionaryField>> moduleFieldMap = dictService.getAllFields(moduleRelationModuleMap.keySet());
		moduleFieldMap.forEach((module, fields)->{
			preared.getModuleFieldsMap().put(module, CollectionUtils.toMap(fields, DictionaryField::getId));
		});
		
		return preared;
	}

	@Override
	protected void handlerCache(TemplateTreeTemplate ttmpl, PreparedToTree prepareToCache) {
		List<TemplateTreeNode> nodeList = prepareToCache.getNodeList();
		Map<Long, List<TemplateTreeNodeCriteria>> nodeCriteriasMap = prepareToCache.getNodeCriteriasMap();
		Map<Long, List<TemplateTreeRelation>> nodeRelationsMap = prepareToCache.getNodeRelationsMap();
		Map<Long, List<TemplateTreeRelationCriteria>> relationCriteriasMap = prepareToCache.getRelationCriteriasMap();
		if(nodeList != null) {
			for (TemplateTreeNode node : nodeList) {
				
				List<TemplateTreeNodeCriteria> nodeCriterias = nodeCriteriasMap.get(node.getId());
				if(nodeCriterias != null) {
					handleCriteria(node.getModuleName(), nodeCriterias, prepareToCache);
					node.setCriterias(nodeCriterias);
				}
				
				List<TemplateTreeRelation> rels = nodeRelationsMap.get(node.getId());
				if(rels != null) {
					for (TemplateTreeRelation rel : rels) {
						//根据模块名和关系名获得关系对应的RABC的模块名
						String relationModuleName = prepareToCache.getRelationModuleName(node.getModuleName(), rel.getRelationName());
						if(relationModuleName != null) {
							rel.setRelationModule(relationModuleName);
							List<TemplateTreeRelationCriteria> criterias = relationCriteriasMap.get(rel.getId());
							if(criterias != null) {
								handleCriteria(relationModuleName, criterias, prepareToCache);
								rel.setCriterias(criterias);
							}
						}
					}
					node.setRelations(rels);
				}
			}
			ttmpl.setNodes(nodeList);
		}
		
	}
	
	@Override
	protected void handlerCacheAfterAllLoaded(TemplateTreeTemplate ttmpl) {
		List<TemplateTreeNode> nodes = ttmpl.getNodes();
		if(nodes != null ) {
			for (TemplateTreeNode node : nodes) {
				if(node.getTemplateGroupId() != null) {
					TemplateGroup tmplGroup = tmplGroupManager.get(node.getTemplateGroupId());
					if(tmplGroup != null) {
						node.setTemplateGroupTitle(tmplGroup.getTitle());
					}
				}
			}
		}
	}

	private void handleCriteria(String relationModuleName, List<? extends SuperTemplateListCriteria> criterias, PreparedToTree prepareToCache) {
		
		for (SuperTemplateListCriteria criteria : criterias) {
			if(criteria.getFieldId() != null) {
				Map<Long, DictionaryField> fieldMap = prepareToCache.getModuleFieldsMap().get(relationModuleName);
				DictionaryField field = fieldMap.get(criteria.getFieldId());
				if(field != null) {
					if(getMetaSupportor().supportFieldInputType(criteria.getInputType(), field.getType(), prepareToCache.getReferData().getFieldInputTypeMap())) {
						criteria.setFieldKey(field.getFullKey());
						//只有字段存在并且字段当前类型支持当前条件的表单类型，该条件字段才可用
						//(因为条件的表单类型是创建模板时选择的，与字段类型不同，防止字段修改了类型但与条件表单类型不匹配)
						continue;
					}
				}
				criteria.setFieldUnavailable();
			}
			if(criteria.getCompositeId() != null) {
				criteria.setComposite(prepareToCache.getReferData().getCompositeMap().get(criteria.getCompositeId()));
			}
		}
	}


	@Override
	protected TemplateTreeTemplate createCachablePojo() {
		return new TemplateTreeTemplate();
	}

	@Override
	protected Long doCreate(TemplateTreeTemplate ttmpl) {
		Long ttmplId = getDao().getNormalOperateDao().save(ttmpl);
		if(ttmpl != null) {
			for (TemplateTreeNode node : ttmpl.getNodes()) {
				node.setTreeTemplateId(ttmplId);
				doCreateNode(node);
			}
		}
		return ttmplId;
	}

	@Override
	protected void doUpdate(TemplateTreeTemplate ttmpl) {
		Date now = new Date();
		TemplateTreeTemplate origin = getDao().get(ttmpl.getId());
		origin.setTitle(ttmpl.getTitle());
		origin.setDefaultNodeColor(ttmpl.getDefaultNodeColor());
		origin.setMaxDeep(ttmpl.getMaxDeep());
		origin.setUpdateTime(now);
		getDao().getNormalOperateDao().update(origin);
		List<TemplateTreeNode> originNodes = getDao().queryNodeList(ttmpl.getId());
		Set<Long> nodeIds = CollectionUtils.toSet(originNodes, TemplateTreeNode::getId);
		Map<Long, List<TemplateTreeRelation>> nodeRelationMap = getDao().queryRelationsMapByNodes(nodeIds);
		Map<Long, List<TemplateTreeNodeCriteria>> nodeCriteriasMap = getDao().queryNodeCriterias(nodeIds);
		Set<Long> relationIds = new HashSet<>();
		nodeRelationMap.values().forEach(rels->{
			relationIds.addAll(CollectionUtils.toSet(rels, TemplateTreeRelation::getId));
		});
		Map<Long, List<TemplateTreeRelationCriteria>> relCriteriasMap = getDao().queryCriteriasMapByRelations(relationIds);
		NormalDaoSetUpdateStrategy.build(TemplateTreeNode.class, 
				getDao().getNormalOperateDao(), 
				TemplateTreeNode::getId, 
				(oNode, node)->{
					oNode.setTitle(node.getTitle());
					oNode.setNodeColor(node.getNodeColor());
					oNode.setOrder(node.getOrder());
					oNode.setText(node.getText());
					oNode.setSelector(node.getSelector());
					oNode.setTemplateGroupId(node.getTemplateGroupId());
					oNode.setHideDetailButton(node.getHideDetailButton());
					oNode.setHideUpdateButton(node.getHideUpdateButton());
					oNode.setIsRootNode(node.getIsRootNode());
					oNode.setIsDirect(node.getIsDirect());
					getDao().getNormalOperateDao().update(oNode);
					
					List<TemplateTreeNodeCriteria> oNodeCriterias = nodeCriteriasMap.get(oNode.getId());
					Set<TemplateTreeNodeCriteria> originNodeCriterias = new LinkedHashSet<>();
					if(oNodeCriterias != null) {
						originNodeCriterias.addAll(oNodeCriterias);
					}
					NormalDaoSetUpdateStrategy.build(TemplateTreeNodeCriteria.class, 
							getDao().getNormalOperateDao(), TemplateTreeNodeCriteria::getId, 
							(oCriteria, criteria)->{
								lcFactory.coverSupCriteriaForUpdate(oCriteria, criteria);
								getDao().getNormalOperateDao().update(oCriteria);
							}, (criteria)->{
								criteria.setTemplateId(oNode.getId());
								getDao().getNormalOperateDao().save(criteria);
							})
					.doUpdate(originNodeCriterias, new LinkedHashSet<>(node.getCriterias()));
					
					List<TemplateTreeRelation> originRelationList = nodeRelationMap.get(oNode.getId());
					Set<TemplateTreeRelation> originRelations = new LinkedHashSet<>();
					if(originRelationList != null) {
						originRelations.addAll(originRelationList);
					}
					NormalDaoSetUpdateStrategy.build(TemplateTreeRelation.class, 
							getDao().getNormalOperateDao(), 
							TemplateTreeRelation::getId, (oRelation, relation)->{
								oRelation.setTitle(relation.getTitle());
								oRelation.setOrder(relation.getOrder());
								getDao().getNormalOperateDao().update(oRelation);
								List<TemplateTreeRelationCriteria> oCriteriaList = relCriteriasMap.get(oRelation.getId());
								Set<TemplateTreeRelationCriteria> originCriterias = new LinkedHashSet<>();
								if(oCriteriaList != null) {
									originCriterias.addAll(oCriteriaList);
								}
								NormalDaoSetUpdateStrategy.build(TemplateTreeRelationCriteria.class, 
										getDao().getNormalOperateDao(), 
										TemplateTreeRelationCriteria::getId, 
										(oCriteria, criteria)->{
											lcFactory.coverSupCriteriaForUpdate(oCriteria, criteria);
											oCriteria.setCompositeId(criteria.getCompositeId());
											getDao().getNormalOperateDao().update(oCriteria);
									}, (criteria)->{
										criteria.setTemplateId(oRelation.getId());
										getDao().getNormalOperateDao().save(criteria);
									})
									.doUpdate(originCriterias, new LinkedHashSet<>(relation.getCriterias()));
							}, relation->{
								relation.setNodeId(oNode.getId());
								doCreateRelation(relation);
							})
						.doUpdate(originRelations, new LinkedHashSet<>(node.getRelations()));
				}, node->{
					node.setTreeTemplateId(ttmpl.getId());
					doCreateNode(node);
				})
			.doUpdate(new LinkedHashSet<>(originNodes), new LinkedHashSet<>(ttmpl.getNodes()));
	}


	private void doCreateNode(TemplateTreeNode node) {
		Long nodeId = getDao().getNormalOperateDao().save(node);
		List<TemplateTreeNodeCriteria> criterias = node.getCriterias();
		if(criterias != null) {
			for (TemplateTreeNodeCriteria criteria : criterias) {
				criteria.setTemplateId(nodeId);
				getDao().getNormalOperateDao().save(criteria);
			}
		}
		List<TemplateTreeRelation> relations = node.getRelations();
		if(relations != null) {
			for (TemplateTreeRelation relation : relations) {
				relation.setNodeId(nodeId);
				doCreateRelation(relation);
			}
		}
	}
	
	private void doCreateRelation(TemplateTreeRelation relation) {
		Long relationId = getDao().getNormalOperateDao().save(relation);
		List<TemplateTreeRelationCriteria> criterias = relation.getCriterias();
		
		if(criterias != null) {
			for (TemplateTreeRelationCriteria criteria : criterias) {
				criteria.setTemplateId(relationId);
				getDao().getNormalOperateDao().save(criteria);
			}
		}
	}
	
	@Override
	public List<TemplateTreeTemplate> queryByNodeModule(String nodeModule) {
		return getCachableMap().values().stream().filter(ttmpl->{
			List<TemplateTreeNode> nodes = ttmpl.getNodes();
			if(nodes != null) {
				return nodes.stream().anyMatch(node->nodeModule.equals(node.getModuleName()));
			}
			return false;
		}).collect(Collectors.toList());
	}

}
