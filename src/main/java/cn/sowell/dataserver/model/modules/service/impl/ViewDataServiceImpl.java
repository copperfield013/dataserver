package cn.sowell.dataserver.model.modules.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.abc.mapping.entity.Entity;
import com.abc.query.criteria.Criteria;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.dataserver.model.abc.service.ABCExecuteService;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.modules.service.ViewDataService;
import cn.sowell.dataserver.model.modules.service.impl.EntityViewCriteria.CriteriaEntry;
import cn.sowell.dataserver.model.tmpl.bean.QueryEntityParameter;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;
import cn.sowell.dataserver.model.tmpl.service.TemplateService;

@Service
public class ViewDataServiceImpl implements ViewDataService{

	@Resource
	ABCExecuteService abcService;
	
	@Resource
	ModulesService mService;
	
	@Resource
	TemplateService tService;
	
	@Resource
	DictionaryService dService;
	
	@Override
	public EntityView query(EntityViewCriteria criteria) {
		String moduleName = criteria.getModule();
		ModuleMeta module = mService.getModule(moduleName);
		EntityView view = null;
		List<Entity> entities = null;
		if(criteria instanceof ListTemplateEntityViewCriteria) {
			ListTemplateEntityViewCriteria lCriteria = (ListTemplateEntityViewCriteria) criteria;
			TemplateGroup tmplGroup = null;
			if(lCriteria.getListTemplateId() == null) {
				if(lCriteria.getTemplateGroupId() != null) {
					tmplGroup = tService.getTemplateGroup(lCriteria.getTemplateGroupId());
					Assert.notNull("根据[id=" + lCriteria.getTemplateGroupId() + "]无法找到对应的模板组合");
				}else {
					tmplGroup = tService.getTemplateGroup(moduleName, lCriteria.getTemplateGroupKey());
					Assert.hasText("根据[key=" + lCriteria.getTemplateGroupKey() + "]无法找到对应的模板组合");
				}
				lCriteria.setListTemplateId(tmplGroup.getListTemplateId());
			}
			Assert.notNull(lCriteria.getListTemplateId(), "ListTemplateEntiryViewCriteria的listTemplateId不能为空");
			TemplateListTemplate ltmpl = tService.getListTemplate(lCriteria.getListTemplateId());
			Map<Long, DictionaryField> fieldMap = dService.getFieldMap(moduleName, CollectionUtils.toSet(ltmpl.getColumns(), col->col.getFieldId()));
			ListTemplateEntityView lview = new ListTemplateEntityView(ltmpl, fieldMap);
			view = lview;
			entities = queryEntities(lCriteria, ltmpl);
		}else {
			view = new EntityView();
			entities = queryEntities(criteria);
		}
		List<ModuleEntityPropertyParser> parsers = CollectionUtils.toList(entities, entity->abcService.getModuleEntityParser(moduleName, entity));
		
		Set<Long> criteriaFieldIds = view.getCriteria().getCriteriaEntries().stream().map(CriteriaEntry::getFieldId).collect(Collectors.toSet());
		Set<String> criteriaFieldNames = view.getCriteria().getCriteriaEntries().stream().map(CriteriaEntry::getFieldName).collect(Collectors.toSet());
		view.setCriteriaOptionMap(dService.getOptionsMap(criteriaFieldIds));
		view.setCriteriaLabelMap(dService.getModuleLabelMap(moduleName, criteriaFieldNames));
		view.setParsers(parsers);
		view.setEntities(entities);
		view.setCriteria(criteria);
		view.setModule(module);
		return view;
		
		
	}

	private List<Entity> queryEntities(ListTemplateEntityViewCriteria criteria, TemplateListTemplate ltmpl) {
		if(criteria.getListTemplateCriteria() == null) {
			criteria.setListTemplateCriteria(new HashMap<>());
		}
		Map<Long, String> ltmplCrteriaMap = criteria.getListTemplateCriteria();
		Map<Long, TemplateListCriteria> tCriteriaMap = CollectionUtils.toMap(ltmpl.getCriterias(), c->c.getId());
		
		if(tCriteriaMap != null) {
			tCriteriaMap.forEach((criteriaId, tCriteria)->{
				if(tCriteria.getDefaultValue() != null && !ltmplCrteriaMap.containsKey(criteriaId)) {
					ltmplCrteriaMap.put(criteriaId, tCriteria.getDefaultValue());
				}
			});
		}
		if(ltmplCrteriaMap != null ) {
			ltmplCrteriaMap.forEach((criteriaId, value)->{
				TemplateListCriteria tCriteria = tCriteriaMap.get(criteriaId);
				CriteriaEntry cEntry = new CriteriaEntry();
				cEntry.setFieldId(tCriteria.getFieldId());
				cEntry.setComparator(tCriteria.getComparator());
				cEntry.setValue(value);
				cEntry.setRelationLabel(tCriteria.getRelationLabel());
				criteria.getCriteriaEntries().add(cEntry);
			});
		}
		
		return queryEntities(criteria);
	}

	private List<Entity> queryEntities(EntityViewCriteria criteria) {
		QueryEntityParameter param = new QueryEntityParameter();
		param.setModule(criteria.getModule());
		param.setPageInfo(criteria.getPageInfo());
		Set<NormalCriteria> nCriterias = new HashSet<NormalCriteria>();
		if(criteria.getCriteriaEntries() != null) {
			criteria.getCriteriaEntries().forEach(entry->{
				NormalCriteria nCriteria = new NormalCriteria();
				nCriteria.setFieldId(entry.getFieldId());
				String fieldName = dService.getFieldName(criteria.getModule(), entry.getFieldId()).getFullKey();
				nCriteria.setFieldName(fieldName);
				entry.setFieldName(fieldName);
				nCriteria.setComparator(entry.getComparator());
				nCriteria.setRelationLabel(entry.getRelationLabel());
				nCriteria.setValue(entry.getValue());
				nCriterias.add(nCriteria);
			});
		}
		List<Criteria> criterias = mService.toCriterias(nCriterias, criteria.getModule());
		param.setCriterias(criterias);
		List<Entity> list = abcService.queryModuleEntities(param);
		return list;
	}

}
