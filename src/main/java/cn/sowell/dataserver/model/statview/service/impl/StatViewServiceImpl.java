package cn.sowell.dataserver.model.statview.service.impl;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abc.application.BizFusionContext;
import com.abc.mapping.entity.Entity;
import com.abc.panel.EntitySortedPagedQueryFactory;
import com.abc.panel.PanelFactory;
import com.abc.panel.StatUpDrill;
import com.abc.rrc.query.criteria.EntityCriteriaFactory;
import com.abc.rrc.query.entity.EntitySortedPagedQuery;
import com.abc.stat.StatUpDrillContext;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.datacenter.entityResolver.CEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.dataserver.model.abc.service.EntityParserParameter;
import cn.sowell.dataserver.model.abc.service.ModuleEntityService;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.modules.service.view.StatListTemplateEntityView;
import cn.sowell.dataserver.model.statview.pojo.StatCriteria;
import cn.sowell.dataserver.model.statview.service.StatViewService;
import cn.sowell.dataserver.model.tmpl.manager.StatViewManager;
import cn.sowell.dataserver.model.tmpl.param.StatModuleDetail;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatList;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatView;
import cn.sowell.dataserver.model.tmpl.service.ListCriteriaFactory;
import cn.sowell.dataserver.model.tmpl.service.impl.AbstractTemplateService;

@Service
public class StatViewServiceImpl 
	extends AbstractTemplateService<TemplateStatView, StatViewManager> 
	implements StatViewService{

	@Resource
	FusionContextConfigFactory fFactory;
	
	@Resource
	DictionaryService dictService;
	
	@Resource
	ModulesService mService;
	
	@Resource
	ModuleEntityService entityService;
	
	@Resource
	ListCriteriaFactory lcriteriaFactory;
	
	@Autowired
	public StatViewServiceImpl(@Autowired StatViewManager manager) {
		super(manager);
	}
	
	@Override
	public Map<String, StatModuleDetail> getStatModuleDetail(Set<String> modules) {
		Map<String, FusionContextConfig> configMap = CollectionUtils.toMap(fFactory.getAllConfigs(), FusionContextConfig::getModule);
		Map<String, StatModuleDetail> map = new HashMap<>();
		for (String moduleName : modules) {
			FusionContextConfig config = configMap.get(moduleName);
			if(config != null && config.isStatistic()) {
				StatModuleDetail detail = new StatModuleDetail();
				detail.setViews(queryAll(moduleName));
				map.put(moduleName, detail);
			}
		}
		return map;
		
	}
	
	
	@Override
	public StatListTemplateEntityView stat(StatCriteria criteria) {
		TemplateStatList statListTemplate = criteria.getStatListTemplate();
		String moduleName = statListTemplate.getModule();
		FusionContextConfig config =  fFactory.getModuleConfig(moduleName);
		BizFusionContext context = config.getCurrentContext(criteria.getUser());
		StatUpDrill drill = PanelFactory.getStatUpDrill(context);
		StatUpDrillContext drillContext = new StatUpDrillContext();
		
		List<TemplateStatColumn> columns = statListTemplate.getColumns();
		
		Set<Long> fieldIds = CollectionUtils.toSet(columns, col->col.getFieldId());
		Map<Long, DictionaryField> fieldMap = dictService.getFieldMap(moduleName, fieldIds);
		LinkedHashSet<String> dimensions = new LinkedHashSet<String>();
		
		for (TemplateStatColumn column : columns) {
			if(!criteria.getDisabledColumnIds().contains(column.getId()) && column.getFieldId() != null) {
				DictionaryField field = fieldMap.get(column.getFieldId());
				if(field != null) {
					if(PanelFactory.getStatGenerator().isDimension(field.getAbcAttrCode())) {
						dimensions.add(field.getAbcAttrCode());
					}
				}
			}
		}
		
		drillContext.setDimensions(dimensions);
		
		//设置统计查询条件
		//获得请求中的条件
		Map<Long, String> reqCriteriaMap = criteria.getReqCriteriaMap();
		
		//将其转换成通用条件对象
		List<TemplateStatCriteria> criterias = statListTemplate.getCriterias();
		Map<Long, TemplateStatCriteria> criteriaMap = CollectionUtils.toMap(criterias, TemplateStatCriteria::getId);
		Map<Long, NormalCriteria> nCriteriaMap = lcriteriaFactory.getCriteriasFromRequest(new MutablePropertyValues(reqCriteriaMap), criteriaMap);
		
		if(criteriaMap != null) {
			criteriaMap.forEach((criteriaId, tCriteria)->{
				if(!reqCriteriaMap.containsKey(criteriaId)) {
					reqCriteriaMap.put(criteriaId, tCriteria.getDefaultValue());
				}
			});
		}
		
		//根据条件的类型，归放到before和after中
		Set<NormalCriteria> beforeCriterias = new LinkedHashSet<>();
		Set<NormalCriteria> afterCriterias = new LinkedHashSet<>();
		
		nCriteriaMap.forEach((criteriaId, nCriteria)->{
			switch (nCriteria.getFilterOccasion()) {
				case TemplateStatCriteria.FILTER_OCCASION_BEFORE:
					beforeCriterias.add(nCriteria);
					break;
				case TemplateStatCriteria.FILTER_OCCASION_AFTER:
					afterCriterias.add(nCriteria);
					break;
			}
		});
		
		
		EntitySortedPagedQueryFactory beforeEntitySortedPagedQueryFactory = new EntitySortedPagedQueryFactory(context);
		EntityCriteriaFactory beforeEntityCriteriaFactory = beforeEntitySortedPagedQueryFactory.getHostCriteriaFactory();
		lcriteriaFactory.appendCriterias(beforeCriterias, moduleName, beforeEntityCriteriaFactory);
		
		//EntityCriteriaFactory beforeEntityCriteriaFactory = lcriteriaFactory.appendCriterias(beforeCriterias, moduleName, context);
		drillContext.setBeforeCriteria(beforeEntityCriteriaFactory.getCriterias());
		
		EntitySortedPagedQueryFactory afterEntitySortedPagedQueryFactory = new EntitySortedPagedQueryFactory(context);
		EntityCriteriaFactory afterEntityCriteriaFactory = afterEntitySortedPagedQueryFactory.getHostCriteriaFactory();
		lcriteriaFactory.appendCriterias(afterCriterias, moduleName, afterEntityCriteriaFactory);
		//EntityCriteriaFactory afterEntityCriteriaFactory = lcriteriaFactory.appendCriterias(afterCriterias, moduleName, context);
		drillContext.setAfterCriteria(afterEntityCriteriaFactory.getCriterias());
		
		
		//执行查询
		EntitySortedPagedQuery query = drill.drillUp(drillContext);
		
		
		PageInfo pageInfo = criteria.getPageInfo();
		pageInfo.setCount(query.getAllCount());
		List<Entity> entities = query.visitEntity(pageInfo.getPageNo());
		StatListTemplateEntityView view = new StatListTemplateEntityView(statListTemplate, fieldMap);
		view.getDisabledColumns().addAll(criteria.getDisabledColumnIds());
		ModuleMeta module = mService.getModule(moduleName);
		List<? extends CEntityPropertyParser> parsers = CollectionUtils.toList(entities, entity->entityService.toEntityParser(entity, new EntityParserParameter(moduleName, criteria.getUser())));
		
		Set<Long> criteriaFieldIds = CollectionUtils.toSet(criterias, TemplateStatCriteria::getFieldId);
		view.setCriteriaOptionMap(dictService.getOptionsMap(criteriaFieldIds));
		/*Set<String> criteriaFieldNames = view.getCriteria()
				.getCriteriaEntries().stream().map(entry->{
					DictionaryField field = dictService.getField(moduleName, entry.getFieldId());
					return field != null? field.getFullKey().replaceAll("\\[\\d+\\]", ""): null;
				}).collect(Collectors.toSet());
		view.setCriteriaLabelMap(dictService.getModuleLabelMap(moduleName, criteriaFieldNames));*/
		view.setModule(module);
		view.setEntities(entities);
		view.setParsers(parsers);
		return view;
	}
	
	@Override
	public void recalc(String moduleName, UserIdentifier user) {
		BizFusionContext context = fFactory.getModuleConfig(moduleName).getCurrentContext(user);
		PanelFactory.getStatGenerator().write(context);
	}

}
