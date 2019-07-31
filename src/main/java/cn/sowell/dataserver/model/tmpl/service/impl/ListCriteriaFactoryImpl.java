package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;

import cho.carbon.panel.EntitySortedPagedQueryFactory;
import cho.carbon.query.entity.factory.EntityConJunctionFactory;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.abc.service.AbstractEntityQueryParameter.ArrayItemCriteria;
import cn.sowell.dataserver.model.abc.service.EntityQueryParameter;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.modules.bean.criteriaConveter.CriteriaConverter;
import cn.sowell.dataserver.model.modules.bean.criteriaConveter.CriteriaConverterFactory;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.SuperTemplateListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupPremise;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatCriteria;
import cn.sowell.dataserver.model.tmpl.service.ListCriteriaFactory;

@Service
public class ListCriteriaFactoryImpl implements ListCriteriaFactory{
	
	@Resource
	CriteriaConverterFactory criteriaConverterFactory;
	
	@Resource
	DictionaryService dictService;
	
	@Override
	public Map<Long, String> exractTemplateCriteriaMap(HttpServletRequest request) {
		ServletRequestParameterPropertyValues pvs = new ServletRequestParameterPropertyValues(request, "criteria", "_");
		Map<Long, String> criteriaMap = new HashMap<Long, String>();
		pvs.getPropertyValueList().forEach(pv->{
			 Long criteriaId = FormatUtils.toLong(pv.getName());
			 if(criteriaId != null){
				 criteriaMap.put(criteriaId, FormatUtils.toString(pv.getValue()));
			 }
		 });
		return criteriaMap;
	}
	
	
	@Override
	public Map<Long, NormalCriteria> getCriteriasFromRequest(
			MutablePropertyValues pvs, 
			Map<Long, ? extends AbstractListCriteria> defaultCriteriaMap) {
		 Map<Long, NormalCriteria> map = new HashMap<Long, NormalCriteria>();
		 pvs.getPropertyValueList().forEach(pv->{
			 Long criteriaId = FormatUtils.toLong(pv.getName());
			 if(criteriaId != null){
				 AbstractListCriteria criteria = defaultCriteriaMap.get(criteriaId);
				 if(criteria != null){
					 NormalCriteria ncriteria = new NormalCriteria();
					 bindNormalCriteria(criteria, ncriteria);
					 ncriteria.setValue(FormatUtils.toString(pv.getValue()));
					 map.put(criteriaId, ncriteria);
				 }
			 }
		 });
		 defaultCriteriaMap.forEach((criteriaId, criteria)->{
			 if(TextUtils.hasText(criteria.getDefaultValue()) && !map.containsKey(criteriaId)){
				 NormalCriteria nCriteria = new NormalCriteria();
				 bindNormalCriteria(criteria, nCriteria);
				 nCriteria.setCriteriaId(criteriaId);
				 nCriteria.setValue(criteria.getDefaultValue());
				 map.put(criteriaId, nCriteria);
			 }
		 });;
		return map;
	}
	
	
	
	private void bindNormalCriteria(AbstractListCriteria criteria, NormalCriteria ncriteria) {
		//TODO: 需要将fieldKey转换成attributeName
		 ncriteria.setFieldId(criteria.getFieldId());
		 if(criteria instanceof TemplateListCriteria) {
			 ncriteria.setCompositeId(((TemplateListCriteria)criteria).getCompositeId());
		 }
		 if(criteria instanceof TemplateStatCriteria) {
			 ncriteria.setFilterOccasion(((TemplateStatCriteria) criteria).getFilterOccasion());
		 }
		 ncriteria.setFieldName(criteria.getFieldKey());
		 ncriteria.setComparator(criteria.getComparator());
		 ncriteria.setRelationLabel(criteria.getRelationLabel());
	}


	@Override
	public void appendCriterias(List<NormalCriteria> nCriterias, String moduleName, EntityConJunctionFactory conjunctionFactory){
		nCriterias.forEach(nCriteria->{
			CriteriaConverter converter = criteriaConverterFactory.getConverter(nCriteria);
			if(converter != null) {
				if(nCriteria.getFieldName() != null) {
					String attributeName = nCriteria.getFieldName();
					if(attributeName.contains(".")) {
						nCriteria.setComposite(dictService.getCurrencyCacheCompositeByFieldId(moduleName, nCriteria.getFieldId()));
					}
				}else if(nCriteria.getCompositeId() != null) {
					nCriteria.setComposite(dictService.getComposite(moduleName, nCriteria.getCompositeId()));
				}
				converter.invokeAddCriteria(conjunctionFactory, nCriteria);
			}
		});
	}
	@Override
	public void appendCompositeCriterias(List<NormalCriteria> nCriterias, String moduleName, EntityConJunctionFactory conjunctionFactory) {
		nCriterias.forEach(nCriteria->{
			CriteriaConverter converter = criteriaConverterFactory.getConverter(nCriteria);
			if(converter != null) {
				if(nCriteria.getFieldName() != null) {
					String attributeName = nCriteria.getFieldName();
					if(attributeName.contains(".")) {
						nCriteria.setComposite(dictService.getCurrencyCacheCompositeByFieldId(moduleName, nCriteria.getFieldId()));
					}
				}
				converter.invokeAddCriteria(conjunctionFactory, nCriteria);
			}
		});
	}
	
	
	@Override
	public void appendPremiseCriteria(String moduleName, List<TemplateGroupPremise> premises, List<NormalCriteria> nCriterias) {
		//添加模板组合的默认字段条件
		if(premises != null) {
			premises.forEach(premise->{
				DictionaryField field = dictService.getField(moduleName, premise.getFieldId());
				if(field != null) {
					NormalCriteria nCriteria = new NormalCriteria();
					nCriteria.setFieldId(field.getId());
					nCriteria.setFieldName(premise.getFieldName());
					nCriteria.setComparator("equals");
					nCriteria.setValue(premise.getFieldValue());
					nCriterias.add(nCriteria);
				}
			});
		}
	}
	@Override
	public <CRI extends AbstractListCriteria> void coverAbsCriteriaForUpdate(CRI originCriteria, CRI criteria) {
		originCriteria.setTitle(criteria.getTitle());
		originCriteria.setOrder(criteria.getOrder());
		if(criteria.getFieldAvailable()) {
			originCriteria.setFieldId(criteria.getFieldId());
			originCriteria.setFieldKey(criteria.getFieldKey());
			originCriteria.setRelation(criteria.getRelation());
			originCriteria.setQueryShow(criteria.getQueryShow());
			originCriteria.setComparator(criteria.getComparator());
			originCriteria.setInputType(criteria.getInputType());
			originCriteria.setRelationLabel(criteria.getRelationLabel());
			originCriteria.setViewOption(criteria.getViewOption());
			originCriteria.setDefaultValue(criteria.getDefaultValue());
			originCriteria.setPlaceholder(criteria.getPlaceholder());
		}
	}
	@Override
	public <CRI extends SuperTemplateListCriteria> void coverSupCriteriaForUpdate(CRI originCriteria, CRI criteria) {
		coverAbsCriteriaForUpdate(originCriteria, criteria);
		originCriteria.setCompositeId(criteria.getCompositeId());
	}
	
	
	@Override
	public void appendArrayItemCriteriaParameter(EntitySortedPagedQueryFactory sortedPagedQueryFactory,
			EntityQueryParameter queryParam) {
		List<ArrayItemCriteria> aCriterias = queryParam.getArrayItemCriterias();
		for (ArrayItemCriteria aCriteria : aCriterias) {
			List<NormalCriteria> nCriterias = aCriteria.getCriterias();
			appendCompositeCriterias(nCriterias, aCriteria.getModuleName(), sortedPagedQueryFactory.getHostParamFactory().getEntityConJunctionFactory());
			/*
			 * if(aCriteria.isRelation()) { QueryEntityParamFactory relationCriteriaFactory
			 * =
			 * sortedPagedQueryFactory.getSubEntityCriteriaFactory(aCriteria.getComposite().
			 * getName()); appendCompositeCriterias(nCriterias, aCriteria.getModuleName(),
			 * relationCriteriaFactory); }else { QueryEnGroup2DParamFactory
			 * arrayItemCriteriaFactory =
			 * sortedPagedQueryFactory.getGroup2DParamFactory(aCriteria.getComposite().
			 * getName()); appendCriterias(nCriterias, aCriteria.getModuleName(),
			 * arrayItemCriteriaFactory); }
			 */
		}
	}

	@Override
	public Consumer<EntityConJunctionFactory> getNormalCriteriaFactoryConsumer(String moduleName, List<NormalCriteria> nCriterias) {
		return factory->{
			appendCriterias(nCriterias, moduleName, factory);
		};
	}
}
