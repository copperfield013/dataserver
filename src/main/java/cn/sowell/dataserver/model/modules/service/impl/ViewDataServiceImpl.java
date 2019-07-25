package cn.sowell.dataserver.model.modules.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cho.carbon.entity.entity.RecordEntity;
import cho.carbon.query.entity.SortedPagedQuery;
import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.CEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.dataserver.model.abc.service.EntitiesQueryParameter;
import cn.sowell.dataserver.model.abc.service.EntityParserParameter;
import cn.sowell.dataserver.model.abc.service.ModuleEntityService;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.modules.service.ViewDataService;
import cn.sowell.dataserver.model.modules.service.view.EntityView;
import cn.sowell.dataserver.model.modules.service.view.EntityViewCriteria;
import cn.sowell.dataserver.model.modules.service.view.EntityViewCriteria.CriteriaEntry;
import cn.sowell.dataserver.model.modules.service.view.ListTemplateEntityView;
import cn.sowell.dataserver.model.modules.service.view.ListTemplateEntityViewCriteria;
import cn.sowell.dataserver.model.modules.service.view.SelectionTemplateEntityView;
import cn.sowell.dataserver.model.modules.service.view.SelectionTemplateEntityViewCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;
import cn.sowell.dataserver.model.tmpl.service.ListCriteriaFactory;
import cn.sowell.dataserver.model.tmpl.service.ListTemplateService;
import cn.sowell.dataserver.model.tmpl.service.TemplateGroupService;

@Service
public class ViewDataServiceImpl implements ViewDataService{

	@Resource
	ModulesService mService;
	
	@Resource
	TemplateGroupService tmplGroupService;
	
	@Resource
	ListTemplateService ltmplService;
	
	@Resource
	DictionaryService dService;
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	@Resource
	ListCriteriaFactory lcriteriaFactory;
	
	@Resource
	ModuleEntityService entityService;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public EntityView query(EntityViewCriteria criteria) {
		String moduleName = criteria.getModule();
		ModuleMeta module = mService.getModule(moduleName);
		EntityView view = null;
		List<RecordEntity> entities = null;
		List<? extends CEntityPropertyParser> parsers = null;
		if(criteria instanceof ListTemplateEntityViewCriteria) {
			ListTemplateEntityViewCriteria lCriteria = (ListTemplateEntityViewCriteria) criteria;
			TemplateGroup tmplGroup = null;
			if(lCriteria.getListTemplateId() == null) {
				if(lCriteria.getTemplateGroupId() != null) {
					tmplGroup = tmplGroupService.getTemplate(lCriteria.getTemplateGroupId());
					Assert.notNull("根据[id=" + lCriteria.getTemplateGroupId() + "]无法找到对应的模板组合");
				}
				//添加模板组合的默认字段条件
				if(tmplGroup.getPremises() != null) {
					tmplGroup.getPremises().forEach(premise->{
						DictionaryField field = dService.getField(moduleName, premise.getFieldId());
						if(field != null) {
							CriteriaEntry entry = new CriteriaEntry();
							entry.setFieldId(premise.getFieldId());
							entry.setComparator("equals");
							entry.setValue(premise.getFieldValue());
							lCriteria.getCriteriaEntries().add(entry);
						}
					});
				}
				lCriteria.setListTemplateId(tmplGroup.getListTemplateId());
			}
			Assert.notNull(lCriteria.getListTemplateId(), "ListTemplateEntiryViewCriteria的listTemplateId不能为空");
			TemplateListTemplate ltmpl = ltmplService.getTemplate(lCriteria.getListTemplateId());
			Map<Integer, DictionaryField> fieldMap = dService.getFieldMap(moduleName, CollectionUtils.toSet(ltmpl.getColumns(), col->col.getFieldId()));
			ListTemplateEntityView lview = new ListTemplateEntityView(ltmpl, fieldMap);
			view = lview;
			entities = queryEntities(lCriteria, ltmpl);
			EntityParserParameter param = new EntityParserParameter(moduleName, criteria.getUser());
			parsers = CollectionUtils.toList(entities, entity->entityService.toEntityParser(entity, param));
			//parsers = CollectionUtils.toList(entities, entity->entityService.getModuleEntityParser(moduleName, entity, criteria.getUser()));
		}else if(criteria instanceof SelectionTemplateEntityViewCriteria){
			SelectionTemplateEntityViewCriteria sCriteria = (SelectionTemplateEntityViewCriteria) criteria;
			Map<Integer, DictionaryField> fieldMap = dService.getFieldMap(moduleName, CollectionUtils.toSet(sCriteria.getSelectionTemplate().getColumns(), col->col.getFieldId()));
			SelectionTemplateEntityView sview = new SelectionTemplateEntityView(sCriteria.getSelectionTemplate(), fieldMap);
			view = sview;
			entities = queryEntities(sCriteria, sCriteria.getSelectionTemplate());
			EntityParserParameter param = new EntityParserParameter(moduleName, criteria.getRelationName(), criteria.getUser(), null);
			parsers = CollectionUtils.toList(entities, entity->entityService.toRelationParser(entity, param));
			//parsers = CollectionUtils.toList(entities, entity->abcService.getRelationEntityParser(moduleName, criteria.getRelationName(), entity, criteria.getUser()));
		}else {
			view = new EntityView();
			entities = queryEntities(criteria, criteria.getUser());
		}
		
		view.setCriteria(criteria);
		Set<Integer> criteriaFieldIds = view.getCriteria().getCriteriaEntries().stream().filter(entry->entry.getFieldId() != null).map(CriteriaEntry::getFieldId).collect(Collectors.toSet());
		Set<String> criteriaFieldNames = view.getCriteria()
					.getCriteriaEntries().stream().map(entry->{
						DictionaryField field = dService.getField(moduleName, entry.getFieldId());
						return field != null? field.getFullKey().replaceAll("\\[\\d+\\]", ""): null;
					}).collect(Collectors.toSet());
		view.setCriteriaOptionMap(dService.getOptionsMap(criteriaFieldIds));
		view.setCriteriaLabelMap(dService.getModuleLabelMap(moduleName, criteriaFieldNames));
		view.setParsers(parsers);
		view.setEntities(entities);
		view.setCriteria(criteria);
		view.setModule(module);
		return view;
		
		
	}

	private List<RecordEntity> queryEntities(SelectionTemplateEntityViewCriteria sCriteria,
			TemplateSelectionTemplate stmpl) {
		Map<Long, String> stmplCrteriaMap = sCriteria.getTemplateCriteriaMap();
		Map<Long, TemplateSelectionCriteria> tCriteriaMap = CollectionUtils.toMap(stmpl.getCriterias(), TemplateSelectionCriteria::getId);
		
		if(tCriteriaMap != null) {
			tCriteriaMap.forEach((criteriaId, tCriteria)->{
				if(!stmplCrteriaMap.containsKey(criteriaId)) {
					stmplCrteriaMap.put(criteriaId, tCriteria.getDefaultValue());
				}
			});
		}
		if(stmplCrteriaMap != null ) {
			stmplCrteriaMap.forEach((criteriaId, value)->{
				TemplateSelectionCriteria tCriteria = tCriteriaMap.get(criteriaId);
				CriteriaEntry cEntry = new CriteriaEntry();
				cEntry.setFieldId(tCriteria.getFieldId());
				cEntry.setComparator(tCriteria.getComparator());
				cEntry.setValue(value);
				cEntry.setRelationLabel(tCriteria.getRelationLabel());
				sCriteria.getCriteriaEntries().add(cEntry);
			});
		}
		
		return queryEntities(sCriteria, sCriteria.getUser());
	}

	private List<RecordEntity> queryEntities(ListTemplateEntityViewCriteria criteria, TemplateListTemplate ltmpl) {
		if(criteria.getTemplateCriteriaMap() == null) {
			criteria.setTemplateCriteriaMap(new LinkedHashMap<>());
		}
		Map<Long, String> ltmplCrteriaMap = criteria.getTemplateCriteriaMap();
		Map<Long, TemplateListCriteria> tCriteriaMap = CollectionUtils.toMap(ltmpl.getCriterias(), c->c.getId());
		
		if(tCriteriaMap != null) {
			tCriteriaMap.forEach((criteriaId, tCriteria)->{
				if(!ltmplCrteriaMap.containsKey(criteriaId)) {
					ltmplCrteriaMap.put(criteriaId, tCriteria.getDefaultValue());
				}
			});
		}
		if(ltmplCrteriaMap != null ) {
			ltmplCrteriaMap.forEach((criteriaId, value)->{
				TemplateListCriteria tCriteria = tCriteriaMap.get(criteriaId);
				CriteriaEntry cEntry = new CriteriaEntry();
				cEntry.setFieldId(tCriteria.getFieldId());
				cEntry.setCompositeId(tCriteria.getCompositeId());
				cEntry.setComparator(tCriteria.getComparator());
				cEntry.setValue(value);
				cEntry.setRelationLabel(tCriteria.getRelationLabel());
				criteria.getCriteriaEntries().add(cEntry);
			});
		}
		
		return queryEntities(criteria, criteria.getUser());
	}

	private List<RecordEntity> queryEntities(EntityViewCriteria criteria, UserIdentifier user) {
		EntitiesQueryParameter param = new EntitiesQueryParameter(criteria.getModule(), user);
		param.setRelationName(criteria.getRelationName());
		param.setPageInfo(criteria.getPageInfo());
		param.setUser(user);
		List<NormalCriteria> nCriterias = new ArrayList<>();
		boolean hasRelation = TextUtils.hasText(criteria.getRelationName());
		if(criteria.getCriteriaEntries() != null) {
			criteria.getCriteriaEntries().forEach(entry->{
				NormalCriteria nCriteria = new NormalCriteria();
				if(entry.getFieldId() != null) {
					DictionaryField field = dService.getField(criteria.getModule(), entry.getFieldId());
					if(field != null) {
						nCriteria.setCompositeId(entry.getCompositeId());
						nCriteria.setFieldId(entry.getFieldId());
						String fieldName = field.getFullKey();
						if(hasRelation && fieldName.startsWith(criteria.getRelationName())) {
							fieldName = fieldName.substring(criteria.getRelationName().length() + 1);
						}
						nCriteria.setFieldName(fieldName);
						nCriteria.setComparator(entry.getComparator());
						nCriteria.setRelationLabel(entry.getRelationLabel());
						nCriteria.setValue(entry.getValue());
						nCriterias.add(nCriteria);
					}
				}else if(entry.getCompositeId() != null) {
					nCriteria.setCompositeId(entry.getCompositeId());
					nCriteria.setComparator(entry.getComparator());
					nCriteria.setValue(entry.getValue());
					nCriterias.add(nCriteria);
				}
			});
		}
		if(criteria.getExistCodes() != null && !criteria.getExistCodes().isEmpty()) {
			NormalCriteria nCriteria = new NormalCriteria();
			nCriteria.setFieldName("唯一编码");
			nCriteria.setComparator("l1n");
			nCriteria.setValue(CollectionUtils.toChain(criteria.getExistCodes()));
			nCriterias.add(nCriteria);
		}
		
		param.setConjunctionFactoryConsumer(criteriaFactory->{
			lcriteriaFactory.appendCriterias(nCriterias, 
					criteria.getModule(), 
					criteriaFactory
					); 
		});
		
		SortedPagedQuery<RecordEntity> query = entityService.getQuickSortedEntitiesQuery(param);
		PageInfo pageInfo = param.getPageInfo();
		pageInfo.setCount(query.getAllCount());
		return query.visitEntity(pageInfo.getPageNo());
	}

}
