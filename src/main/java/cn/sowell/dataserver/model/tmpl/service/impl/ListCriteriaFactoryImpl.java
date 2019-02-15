package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;

import com.abc.application.BizFusionContext;
import com.abc.rrc.query.criteria.EntityCriteriaFactory;

import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.modules.bean.criteriaConveter.CriteriaConverter;
import cn.sowell.dataserver.model.modules.bean.criteriaConveter.CriteriaConverterFactory;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListCriteria;
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
					 ncriteria.setValue(FormatUtils.toString(pv.getValue()));
					 ncriteria.setRelationLabel(criteria.getRelationLabel());
					 map.put(criteriaId, ncriteria);
				 }
			 }
		 });
		 defaultCriteriaMap.forEach((criteriaId, criteria)->{
			 if(TextUtils.hasText(criteria.getDefaultValue()) && !map.containsKey(criteriaId)){
				 NormalCriteria nCriteria = new NormalCriteria();
				 //TODO: 需要将fieldKey转换成attributeName
				 nCriteria.setFieldId(criteria.getFieldId());
				 nCriteria.setFieldName(criteria.getFieldKey());
				 nCriteria.setComparator(criteria.getComparator());
				 nCriteria.setValue(criteria.getDefaultValue());
				 nCriteria.setRelationLabel(criteria.getRelationLabel());
				 map.put(criteriaId, nCriteria);
			 }
		 });;
		return map;
	}
	
	
	
	@Override
	public EntityCriteriaFactory appendCriterias(Collection<NormalCriteria> nCriterias, String moduleName, BizFusionContext context){
		EntityCriteriaFactory criteriaFactory = new EntityCriteriaFactory(context);
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
				converter.invokeAddCriteria(context, criteriaFactory, nCriteria);
			}
		});
		return criteriaFactory;
	}
}
