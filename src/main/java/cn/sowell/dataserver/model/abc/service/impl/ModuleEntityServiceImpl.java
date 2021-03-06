package cn.sowell.dataserver.model.abc.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cho.carbon.auth.pojo.UserInfo;
import cho.carbon.auth.service.ServiceFactory;
import cho.carbon.dto.VersionQueryParameter;
import cho.carbon.entity.entity.Entity;
import cho.carbon.entity.entity.RecordEntity;
import cho.carbon.extface.dto.Version;
import cho.carbon.hc.HCFusionContext;
import cho.carbon.meta.criteria.model.ModelConJunction;
import cho.carbon.panel.Discoverer;
import cho.carbon.panel.EntitySortedPagedQueryFactory;
import cho.carbon.panel.PanelFactory;
import cho.carbon.panel.PartialRelationEnSPQFactory;
import cho.carbon.panel.StatUpDrill;
import cho.carbon.query.entity.RelationEntitySPQuery;
import cho.carbon.query.entity.SortedPagedQuery;
import cho.carbon.query.entity.factory.EnRelationCriterionFactory;
import cho.carbon.query.entity.factory.EntityConJunctionFactory;
import cho.carbon.query.entity.factory.QueryEntityParamFactory;
import cho.carbon.record.VersionEntity;
import cho.carbon.rrc.query.queryrecord.condition.QueryParameter;
import cho.carbon.stat.StatUpDrillContext;
import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.CEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.ABCNodeProxy;
import cn.sowell.datacenter.entityResolver.impl.RabcModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.RelSelectionEntityPropertyParser;
import cn.sowell.dataserver.model.abc.service.AbstractEntityQueryParameter.ArrayItemCriteria;
import cn.sowell.dataserver.model.abc.service.EntitiesQueryParameter;
import cn.sowell.dataserver.model.abc.service.EntityParserParameter;
import cn.sowell.dataserver.model.abc.service.EntityQueryParameter;
import cn.sowell.dataserver.model.abc.service.ModuleEntityService;
import cn.sowell.dataserver.model.abc.service.RelationEntitiesQueryParameter;
import cn.sowell.dataserver.model.abc.service.SelectionEntityQueyrParameter;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.modules.bean.EntityPagingIterator;
import cn.sowell.dataserver.model.modules.bean.EntityPagingQueryProxy;
import cn.sowell.dataserver.model.modules.bean.EntityQueryAdapter;
import cn.sowell.dataserver.model.modules.bean.ExportDataPageInfo;
import cn.sowell.dataserver.model.modules.pojo.EntityVersionItem;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;
import cn.sowell.dataserver.model.modules.service.view.EntityItem;
import cn.sowell.dataserver.model.modules.service.view.EntityNode;
import cn.sowell.dataserver.model.modules.service.view.EntityQuery;
import cn.sowell.dataserver.model.modules.service.view.ListEntityItem;
import cn.sowell.dataserver.model.modules.service.view.ListModuleEntityItem;
import cn.sowell.dataserver.model.modules.service.view.PagedEntityList;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListColumn;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatList;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeNode;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeTemplate;
import cn.sowell.dataserver.model.tmpl.service.ListCriteriaFactory;
import cn.sowell.dataserver.model.tmpl.service.ListTemplateService;
import cn.sowell.dataserver.model.tmpl.service.SelectionTemplateService;
import cn.sowell.dataserver.model.tmpl.service.StatListTemplateService;
import cn.sowell.dataserver.model.tmpl.service.TemplateGroupService;
import cn.sowell.dataserver.model.tmpl.service.TreeTemplateService;

@Service
public class ModuleEntityServiceImpl implements ModuleEntityService {
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	static Logger logger = Logger.getLogger(ModuleEntityServiceImpl.class);
	
	@Resource
	ListCriteriaFactory lcriteriaFactory;
	
	@Resource
	TreeTemplateService treeService;
	
	@Resource
	TemplateGroupService tmplGroupService;
	
	@Resource
	ListTemplateService ltmplService;
	
	@Resource
	SelectionTemplateService stmplService;

	@Resource
	ApplicationContext applicationContext;
	
	@Override
	public Entity getEntity(EntityQueryParameter queryParam){
		long start = System.currentTimeMillis();
		HCFusionContext context = fFactory.getModuleConfig(queryParam.getModuleName()).getCurrentContext(queryParam.getUser());
		
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		
		EntitySortedPagedQueryFactory sortedPagedQueryFactory = new EntitySortedPagedQueryFactory(context);
		lcriteriaFactory.appendArrayItemCriteriaParameter(sortedPagedQueryFactory, queryParam);
		
		Entity entity = discoverer.discover(queryParam.getEntityCode(), sortedPagedQueryFactory.getSubQueryParaMap());
		logger.debug("discover实体[code=" + queryParam.getEntityCode() + "]使用时间" + (System.currentTimeMillis() - start) + "ms");
		return entity;
	}
	
	@Override
	public ModuleEntityPropertyParser getEntityParser(EntityQueryParameter queryParam) {
		Entity entity = getEntity(queryParam);
		if(entity != null) {
			return toEntityParser(entity, new EntityParserParameter(queryParam.getModuleName(), queryParam.getUser()));
		}
		return null;
	} 
	
	
	
	@Override
	public ModuleEntityPropertyParser toEntityParser(RecordEntity entity, EntityParserParameter parameter) {
		return fFactory.getModuleResolver(parameter.getModuleName()).createParser(entity, parameter.getUser(), parameter.getPropertyGetterArgument());
	}
	
	@Override
	public RelSelectionEntityPropertyParser toRelationParser(RecordEntity entity, EntityParserParameter parameter) {
		return fFactory.getModuleResolver(parameter.getModuleName()).createRelationParser(entity, parameter.getRelationName(), parameter.getUser());
	}
	
	@Override
	public RabcModuleEntityPropertyParser toRabcEntityParser(RecordEntity entity, EntityParserParameter parameter) {
		return fFactory.getModuleResolver(parameter.getModuleName()).createRabcEntityParser(entity, parameter.getUser(), parameter.getPropertyGetterArgument());
	}
	
	private EntityNode toEntityNode(ModuleEntityPropertyParser parser, TemplateTreeNode nodeTmpl) {
		if(parser != null) {
			EntityNode node = new EntityNode(parser);
			String text = treeService.parserNodeText(nodeTmpl.getText(), parser);
			node.setText(text);
			String nodeColor = nodeTmpl.getNodeColor();
			node.setNodeColor(nodeColor);
			return node;
		}
		return null;
	}
	
	private EntityItem toEntityItem(CEntityPropertyParser parser, TemplateSelectionTemplate selectionTemplate) {
		if(parser != null) {
			List<TemplateSelectionColumn> cols = selectionTemplate.getColumns();
			ListEntityItem item = new ListEntityItem(parser);
			for (TemplateSelectionColumn col : cols) {
				if(col.getFieldKey() != null) {
					String val = parser.getFormatedProperty(col.getFieldKey());
					item.putCell(col.getId().toString(), val);
				}
			}
			return item;
		}
		return null;
	}
	
	private EntityItem toEntityItem(ModuleEntityPropertyParser parser, AbstractListTemplate<? extends AbstractListColumn, ?> ltmpl) {
		if(parser != null) {
			List<? extends AbstractListColumn> cols = ltmpl.getColumns();
			ListModuleEntityItem item = new ListModuleEntityItem(parser);
			for (AbstractListColumn col : cols) {
				if(col.getFieldKey() != null) {
					String val = parser.getFormatedProperty(col.getFieldKey());
					item.putCell(col.getId().toString(), val);
				}
			}
			return item;
		}
		return null;
	}
	
	@Resource
	StatListTemplateService statListTemplateService;
	
	@Override
	public List<EntityItem> convertEntityItems(PagedEntityList el) {
		Assert.notNull(el, "PagedEntityList不能为空");
		EntityQuery query = el.getQuery();
		List<CEntityPropertyParser> parsers = el.getParsers();
		if(query.getNodeTemplate() != null) {
			return CollectionUtils.toList(parsers, parser->toEntityNode((ModuleEntityPropertyParser) parser, query.getNodeTemplate()));
		}else if(query.getSelectionTemplate() != null) {
			return CollectionUtils.toList(parsers, parser->toEntityItem(parser, query.getSelectionTemplate()));
		}else if(query.getTemplateGroup() != null) {
			TemplateListTemplate ltmpl = ltmplService.getTemplate(query.getTemplateGroup().getListTemplateId());
			return CollectionUtils.toList(parsers, parser->toEntityItem((ModuleEntityPropertyParser)parser, ltmpl));
		}else if(query.getStatViewTemplate() != null) {
			TemplateStatList ltmpl = statListTemplateService.getTemplate(query.getStatViewTemplate().getStatListTemplateId());
			return CollectionUtils.toList(parsers, parser->toEntityItem((ModuleEntityPropertyParser)parser, ltmpl));
		}
		return null;
	}
	
	

	@Override
	public ModuleEntityPropertyParser getHistoryEntityParser(EntityQueryParameter queryParam, String versionCode, Date historyTime) {
		HCFusionContext context = fFactory.getModuleConfig(queryParam.getModuleName()).getCurrentContext(queryParam.getUser());
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		VersionEntity vEntity = null;
		if(versionCode != null) {
			vEntity = discoverer.track(versionCode);
			//tracker = discoverer.track(BigInteger.valueOf(historyId));
		}else if(queryParam.getEntityCode() != null) {
			discoverer.track(queryParam.getEntityCode(), historyTime);
			vEntity = discoverer.track(queryParam.getEntityCode(), historyTime);
		}else {
			logger.error("historyId以及entityCode不能都为null！！！");
		}
		return trackEntityParser(discoverer, vEntity, queryParam);
		
	}

	private ModuleEntityPropertyParser trackEntityParser(Discoverer discoverer, VersionEntity vEntity, EntityQueryParameter queryParam) {
		if(vEntity != null) {
			Entity entity = vEntity.getEntity();
			ModuleEntityPropertyParser parser = toEntityParser(entity, new EntityParserParameter(queryParam.getModuleName(), queryParam.getUser(), discoverer));
			return parser;
		}else {
			return null;
		}
	}
	
	@Override
	public EntityPagingIterator queryExportIterator(EntitiesQueryParameter param, ExportDataPageInfo ePageInfo) {
		//String moduleName = param.getModuleName();
		PageInfo pageInfo = ePageInfo.getPageInfo();
		//BizFusionContext context = fFactory.getModuleConfig(moduleName).getCurrentContext(param.getUser());
		//EntityCriteriaFactory cf = lcriteriaFactory.appendCriterias(param.getMainCriterias(), moduleName, context);
		EntityPagingQueryProxy proxy = getEntityExportQueryProxy(param, ePageInfo);
		int dataCount = pageInfo.getPageSize();
		int startPageNo = pageInfo.getPageNo();
		int totalCount = proxy.getTotalCount();
		int ignoreCount = 0;
		if(totalCount < pageInfo.getPageSize()) {
			dataCount = totalCount;
			startPageNo = 1;
		}
		if("all".equals(ePageInfo.getScope())){
			dataCount = proxy.getTotalCount();
			startPageNo = 1;
			if(ePageInfo.getRangeStart() != null){
				ignoreCount = ePageInfo.getRangeStart() - 1;
				if(ePageInfo.getRangeEnd() != null && ePageInfo.getRangeEnd() < dataCount){
					dataCount = ePageInfo.getRangeEnd() - ePageInfo.getRangeStart() + 1;
				}else{
					dataCount -= ePageInfo.getRangeStart() - 1;
				}
			}else if(ePageInfo.getRangeEnd() != null && ePageInfo.getRangeEnd() < dataCount){
				dataCount = ePageInfo.getRangeEnd();
			}
		}
		return new EntityPagingIterator(totalCount, dataCount, ignoreCount, startPageNo, param.getUser(), proxy);
	}
	
	
	
	@Override
	public EntityPagingQueryProxy getEntityExportQueryProxy(EntitiesQueryParameter param, ExportDataPageInfo ePageInfo) {
		FusionContextConfig config = fFactory.getModuleConfig(param.getModuleName());
		HCFusionContext context = config.getCurrentContext(param.getUser());
		//Discoverer discoverer=PanelFactory.getDiscoverer(context);
		
		
		//EntitySortedPagedQuery sortedPagedQuery = discoverer.discover(param.getMainCriterias(), "编辑时间", param.getCriteriasMap());
		
		EntitySortedPagedQueryFactory entitySortedPagedQueryFactory = new EntitySortedPagedQueryFactory(context);
		QueryEntityParamFactory criteriaFactory = entitySortedPagedQueryFactory.getHostParamFactory();
		
		Assert.isTrue(!TextUtils.hasText(param.getRelationName()), "relationName只能为空");
		if(param.getConjunctionFactoryConsumer() != null) {
			param.getConjunctionFactoryConsumer().accept(criteriaFactory.getEntityConJunctionFactory());
		}
		criteriaFactory.getEnSortedColumnFactory().addSortedColumn("编辑时间");
		
		SortedPagedQuery<Entity> sortedPagedQuery = entitySortedPagedQueryFactory.getEntityQuery();
		
		PageInfo pageInfo = ePageInfo.getPageInfo();
		if("all".equals(ePageInfo.getScope())){
			return new EntityQueryAdapter(sortedPagedQuery, config.getConfigResolver(), ePageInfo.getQueryCacheCount());
		}else{
			return new EntityQueryAdapter(sortedPagedQuery, config.getConfigResolver(), pageInfo.getPageSize());
		}
	}
	
	@Override
	public Map<String, RelSelectionEntityPropertyParser> queryRelationEntityParsers(EntitiesQueryParameter param) {
		Map<String, RelSelectionEntityPropertyParser> map = new LinkedHashMap<>();
		if(param.getEntityCodes() != null && !param.getEntityCodes().isEmpty() && param.getRelationName() != null) {
			for (String code : param.getEntityCodes()) {
				EntityQueryParameter entityQueryParam = new EntityQueryParameter(param.getModuleName(), code, param.getRelationName(), param.getUser());
				RelSelectionEntityPropertyParser parser = getRelationEntityParser(entityQueryParam);
				if(parser != null) {
					map.put(code, parser);
				}
			}
		}
		return map;
	}
	
	@Override
	public RelSelectionEntityPropertyParser getRelationEntityParser(EntityQueryParameter param) {
		return fFactory.getModuleResolver(param.getModuleName()).
				createRelationParser(
						getModuleRelationEntity(param), 
						param.getRelationName(), 
						param.getUser());
	}
	
	@Override
	public Entity getModuleRelationEntity(EntityQueryParameter entityQueryParam) {
		HCFusionContext context = fFactory.getModuleConfig(entityQueryParam.getModuleName()).createRelationContext(entityQueryParam.getRelationName(), entityQueryParam.getUser());
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		Entity result=discoverer.discover(entityQueryParam.getEntityCode());
		return result;
	}
	
	@Override
	public List<EntityVersionItem> queryHistory(EntityQueryParameter param, Integer pageNo, Integer pageSize) {
		HCFusionContext context = fFactory.getModuleConfig(param.getModuleName()).getCurrentContext(param.getUser());
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		
		VersionQueryParameter vParam = new VersionQueryParameter(param.getEntityCode());
		vParam.setOffset((pageNo - 1) * pageSize);
		vParam.setSize(pageSize);
		List<Version> historyList = discoverer.trackVersion(vParam);
		List<EntityVersionItem> list = new ArrayList<EntityVersionItem>();
		historyList.forEach(history->{
			EntityVersionItem item = new EntityVersionItem();
			item.setVersionCode(history.getVersionCode());
			item.setTime(history.getVersionTime());
			item.setUserName(toUserName(history.getUserId()));
			item.setDesc(history.getDescripation());
			list.add(item);
		});
		return list;
	}
	
	@Override
	public EntityVersionItem getLastHistoryItem(EntityQueryParameter param) {
		List<EntityVersionItem> histories = queryHistory(param, 1, 1);
		if(histories != null && !histories.isEmpty()) {
			return histories.get(0);
		}
		return null;
	}
	
	private String toUserName(String usergroupId) {
		UserInfo user = ServiceFactory.getUserInfoService().getUserInfo(usergroupId);
		if(user != null) {
			return user.getUserName();
		}else {
			return "未知用户";
		}
	}

	@Override
	public String fuseEntity(EntityQueryParameter param, Map<String, Object> entityMap) {
		FusionContextConfig config = fFactory.getModuleConfig(param.getModuleName());
		String code = (String) entityMap.remove(config.getCodeAttributeName());
		entityMap.remove(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL);
		if(TextUtils.hasText(code)) {
			param.setEntityCode(code);
			delete(param);
		}
		return mergeEntity(param, entityMap);
	}

	@Override
	public String mergeEntity(EntityQueryParameter param, Map<String, Object> entityMap) {
		HCFusionContext context = fFactory.getModuleConfig(param.getModuleName()).getCurrentContext(param.getUser());
		
		EntitySortedPagedQueryFactory sortedPagedQueryFactory = new EntitySortedPagedQueryFactory(context);
		lcriteriaFactory.appendArrayItemCriteriaParameter(sortedPagedQueryFactory, param);
		
		Map<Integer, QueryParameter> paramMap = sortedPagedQueryFactory.getSubQueryParaMap();
		Map<Integer, ModelConJunction> conjunctionMap = new LinkedHashMap<Integer, ModelConJunction>();
		for (Entry<Integer, QueryParameter> paramEntry : paramMap.entrySet()) {
			QueryParameter queryParam = paramEntry.getValue();
			conjunctionMap.put(paramEntry.getKey(), queryParam.getJunction());
		}
				
		return fFactory.getModuleResolver(param.getModuleName()).saveEntity(entityMap, null, param.getUser(), conjunctionMap);
	}
	
	@Override
	public void delete(EntityQueryParameter param) {
		if(param.getEntityCode() != null) {
			FusionContextConfig config = fFactory.getModuleConfig(param.getModuleName());
			config.removeEntity(param.getEntityCode(), param.getUser());
		}
	}
	
	@Override
	public void remove(EntitiesQueryParameter param) {
		if(!param.getEntityCodes().isEmpty()) {
			FusionContextConfig config = fFactory.getModuleConfig(param.getModuleName());
			for (String entityCode : param.getEntityCodes()) {
				config.removeEntity(entityCode, param.getUser());
			}
		}
	}
	
	
	@Override
	public SortedPagedQuery<Entity> getNormalSortedEntitiesQuery(EntitiesQueryParameter param) {
		EntitySortedPagedQueryFactory entitySortedPagedQueryFactory = getEntitySortedPagedQueryFactory(param);
		return entitySortedPagedQueryFactory.getEntityQuery();
	}
	
	@Override
	public SortedPagedQuery<RecordEntity> getQuickSortedEntitiesQuery(EntitiesQueryParameter param) {
		EntitySortedPagedQueryFactory entitySortedPagedQueryFactory = getEntitySortedPagedQueryFactory(param);
		SortedPagedQuery<RecordEntity> query = entitySortedPagedQueryFactory.getRecordEntityQuery();
		if(param.getPageInfo() != null) {
			query.setPageSize(param.getPageInfo().getPageSize());
		}
		return query;
	}
	
	private EntitySortedPagedQueryFactory getEntitySortedPagedQueryFactory(EntitiesQueryParameter param) {
		String moduleName = param.getModuleName();
		 HCFusionContext context = fFactory.getModuleConfig(moduleName).getCurrentContext(param.getUser());
		
		EntitySortedPagedQueryFactory entitySortedPagedQueryFactory = new EntitySortedPagedQueryFactory(context);
		QueryEntityParamFactory criteriaFactory = entitySortedPagedQueryFactory.getHostParamFactory();
		
		
		Assert.isTrue(!TextUtils.hasText(param.getRelationName()), "relationName只能为空");
		if(param.getConjunctionFactoryConsumer() != null) {
			param.getConjunctionFactoryConsumer().accept(criteriaFactory.getEntityConJunctionFactory());
		}
		
		if(param.getArrayItemCriterias() != null && !param.getArrayItemCriterias().isEmpty()) {
			for (ArrayItemCriteria aCriteria : param.getArrayItemCriterias()) {
				if(aCriteria.isRelation()) {
					QueryEntityParamFactory relationCriteriaFactory = entitySortedPagedQueryFactory.getSubEntityCriteriaFactory(aCriteria.getComposite().getName());
					lcriteriaFactory.appendCriterias(aCriteria.getCriterias(), aCriteria.getModuleName(), relationCriteriaFactory.getEntityConJunctionFactory());
				}else {
					QueryEntityParamFactory multiCriteriaFactory = entitySortedPagedQueryFactory.getSubEntityCriteriaFactory(aCriteria.getComposite().getName());
					lcriteriaFactory.appendCriterias(aCriteria.getCriterias(), aCriteria.getModuleName(), multiCriteriaFactory.getEntityConJunctionFactory());
				}
				
			}
		}
		criteriaFactory.getEnSortedColumnFactory().addSortedColumn("编辑时间");
		return entitySortedPagedQueryFactory;
	}
	
	@Override
	public SortedPagedQuery<RecordEntity> getStatSortedEntitiesQuery(EntitiesQueryParameter queryParam) {

		FusionContextConfig config =  fFactory.getModuleConfig(queryParam.getModuleName());
		HCFusionContext context = config.getCurrentContext(queryParam.getUser());
		StatUpDrillContext drillContext = new StatUpDrillContext();
		
		drillContext.setDimensions(queryParam.getStatDimensions());
		
		//根据条件的类型，归放到before和after中
		List<NormalCriteria> beforeCriterias = new ArrayList<>();
		List<NormalCriteria> afterCriterias = new ArrayList<>();
		
		List<NormalCriteria> nCriterias = queryParam.getStatNormalCriterias();
		if(nCriterias != null) {
			for (NormalCriteria nCriteria : nCriterias) {
				switch (nCriteria.getFilterOccasion()) {
				case TemplateStatCriteria.FILTER_OCCASION_BEFORE:
					beforeCriterias.add(nCriteria);
					break;
				case TemplateStatCriteria.FILTER_OCCASION_AFTER:
					afterCriterias.add(nCriteria);
					break;
				}
			}
		}
		
		
		EntitySortedPagedQueryFactory beforeEntitySortedPagedQueryFactory = new EntitySortedPagedQueryFactory(context);
		QueryEntityParamFactory beforeEntityCriteriaFactory = beforeEntitySortedPagedQueryFactory.getHostParamFactory();
		lcriteriaFactory.appendCriterias(beforeCriterias, queryParam.getModuleName(), beforeEntityCriteriaFactory.getEntityConJunctionFactory());
		
		drillContext.setBeforeJunction(beforeEntityCriteriaFactory.getConJunction());
		
		EntitySortedPagedQueryFactory afterEntitySortedPagedQueryFactory = new EntitySortedPagedQueryFactory(context);
		QueryEntityParamFactory afterEntityCriteriaFactory = afterEntitySortedPagedQueryFactory.getHostParamFactory();
		lcriteriaFactory.appendCriterias(afterCriterias, queryParam.getModuleName(), afterEntityCriteriaFactory.getEntityConJunctionFactory());
		drillContext.setAfterJunction(afterEntityCriteriaFactory.getConJunction());
		
		StatUpDrill drill = PanelFactory.getStatUpDrill(context);
		//执行查询
		SortedPagedQuery<RecordEntity> query = drill.drillUp(drillContext);
		PageInfo pageInfo = queryParam.getPageInfo();
		query.setPageSize(pageInfo.getPageSize());
		return query;
	}

	@Override
	public List<RecordEntity> queryModuleEntities(EntitiesQueryParameter param){
		SortedPagedQuery<RecordEntity> query = getQuickSortedEntitiesQuery(param);
		PageInfo pageInfo = param.getPageInfo();
		return query.visitEntity(pageInfo.getPageNo());
		
	}
	
	@Override
	public RelationEntitySPQuery getRabcEntitiesQuery(RelationEntitiesQueryParameter queryParam) {
		FusionContextConfig config = fFactory.getModuleConfig(queryParam.getModuleName());
		HCFusionContext context = config.getCurrentContext(queryParam.getUser());
		
		PartialRelationEnSPQFactory relationEnSPQFactory = new PartialRelationEnSPQFactory(context, queryParam.getRelationName());
		EnRelationCriterionFactory relationCriterionFactory = relationEnSPQFactory.getEnQueryRelaParamFactory().getEnRelationCriterionFactory();
//		EntityUnRecursionCriteriaFactory unrecursionCriteriaFactory = relationEnSPQFactory.getRelationCriteriaFactory().getEntityUnRecursionCriteriaFactory();
		
		//添加父实体的code约束
		if(queryParam.getParentEntityCode() != null) {
			relationCriterionFactory.setInLeftCodes(queryParam.getParentEntityCode());
			//unrecursionCriteriaFactory.addLeftCode(queryParam.getParentEntityCode());
		}
		//关系名称过滤
		if(!queryParam.getRelationIncludeLabels().isEmpty()) {
			relationCriterionFactory.setInRelationTypes(queryParam.getRelationIncludeLabels());
		}
		if(!queryParam.getRelationExcludeLabels().isEmpty()) {
			relationCriterionFactory.setExRelationTypes(queryParam.getRelationExcludeLabels());
		}
		//添加关系筛选条件
		if(queryParam.getCriteriaFactoryConsumer() != null) {
			queryParam.getCriteriaFactoryConsumer().accept(relationEnSPQFactory.getEnQueryRelaParamFactory().getEnRelationCriterionFactory().getRightJunctionFactory());
		}
		
		
		RelationEntitySPQuery query = relationEnSPQFactory.getRStrucQuery();
		PageInfo pageInfo = queryParam.getPageInfo();
		query.setPageSize(pageInfo.getPageSize());
		return query;
	}
	
	@Override
	public SortedPagedQuery<Entity> getSelectionEntitiesQuery(SelectionEntityQueyrParameter queryParam) {
		FusionContextConfig config = fFactory.getModuleConfig(queryParam.getModuleName());
		HCFusionContext context = config.createRelationContext(queryParam.getRelationName(), queryParam.getUser());
		
		//BizFusionContext context = config.getCurrentContext(queryParam.getUser());
		
		EntitySortedPagedQueryFactory entitySortedPagedQueryFactory = new EntitySortedPagedQueryFactory(context);
		EntityConJunctionFactory conjunctionFactory = entitySortedPagedQueryFactory.getHostParamFactory().getEntityConJunctionFactory();
		
		
		//添加关系筛选条件
		if(queryParam.getConjunctionFactoryConsumer() != null) {
			queryParam.getConjunctionFactoryConsumer().accept(conjunctionFactory);
		}
		
		SortedPagedQuery<Entity> query = entitySortedPagedQueryFactory.getEntityQuery();
		PageInfo pageInfo = queryParam.getPageInfo();
		query.setPageSize(pageInfo.getPageSize());
		return query;
	}
	
	
	
	
	@Override
	public void wrapSelectEntityQuery(EntityQuery query, TemplateDetailFieldGroup fieldGroup, Map<Long, String> requrestCriteriaMap) {
		if(TemplateDetailFieldGroup.DIALOG_SELECT_TYPE_STMPL.equals(fieldGroup.getDialogSelectType())) {
			TemplateSelectionTemplate stmpl = stmplService.getTemplate(fieldGroup.getSelectionTemplateId());
			if(stmpl != null) {
				query.setModuleName(stmpl.getModule())
					.setPageSize(stmpl.getDefaultPageSize())
					.setSelectionTemplate(stmpl)
					;
				query.prepare(requrestCriteriaMap, applicationContext);
			}
		}else if(TemplateDetailFieldGroup.DIALOG_SELECT_TYPE_TTMPL.equals(fieldGroup.getDialogSelectType())) {
			TemplateTreeTemplate ttmpl = treeService.getTemplate(fieldGroup.getRabcTreeTemplateId());
			if(ttmpl != null) {
				TemplateTreeNode defaultNode = treeService.getDefaultNodeTemplate(ttmpl);
				query
					.setModuleName(defaultNode.getModuleName())
					.setPageSize(10)
					.setNodeTemplate(defaultNode)
					;
				query.prepare(requrestCriteriaMap, applicationContext);
			}
		}else if(TemplateDetailFieldGroup.DIALOG_SELECT_TYPE_LTMPL.equals(fieldGroup.getDialogSelectType())) {
			TemplateGroup tmplGroup = tmplGroupService.getTemplate(fieldGroup.getRabcTemplateGroupId());
			if(tmplGroup != null) {
				TemplateListTemplate ltmpl = ltmplService.getTemplate(tmplGroup.getListTemplateId());
				query
					.setModuleName(tmplGroup.getModule())
					.setPageSize(ltmpl.getDefaultPageSize())
					.setTemplateGroup(tmplGroup)
					;
				//根据传入的条件和约束开始初始化查询对象，但还不获取实体数据
				query.prepare(requrestCriteriaMap, applicationContext);
			}
		}
		
	}

	@Override
	public Map<String, RelSelectionEntityPropertyParser> loadEntities(Set<String> codeSet,
			TemplateDetailFieldGroup fieldGroup, UserIdentifier user) {
			DictionaryComposite composite = fieldGroup.getComposite();
			EntitiesQueryParameter param = new EntitiesQueryParameter(composite.getModule(), user);
			param.setRelationName(composite.getRelationKey());
			param.setEntityCodes(codeSet);
			return queryRelationEntityParsers(param);
		
	}

}
