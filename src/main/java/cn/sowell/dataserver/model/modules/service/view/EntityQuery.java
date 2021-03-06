package cn.sowell.dataserver.model.modules.service.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import com.beust.jcommander.internal.Lists;

import cho.carbon.entity.entity.Entity;
import cho.carbon.entity.entity.FreeRelationEntity;
import cho.carbon.entity.entity.RecordEntity;
import cho.carbon.panel.PanelFactory;
import cho.carbon.query.entity.RelationEntitySPQuery;
import cho.carbon.query.entity.SortedPagedQuery;
import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.CommonPageInfo;
import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.CEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.Label;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.ABCNodeProxy;
import cn.sowell.datacenter.entityResolver.impl.EntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.RabcModuleEntityPropertyParser;
import cn.sowell.dataserver.Constants;
import cn.sowell.dataserver.model.abc.service.AbstractEntityQueryParameter.ArrayItemCriteria;
import cn.sowell.dataserver.model.abc.service.EntitiesQueryParameter;
import cn.sowell.dataserver.model.abc.service.EntityParserParameter;
import cn.sowell.dataserver.model.abc.service.ModuleEntityService;
import cn.sowell.dataserver.model.abc.service.RelationEntitiesQueryParameter;
import cn.sowell.dataserver.model.abc.service.SelectionEntityQueyrParameter;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.pojo.OptionItem;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.karuiserv.match.KaruiEntityQueryCriteria;
import cn.sowell.dataserver.model.karuiserv.pojo.KaruiServ;
import cn.sowell.dataserver.model.modules.bean.EntityPagingIterator;
import cn.sowell.dataserver.model.modules.bean.EntityPagingQueryProxy;
import cn.sowell.dataserver.model.modules.bean.ExportDataPageInfo;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.Cachable;
import cn.sowell.dataserver.model.tmpl.pojo.SuperTemplateListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupPremise;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatList;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatView;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeNode;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeRelation;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeRelationCriteria;
import cn.sowell.dataserver.model.tmpl.service.ArrayItemFilterService;
import cn.sowell.dataserver.model.tmpl.service.ListCriteriaFactory;
import cn.sowell.dataserver.model.tmpl.service.ListTemplateService;
import cn.sowell.dataserver.model.tmpl.service.OpenTemplateService;
import cn.sowell.dataserver.model.tmpl.service.StatListTemplateService;

public class EntityQuery {
	//查询的唯一标识
	private String key;
	//查询的模块名
	private String moduleName;
	//用于查询树形结构时的父节点code
	private String parentEntityCode;
	//用于查询树形结构，或者是用于查询某个模块的关系对应的模块的列表时
	//列表查询的分页数据
	private PageInfo pageInfo = new CommonPageInfo();
	//列表查询和树状查询时，所传入的模板组合
	private TemplateGroup templateGroup;
	//树形查询时，构造查询结果的节点模板
	private TemplateTreeNode nodeTemplate;
	//树形查询时，查询的子节点所在的关系模板
	private TemplateTreeRelation relationTemplate;
	
	//关系查询时，解析查询的选择模板
	private TemplateSelectionTemplate selectionTemplate;
	
	//统计查询时，解析查询的统计模板
	private TemplateStatView statViewTemplate;
	
	//轻服务查询实体时，解析查询的轻服务
	private KaruiServ karuiServ;
	
	//发起查询的用户
	private UserIdentifier user;
	
	private Set<NormalCriteria> precriterias = new LinkedHashSet<>();
	/**
	 * 以下字段在执行prepare之后才会有值
	 */
	
	//可查看的列表模板的条件Map
	private Map<Long, ViewListCriteria<? extends AbstractListCriteria>> viewCriteriaMap;
	//隐含条件
	private Set<NormalCriteria> hiddenCriterias = new LinkedHashSet<>();
	//执行prepare方法之后生成的查询对象
	private SortedPagedQuery<? extends RecordEntity> sortedEntitiesQuery;
	//用于解析和转换entity
	private Function<RecordEntity, CEntityPropertyParser> parserConverter;
	private RelationEntitySPQuery relaltionEntitiesQuery;
	//用于存放列表模板中查询条件的值
	private Map<Long, String> criteriaValueMap = new HashMap<>();
	//已知的最后一页的页码（不一定是实际的最后一页页码）
	private Integer virtualEndPageNo;
	private EntitiesQueryParameter queryParam;
	private Set<Long> statDisabledColumnIds;
	
	
	
	public EntityQuery(UserIdentifier user) {
		this(TextUtils.uuid(), user);
	}
	public EntityQuery(String key, UserIdentifier user) {
		super();
		Assert.hasText(key, "EntityQuery的Key不能为空");
		Assert.notNull(user, "创建EntityQuery的User不能为Null");
		this.key = key;
		this.user = user;
	}
	public String getModuleName() {
		return moduleName;
	}
	public EntityQuery setModuleName(String moduleName) {
		this.moduleName = moduleName;
		return this;
	}
	public String getParentEntityCode() {
		return parentEntityCode;
	}
	public EntityQuery setParentEntityCode(String parentEntityCode) {
		this.parentEntityCode = parentEntityCode;
		return this;
	}
	public String getRelationName() {
		if(this.relationTemplate != null) {
			return this.relationTemplate.getRelationName();
		}else if(this.selectionTemplate != null) {
			return this.selectionTemplate.getRelationName();
		}else {
			return null;
		}
	}
	public Integer getPageSize() {
		return this.pageInfo.getPageSize();
	}
	public EntityQuery setPageSize(Integer pageSize) {
		if(this.getPageSize().intValue() != pageSize) {
			this.pageInfo.setPageSize(pageSize);
			if(this.sortedEntitiesQuery != null) {
				this.sortedEntitiesQuery.setPageSize(pageSize);
			}else if(this.relaltionEntitiesQuery != null) {
				this.relaltionEntitiesQuery.setPageSize(pageSize);
			}
		}
		return this;
	}
	public String getKey() {
		return key;
	}
	public UserIdentifier getUser() {
		return user;
	}
	public int getPageNo() {
		return this.pageInfo.getPageNo();
	}
	public EntityQuery setTemplateGroup(TemplateGroup templateGroup) {
		this.templateGroup = templateGroup;
		return this;
	}
	public Map<Long, ViewListCriteria<? extends AbstractListCriteria>> getViewCriteriaMap() {
		return viewCriteriaMap;
	}
	/**
	 * 执行获得实体之前的准备工作
	 */
	public EntityQuery prepare(Map<Long, String> requrestCriteriaMap, ApplicationContext context) {
		//重置查询对象
		beforePrepare();
		if(this.parentEntityCode != null) {
			Assert.hasText(this.getRelationName(), "查询父节点下的子节点列表时，需要设置relationName参数");
			//查询树形结构中父节点下的子节点列表
			doPrepareForTree(requrestCriteriaMap, context);
		}else if(this.selectionTemplate != null) {
			//查询关系对应模块的实体列表
			doPrepareForRelSelectionList(requrestCriteriaMap, context);
		}else if(this.templateGroup != null){
			//执行一般的列表查询
			doPrepareForNormalList(requrestCriteriaMap, context);
		}else if(this.nodeTemplate != null) {
			//执行没有关联模板组合的树形结构的根节点列表查询
			doPrepareForTreeRootList(requrestCriteriaMap, context);
		}else if(this.statViewTemplate != null) {
			doPrepareForStatList(requrestCriteriaMap, context);
		}
		return this;
		
	}
	
	
	public EntityQuery doPrepareForKaruiServ(KaruiEntityQueryCriteria qCriteria, ApplicationContext context) {
		beforePrepare();
		DictionaryService dictService = context.getBean(DictionaryService.class);
		Assert.notNull(this.karuiServ, "执行prepare时，EntityQuery的kauriServ不能为null");
		List<SuperTemplateListCriteria> tCriterias = new ArrayList<>();
		if(this.karuiServ.getListTemplate() != null) {
			TemplateListTemplate ltmpl = this.karuiServ.getListTemplate();
			if(ltmpl.getCriterias() != null) {ltmpl.getCriterias().forEach(criteria->tCriterias.add(criteria));}
		}
		
		//封装请求中传入的条件对象，与列表模板中条件对象进行整合
		//整合后的对象可以允许被外部访问（Map<Long, ViewListCriteria>）
		this.viewCriteriaMap = integrateViewCriteriaMap(this.moduleName, tCriterias, qCriteria.getRequrestLtmplCriteriaMap(), dictService);
		
		//按照“请求条件-列表隐藏条件-模板组合默认字段”的递增优先级
		//创建NormalCriteria列表
		List<NormalCriteria> nCriterias = new ArrayList<>(this.precriterias);
		this.viewCriteriaMap.values().forEach(vCriteria->{
			@SuppressWarnings("unchecked")
			ViewListCriteria<? extends SuperTemplateListCriteria> vvCriteria = (ViewListCriteria<? extends SuperTemplateListCriteria>) vCriteria;
			appendSuperTemplateListCriteria(this.moduleName, vvCriteria, nCriterias, dictService);
		});
		
		nCriterias.addAll(this.hiddenCriterias);
		
		//构造EntitiesQueryParameter对象，调用ModuleEntityService构造EntitySortedPagedQuery对象
		this.queryParam = new EntitiesQueryParameter(this.moduleName, this.user);
		this.queryParam.setPageInfo(this.pageInfo);
		if(this.karuiServ.getDetailTemplateId() != null) {
			//根据详情模板中的Composite筛选器，构造筛选条件列表ArrayItemCriteriaList
			ArrayItemFilterService arrayItemFilterService = context.getBean(ArrayItemFilterService.class);
			List<ArrayItemCriteria> aCriterias = arrayItemFilterService.getArrayItemFilterCriterias(this.karuiServ.getDetailTemplateId(), this.user);
			this.queryParam.setArrayItemCriterias(aCriterias);
		}
		
		ListCriteriaFactory lcFactory = context.getBean(ListCriteriaFactory.class);
		this.queryParam.setConjunctionFactoryConsumer(lcFactory.getNormalCriteriaFactoryConsumer(this.moduleName, nCriterias));
		ModuleEntityService entityService = context.getBean(ModuleEntityService.class);
		this.sortedEntitiesQuery = entityService.getNormalSortedEntitiesQuery(this.queryParam);
		this.sortedEntitiesQuery.setPageSize(this.pageInfo.getPageSize());
		this.sortedEntitiesQuery.setCachedPageCount(10);
		//完成准备工作
		EntityParserParameter parserParam = new EntityParserParameter(this.moduleName, this.user);
		this.parserConverter = entity->entityService.toEntityParser(entity, parserParam);
		//生成查询条件值的map
		generateCriteriaValueMap(nCriterias);
		return this;
	}
	private void doPrepareForStatList(Map<Long, String> requrestCriteriaMap, ApplicationContext appContext) {
		String moduleName = this.getModuleName();
		
		TemplateStatList statListTemplate = getTemplate(appContext, StatListTemplateService.class, this.statViewTemplate.getStatListTemplateId());
		List<TemplateStatColumn> columns = statListTemplate.getColumns();
		
		Set<Integer> fieldIds = CollectionUtils.toSet(columns, col->col.getFieldId());
		DictionaryService dictService = appContext.getBean(DictionaryService.class);
		Map<Integer, DictionaryField> fieldMap = dictService.getFieldMap(moduleName, fieldIds);
		LinkedHashSet<String> dimensions = new LinkedHashSet<String>();
		
		for (TemplateStatColumn column : columns) {
			if(!this.statDisabledColumnIds.contains(column.getId()) && column.getFieldId() != null) {
				DictionaryField field = fieldMap.get(column.getFieldId());
				if(field != null) {
					if(PanelFactory.getStatGenerator().isDimension(field.getAbcAttrCode())) {
						dimensions.add(field.getAbcAttrCode());
					}
				}
			}
		}
		
		//将其转换成通用条件对象
		List<TemplateStatCriteria> tCriterias = statListTemplate.getCriterias();
		Map<Long, TemplateStatCriteria> criteriaMap = CollectionUtils.toMap(tCriterias, TemplateStatCriteria::getId);
		ListCriteriaFactory lcriteriaFactory = appContext.getBean(ListCriteriaFactory.class);
		Map<Long, NormalCriteria> nCriteriaMap = lcriteriaFactory.getCriteriasFromRequest(new MutablePropertyValues(requrestCriteriaMap), criteriaMap);
		
		this.viewCriteriaMap = integrateViewCriteriaMap(moduleName, tCriterias, requrestCriteriaMap, dictService);
		
		this.queryParam = new EntitiesQueryParameter(moduleName, this.user);
		this.queryParam.setPageInfo(pageInfo);
		this.queryParam.setStatNormalCriterias(new ArrayList<>(nCriteriaMap.values()));
		this.queryParam.setStatDimensions(dimensions);
		
		ModuleEntityService entityService = appContext.getBean(ModuleEntityService.class);
		this.sortedEntitiesQuery = entityService.getStatSortedEntitiesQuery(queryParam);
		
		//完成准备工作
		EntityParserParameter parserParam = new EntityParserParameter(this.moduleName, this.user);
		this.parserConverter = entity->entityService.toEntityParser(entity, parserParam);
		//生成查询条件值的map
		generateCriteriaValueMap(queryParam.getStatNormalCriterias());
	}
	
	
	
	private void doPrepareForTreeRootList(Map<Long, String> requrestCriteriaMap, ApplicationContext context) {
		String nodeModuleName = this.nodeTemplate.getModuleName();
		//Assert.state(this.moduleName.equals(this.nodeTemplate.getModuleName()), "nodeTemplate的moduleName与this.moduleName不一致");
		DictionaryService dictService = context.getBean(DictionaryService.class);
		//获得模板组合中的默认字段、列表模板中的所有条件对象。
		List<SuperTemplateListCriteria> tCriterias = new ArrayList<>();
		if(this.nodeTemplate.getCriterias() != null) {this.nodeTemplate.getCriterias().forEach(criteria->tCriterias.add(criteria));}
		
		//封装请求中传入的条件对象，与列表模板中条件对象进行整合
		//整合后的对象可以允许被外部访问（Map<Long, ViewListCriteria>）
		this.viewCriteriaMap = integrateViewCriteriaMap(nodeModuleName, tCriterias, requrestCriteriaMap, dictService);
		
		//按照“请求条件-列表隐藏条件-模板组合默认字段”的递增优先级
		//创建NormalCriteria列表
		List<NormalCriteria> nCriterias = new ArrayList<>(this.precriterias);
		this.viewCriteriaMap.values().forEach(vCriteria->{
			@SuppressWarnings("unchecked")
			ViewListCriteria<? extends SuperTemplateListCriteria> vvCriteria = (ViewListCriteria<? extends SuperTemplateListCriteria>) vCriteria;
			appendSuperTemplateListCriteria(nodeModuleName, vvCriteria, nCriterias, dictService);
		});
		
		nCriterias.addAll(this.hiddenCriterias);
		
		//构造EntitiesQueryParameter对象，调用ModuleEntityService构造EntitySortedPagedQuery对象
		EntitiesQueryParameter queryParam = new EntitiesQueryParameter(nodeModuleName, this.user);
		queryParam.setPageInfo(this.pageInfo);
		ListCriteriaFactory lcFactory = context.getBean(ListCriteriaFactory.class);
		queryParam.setConjunctionFactoryConsumer(lcFactory.getNormalCriteriaFactoryConsumer(nodeModuleName, nCriterias));
		ModuleEntityService entityService = context.getBean(ModuleEntityService.class);
		this.sortedEntitiesQuery = entityService.getQuickSortedEntitiesQuery(queryParam);
		//完成准备工作
		EntityParserParameter parserParam = new EntityParserParameter(nodeModuleName, this.user);
		this.parserConverter = entity->entityService.toEntityParser(entity, parserParam);
		//生成查询条件值的map
		generateCriteriaValueMap(nCriterias);
	}
	/**
	 * 查询关系对应模块的实体列表
	 * @param requrestCriteriaMap
	 * @param context
	 */
	private void doPrepareForRelSelectionList(Map<Long, String> requrestCriteriaMap, ApplicationContext context) {
		Assert.notNull(this.selectionTemplate, "selectionTemplate不能为空");
		
		DictionaryService dictService = context.getBean(DictionaryService.class);
		//获得模板组合中的默认字段、列表模板中的所有条件对象。
		List<TemplateSelectionCriteria> tCriterias = this.selectionTemplate.getCriterias();
		
		//封装请求中传入的条件对象，与列表模板中条件对象进行整合
		//整合后的对象可以允许被外部访问（Map<Long, ViewListCriteria>）
		this.viewCriteriaMap = integrateViewCriteriaMap(this.moduleName, tCriterias, requrestCriteriaMap, dictService);
		
		//按照“请求条件-列表隐藏条件-模板组合默认字段”的递增优先级
		//创建NormalCriteria列表
		List<NormalCriteria> nCriterias = new ArrayList<>(this.precriterias);
		this.viewCriteriaMap.values().forEach(vCriteria->{
			@SuppressWarnings("unchecked")
			ViewListCriteria<TemplateSelectionCriteria> vvCriteria = (ViewListCriteria<TemplateSelectionCriteria>) vCriteria;
			appendSelectionTemplateListCriteria(this.moduleName, vvCriteria, nCriterias, dictService);
		});
		
		
		//构造EntitiesQueryParameter对象，调用ModuleEntityService构造EntitySortedPagedQuery对象
		SelectionEntityQueyrParameter queryParam = new SelectionEntityQueyrParameter(this.moduleName, selectionTemplate.getRelationName(), this.user);
		queryParam.setPageInfo(this.pageInfo);
		ListCriteriaFactory lcFactory = context.getBean(ListCriteriaFactory.class);
		queryParam.setConjunctionFactoryConsumer(lcFactory.getNormalCriteriaFactoryConsumer(this.moduleName, nCriterias));
		ModuleEntityService entityService = context.getBean(ModuleEntityService.class);
		this.sortedEntitiesQuery = entityService.getSelectionEntitiesQuery(queryParam);
		//完成准备工作
		EntityParserParameter parserParam = new EntityParserParameter(this.moduleName, this.user);
		parserParam.setRelationName(selectionTemplate.getRelationName());
		this.parserConverter = entity->entityService.toRelationParser(entity, parserParam);
		//生成查询条件值的map
		generateCriteriaValueMap(nCriterias);
		
	}
	/**
	 * 查询树形结构中父节点下的子节点列表
	 * @param requrestCriteriaMap
	 * @param context
	 */
	private void doPrepareForTree(Map<Long, String> requrestCriteriaMap, ApplicationContext context) {
		String relationModuleName = this.relationTemplate.getRelationModule();
		//构造EntitiesQueryParameter对象，调用ModuleEntityService构造EntitySortedPagedQuery对象
		RelationEntitiesQueryParameter queryParam = new RelationEntitiesQueryParameter(this.moduleName, this.getRelationName(), this.parentEntityCode, this.user);
		ListCriteriaFactory lCriteriaFactory = context.getBean(ListCriteriaFactory.class);
		DictionaryService dictService = context.getBean(DictionaryService.class);
		List<TemplateTreeRelationCriteria> tCriterias = new ArrayList<>(),
											labelCriterias = new ArrayList<>();
		if(this.relationTemplate.getCriterias() != null) {
			for (TemplateTreeRelationCriteria criteria : this.relationTemplate.getCriterias()) {
				if(TemplateTreeRelationCriteria.FILTER_MODE_LABEL.equals(criteria.getFilterMode())){
					labelCriterias.add(criteria);
				}else {
					tCriterias.add(criteria);
				}
			}
		}
		//封装请求中传入的条件对象，与列表模板中条件对象进行整合
		//整合后的对象可以允许被外部访问（Map<Long, ViewListCriteria>）
		this.viewCriteriaMap = integrateViewCriteriaMap(relationModuleName, tCriterias,requrestCriteriaMap, dictService);
		
		List<NormalCriteria> nCriterias = new ArrayList<>(this.precriterias);
		this.viewCriteriaMap.values().forEach(vCriteria->{
			@SuppressWarnings("unchecked")
			ViewListCriteria<? extends SuperTemplateListCriteria> vvCriteria = (ViewListCriteria<? extends SuperTemplateListCriteria>) vCriteria;
			appendSuperTemplateListCriteria(relationModuleName, vvCriteria, nCriterias, dictService);
		});
		
		for (TemplateTreeRelationCriteria lCriteria : labelCriterias) {
			if(Constants.TRUE.equals(lCriteria.getIsExcludeLabel())) {
				queryParam.addRelationExcludeLabels(lCriteria.getFilterLabels());
			}else {
				queryParam.addRelationIncludeLabels(lCriteria.getFilterLabels());
			}
		}
		
		queryParam
			.setCriteriaFactoryConsumer(lCriteriaFactory.getNormalCriteriaFactoryConsumer(relationModuleName, nCriterias))
			.setPageInfo(this.pageInfo);
				
		ModuleEntityService entityService = context.getBean(ModuleEntityService.class);
		this.relaltionEntitiesQuery = entityService.getRabcEntitiesQuery(queryParam);
		//完成准备工作
		ModulesService mService = context.getBean(ModulesService.class);
		//获得关系的对应的模块 
		ModuleMeta targetRelationModule = mService.getRelationModule(this.moduleName, this.getRelationName());
		EntityParserParameter parserParam = new EntityParserParameter(targetRelationModule.getName(), this.user);
		this.parserConverter = entity->entityService.toRabcEntityParser(entity, parserParam);
		//生成查询条件值的map
		generateCriteriaValueMap(nCriterias);
	}
	
	/**
	 * 执行一般的列表查询
	 * @param requrestCriteriaMap
	 * @param context
	 */
	private void doPrepareForNormalList(Map<Long, String> requrestCriteriaMap, ApplicationContext context) {
		DictionaryService dictService = context.getBean(DictionaryService.class);
		Assert.notNull(this.templateGroup, "执行prepare时，EntityQuery的templateGroup不能为null");
		//获得模板组合中的默认字段、列表模板中的所有条件对象。
		List<TemplateGroupPremise> premises = this.templateGroup.getPremises();
		TemplateListTemplate ltmpl = getTemplate(context, ListTemplateService.class, this.templateGroup.getListTemplateId());
		List<SuperTemplateListCriteria> tCriterias = new ArrayList<>();
		if(ltmpl.getCriterias() != null) {ltmpl.getCriterias().forEach(criteria->tCriterias.add(criteria));}
		if(this.nodeTemplate != null && this.nodeTemplate.getCriterias() != null) {this.nodeTemplate.getCriterias().forEach(criteria->tCriterias.add(criteria));}
		
		//封装请求中传入的条件对象，与列表模板中条件对象进行整合
		//整合后的对象可以允许被外部访问（Map<Long, ViewListCriteria>）
		this.viewCriteriaMap = integrateViewCriteriaMap(this.moduleName, tCriterias, requrestCriteriaMap, dictService);
		
		//按照“请求条件-列表隐藏条件-模板组合默认字段”的递增优先级
		//创建NormalCriteria列表
		List<NormalCriteria> nCriterias = new ArrayList<>(this.precriterias);
		this.viewCriteriaMap.values().forEach(vCriteria->{
			@SuppressWarnings("unchecked")
			ViewListCriteria<? extends SuperTemplateListCriteria> vvCriteria = (ViewListCriteria<? extends SuperTemplateListCriteria>) vCriteria;
			appendSuperTemplateListCriteria(this.moduleName, vvCriteria, nCriterias, dictService);
		});
		premises.forEach(premise->{
			appendPremiseCriterias(premise, this.hiddenCriterias, dictService);
		});
		
		nCriterias.addAll(this.hiddenCriterias);
		
		//根据详情模板中的Composite筛选器，构造筛选条件列表ArrayItemCriteriaList
		ArrayItemFilterService arrayItemFilterService = context.getBean(ArrayItemFilterService.class);
		List<ArrayItemCriteria> aCriterias = arrayItemFilterService.getArrayItemFilterCriterias(this.templateGroup.getDetailTemplateId(), this.user);
		
		//构造EntitiesQueryParameter对象，调用ModuleEntityService构造EntitySortedPagedQuery对象
		this.queryParam = new EntitiesQueryParameter(this.moduleName, this.user);
		this.queryParam.setPageInfo(this.pageInfo);
		this.queryParam.setArrayItemCriterias(aCriterias);
		ListCriteriaFactory lcFactory = context.getBean(ListCriteriaFactory.class);
		this.queryParam.setConjunctionFactoryConsumer(lcFactory.getNormalCriteriaFactoryConsumer(this.moduleName, nCriterias));
		ModuleEntityService entityService = context.getBean(ModuleEntityService.class);
		this.sortedEntitiesQuery = entityService.getQuickSortedEntitiesQuery(this.queryParam);
		this.sortedEntitiesQuery.setPageSize(this.pageInfo.getPageSize());
		this.sortedEntitiesQuery.setCachedPageCount(10);
		//完成准备工作
		EntityParserParameter parserParam = new EntityParserParameter(this.moduleName, this.user);
		this.parserConverter = entity->entityService.toEntityParser(entity, parserParam);
		//生成查询条件值的map
		generateCriteriaValueMap(nCriterias);
	}
	
	/**
	 * 
	 * @param nCriterias
	 */
	private void generateCriteriaValueMap(List<NormalCriteria> nCriterias) {
		this.criteriaValueMap.clear();
		if(nCriterias != null) {
			for (NormalCriteria nCriteria : nCriterias) {
				if(nCriteria.getCriteriaId() != null) {
					this.criteriaValueMap.put(nCriteria.getCriteriaId(), nCriteria.getValue());
				}
			}
		}
	}
	/**
	 * 根据模板中的条件和请求传递的条件，整合并封装成条件对象
	 * @param <CRI>
	 * @param tCriterias
	 * @param requrestCriteriaMap
	 * @param dictService
	 * @return
	 */
	private <CRI extends AbstractListCriteria> Map<Long, ViewListCriteria<? extends AbstractListCriteria>> integrateViewCriteriaMap(
			String criteriaModuleName, 
			List<CRI> tCriterias, 
			Map<Long, String> requrestCriteriaMap,
			DictionaryService dictService 
			) {
		Map<Long, ViewListCriteria<? extends AbstractListCriteria>> vCriteriaMap = new HashMap<>();
		if(tCriterias != null && !tCriterias.isEmpty()) {
			//获得条件中的枚举值
			Set<Integer> fieldIds = CollectionUtils.toSet(tCriterias, AbstractListCriteria::getFieldId);
			fieldIds.remove(null);
			Set<String> criteriaFieldNames = CollectionUtils.toSet(tCriterias, AbstractListCriteria::getFieldKey);
			Map<String, Label> labelMap = dictService.getModuleLabelMap(criteriaModuleName, criteriaFieldNames);
			Map<Integer, List<OptionItem>> optionsMap = dictService.getOptionsMap(fieldIds);
			for (CRI tCriteria : tCriterias) {
				ViewListCriteria<CRI> vCriteria = new ViewListCriteria<CRI>(tCriteria);
				if(requrestCriteriaMap != null && requrestCriteriaMap.containsKey(tCriteria.getId())) {
					vCriteria.setRequestValue(requrestCriteriaMap.get(tCriteria.getId()));
					//处理请求值
					vCriteria.setValue(vCriteria.getRequestValue());
				}else {
					if(tCriteria.getDefaultValue() != null) {
						vCriteria.setValue(tCriteria.getDefaultValue());
					}
				}
				vCriteria.setSelectOptions(FormatUtils.coalesce(optionsMap.get(tCriteria.getFieldId()), Lists.newArrayList()));
				Label label = labelMap.get(tCriteria.getFieldKey());
				if(label != null) {
					vCriteria.setSelectLabels(new ArrayList<>(label.getSubdomain()));
				}
				vCriteriaMap.put(tCriteria.getId(), vCriteria);
			}
		}
		return vCriteriaMap;
	}
	/**
	 * 重置prepare数据
	 */
	private void beforePrepare() {
		this.sortedEntitiesQuery = null;
		this.relaltionEntitiesQuery = null;
		this.viewCriteriaMap = null;
		this.hiddenCriterias = new LinkedHashSet<>();
		this.parserConverter = null;
	}
	/**
	 * 将默认字段转换成条件对象并放到列表中
	 * @param premise
	 * @param nCriterias
	 * @param dictService
	 */
	private void appendPremiseCriterias(TemplateGroupPremise premise, Set<NormalCriteria> nCriterias,
			DictionaryService dictService) {
		DictionaryField field = dictService.getField(this.moduleName, premise.getFieldId());
		if(field != null) {
			NormalCriteria nCriteria = new NormalCriteria();
			nCriteria.setFieldId(field.getId());
			nCriteria.setComparator("equals");
			nCriteria.setFieldName(field.getFullKey());
			nCriteria.setValue(premise.getFieldValue());
			nCriterias.add(nCriteria);
		}
	}
	
	/**
	 * 
	 * @param vCriteria
	 * @param nCriterias
	 * @param dictService
	 */
	private void appendSuperTemplateListCriteria(String fieldModuleName, ViewListCriteria<? extends SuperTemplateListCriteria> vCriteria, List<NormalCriteria> nCriterias, DictionaryService dictService) {
		SuperTemplateListCriteria tCriteria = vCriteria.getTemplateCriteria();
		boolean hasRelation = TextUtils.hasText(this.getRelationName());
		if(tCriteria.getFieldAvailable()) {
			NormalCriteria nCriteria = new NormalCriteria();
			if(tCriteria.getFieldId() != null) {
				DictionaryField field = dictService.getField(fieldModuleName, tCriteria.getFieldId());
				if(field != null) {
					nCriteria.setCriteriaId(tCriteria.getId());
					nCriteria.setCompositeId(tCriteria.getCompositeId());
					nCriteria.setFieldId(tCriteria.getFieldId());
					String fieldName = field.getFullKey();
					if(hasRelation && fieldName.startsWith(this.getRelationName())) {
						fieldName = fieldName.substring(this.getRelationName().length() + 1);
					}
					nCriteria.setFieldName(fieldName);
					nCriteria.setComparator(tCriteria.getComparator());
					nCriteria.setRelationLabel(tCriteria.getRelationLabel());
					nCriteria.setValue(vCriteria.getValue());
					nCriterias.add(nCriteria);
				}
			}else if(tCriteria.getCompositeId() != null) {
				nCriteria.setCompositeId(tCriteria.getCompositeId());
				nCriteria.setComparator(tCriteria.getComparator());
				nCriteria.setValue(vCriteria.getValue());
				nCriterias.add(nCriteria);
			}
		}
	}
	private void appendSelectionTemplateListCriteria(String fieldModuleName, ViewListCriteria<TemplateSelectionCriteria> vCriteria, List<NormalCriteria> nCriterias, DictionaryService dictService) {
		TemplateSelectionCriteria tCriteria = vCriteria.getTemplateCriteria();
		boolean hasRelation = TextUtils.hasText(this.getRelationName());
		if(tCriteria.getFieldAvailable()) {
			NormalCriteria nCriteria = new NormalCriteria();
			if(tCriteria.getFieldId() != null) {
				DictionaryField field = dictService.getField(fieldModuleName, tCriteria.getFieldId());
				if(field != null) {
					nCriteria.setFieldId(tCriteria.getFieldId());
					String fieldName = field.getFullKey();
					if(hasRelation && fieldName.startsWith(this.getRelationName())) {
						fieldName = fieldName.substring(this.getRelationName().length() + 1);
					}
					nCriteria.setCriteriaId(tCriteria.getId());
					nCriteria.setFieldName(fieldName);
					nCriteria.setComparator(tCriteria.getComparator());
					nCriteria.setRelationLabel(tCriteria.getRelationLabel());
					nCriteria.setValue(vCriteria.getValue());
					nCriterias.add(nCriteria);
				}
			}
		}
	}
	
	private <S extends OpenTemplateService<T>, T extends Cachable> T getTemplate(ApplicationContext context, Class<S> tmplServiceClass, Long tmplId) {
		return context.getBean(tmplServiceClass).getTemplate(tmplId);
	}
	
	public CEntityPropertyParser uniqueResult() {
		this.setPageSize(1);
		PagedEntityList list = this.pageList(1);
		List<CEntityPropertyParser> parsers = list.getParsers();
		if(parsers != null && !parsers.isEmpty()) {
			return parsers.get(0);
		}
		return null;
	}
	
	public PagedEntityList list() {
		return this.pageList(this.getPageNo());
	}
	
	public PagedEntityList pageList(int pageNo){
		PagedEntityList list = new PagedEntityList(this);
		List<CEntityPropertyParser> parsers = null;
		if(this.relaltionEntitiesQuery != null) {
			List<FreeRelationEntity> rEntities = this.relaltionEntitiesQuery.visitEntity(pageNo);
			parsers = CollectionUtils.toList(rEntities, rEntity->{
				Entity entity = rEntity.getEntity();
				EntityPropertyParser parser = (EntityPropertyParser) parserConverter.apply(entity);
				if(parser instanceof RabcModuleEntityPropertyParser) {
					((RabcModuleEntityPropertyParser) parser).setRelationLabel(rEntity.getRelationTypeName());
				}
				return parser;
			});
			list.setIsEndList(!this.relaltionEntitiesQuery.hasData(pageNo + 1));
			this.virtualEndPageNo = this.relaltionEntitiesQuery.getCurrentEndCachePage();
		}else if(this.sortedEntitiesQuery != null) {
			//根据当前已经准备好的EntitySortedPagedQuery对象
			//结合分页参数pageNo和pageSize，执行查询获得Enity列表
			//调用FusionConfigResolver将Entity列表转换成Parser列表
			List<? extends RecordEntity> entities = this.sortedEntitiesQuery.visitEntity(pageNo);
			
			parsers = CollectionUtils.toList(entities, entity->{
				return this.parserConverter.apply((Entity) entity);
			});
			list.setIsEndList(!this.sortedEntitiesQuery.hasData(pageNo + 1));
			this.virtualEndPageNo = this.sortedEntitiesQuery.getCurrentEndCachePage();
		}else {
			throw new UnsupportedOperationException("成功执行prepare方法后再执行当前方法");
		}
		this.pageInfo.setPageNo(pageNo);
		list.setPageNo(pageNo);
		list.setParsers(parsers);
		
		return list;
	}
	/**
	 * 查询实体总数，该方法可能会消耗大量资源，慎用
	 * @return
	 */
	public Integer getCount() {
		if(this.sortedEntitiesQuery != null) {
			return this.sortedEntitiesQuery.getAllCount();
		}else if(this.relaltionEntitiesQuery != null) {
			return this.relaltionEntitiesQuery.getAllCount();
		}else {
			throw new UnsupportedOperationException("成功执行prepare方法后再执行当前方法");
		}
	}
	public TemplateGroup getTemplateGroup() {
		return this.templateGroup;
	}
	public TemplateTreeNode getNodeTemplate() {
		return this.nodeTemplate;
	}
	public EntityQuery setNodeTemplate(TemplateTreeNode nodeTemplate) {
		this.nodeTemplate = nodeTemplate;
		return this;
	}
	public TemplateTreeRelation getRelationTemplate() {
		return relationTemplate;
	}
	public EntityQuery setRelationTemplate(TemplateTreeRelation relationTemplate) {
		this.relationTemplate = relationTemplate;
		return this;
	}
	public TemplateSelectionTemplate getSelectionTemplate() {
		return selectionTemplate;
	}
	public EntityQuery setSelectionTemplate(TemplateSelectionTemplate selectionTemplate) {
		this.selectionTemplate = selectionTemplate;
		return this;
	}
	
	public EntityQuery addExcludeEntityCodes(Set<String> entityCodes) {
		NormalCriteria nCriteria = new NormalCriteria();
		nCriteria.setFieldName(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL);
		nCriteria.setComparator("l1n");
		nCriteria.setValue(CollectionUtils.toChain(entityCodes));
		this.precriterias.add(nCriteria);
		return this;
	}
	public Map<Long, String> getCriteriaValueMap() {
		return Collections.unmodifiableMap(this.criteriaValueMap);
	}
	public Integer getVirtualEndPageNo() {
		return this.virtualEndPageNo;
	}
	public EntityPagingIterator createExportIterator(ExportDataPageInfo ePageInfo, ApplicationContext appContext) {
		Assert.notNull(this.sortedEntitiesQuery, "需要先调用prepare方法后再调用当前方法");
		int dataCount = getPageSize();
		int startPageNo = getPageNo();
		int totalCount = getCount();
		int ignoreCount = 0;
		if(totalCount < getPageSize()) {
			dataCount = totalCount;
			startPageNo = 1;
		}
		if("all".equals(ePageInfo.getScope())){
			dataCount = totalCount;
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
		ModuleEntityService mService = appContext.getBean(ModuleEntityService.class);
		SortedPagedQuery<? extends RecordEntity> query = null;
		if(this.templateGroup != null) {
			query = mService.getNormalSortedEntitiesQuery(this.queryParam);
		}else if(this.statViewTemplate != null) {
			query = mService.getStatSortedEntitiesQuery(this.queryParam);
		}
		query.setPageSize(getPageSize());
		EntityPagingQueryProxy proxy = createEntityPagingQueryProxy(query, ePageInfo);
		EntityPagingIterator itr = new EntityPagingIterator(totalCount, dataCount, ignoreCount, startPageNo, user, proxy);
		return itr;
	}
	
	private EntityPagingQueryProxy createEntityPagingQueryProxy(SortedPagedQuery<? extends RecordEntity> query,
			ExportDataPageInfo ePageInfo) {
		
		EntityQuery _this = this;
		
		return new EntityPagingQueryProxy() {
			
			@Override
			public Set<ModuleEntityPropertyParser> load(int pageNo, UserIdentifier user) {
				List<? extends RecordEntity> entities = query.visitEntity(pageNo);
				return CollectionUtils.toSet(entities, e->(ModuleEntityPropertyParser)parserConverter.apply(e));
			}
			
			@Override
			public synchronized int getTotalCount() {
				return query.getAllCount();
			}
			
			@Override
			public int getPageSize() {
				return _this.getPageSize();
			}
		};
	}
	public EntityQuery setStatViewTemplate(TemplateStatView statViewTmpl) {
		this.statViewTemplate = statViewTmpl;
		return this;
	}
	public TemplateStatView getStatViewTemplate() {
		return this.statViewTemplate;
	}
	public EntityQuery setStatDisabledColumnIds(Set<Long> disabledColumnIds) {
		this.statDisabledColumnIds = disabledColumnIds;
		return this;
	}
	public Set<Long> getStatDisabledColumnIds() {
		return statDisabledColumnIds;
	}
	public EntityQuery setKaruiServ(KaruiServ ks) {
		this.karuiServ = ks;
		return this;
	}
	public KaruiServ getKaruiServ() {
		return karuiServ;
	}
	
	
}
