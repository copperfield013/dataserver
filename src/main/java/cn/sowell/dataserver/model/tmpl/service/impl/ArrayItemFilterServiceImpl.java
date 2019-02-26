package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abc.application.BizFusionContext;
import com.abc.rrc.query.criteria.EntityCriteriaFactory;
import com.abc.rrc.query.queryrecord.criteria.Criteria;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.UserCodeService;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;
import cn.sowell.dataserver.model.tmpl.manager.ArrayItemFilterManager;
import cn.sowell.dataserver.model.tmpl.manager.DetailTemplateManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailArrayItemCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailArrayItemFilter;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.service.ArrayItemFilterService;
import cn.sowell.dataserver.model.tmpl.service.ListCriteriaFactory;

@Service
public class ArrayItemFilterServiceImpl 
	extends AbstractTemplateService<TemplateDetailArrayItemFilter, ArrayItemFilterManager>
	implements ArrayItemFilterService{

	@Resource
	DetailTemplateManager dtmplManager;
	
	@Resource
	ListCriteriaFactory lCriteriaFactory;
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	@Resource
	UserCodeService userCodeService;
	
	@Autowired
	public ArrayItemFilterServiceImpl(@Autowired ArrayItemFilterManager manager) {
		super(manager);
	}
	
	@Override
	public Map<String, Collection<Criteria>> getArrayItemFilterCriteriasMap(Long dtmplId, UserIdentifier user) {
		TemplateDetailTemplate dtmpl =  dtmplManager.get(dtmplId);
		Map<String, Collection<Criteria>> criteriasMap = new LinkedHashMap<>();
		if(dtmpl != null) {
			String moduleName = dtmpl.getModule();
			List<TemplateDetailFieldGroup> groups = dtmpl.getGroups();
			if(groups != null) {
				
				List<TemplateDetailArrayItemFilter> filters = getManager().queryByModule(moduleName);
				
				//遍历所有字段组
				for (TemplateDetailFieldGroup group : groups) {
					DictionaryComposite composite = group.getComposite();
					if(group.getArrayItemFilterId() != null && group.getComposite() != null && 1 == group.getIsArray()) {
						boolean isRelation = DictionaryComposite.RELATION_ADD_TYPE.equals(composite.getAddType());
						
						List<NormalCriteria> nCriterias = new ArrayList<>();
						filters.stream()
							.filter(filter->filter != null && filter.getCompositeId().longValue() == composite.getId())
							.forEach(filter->{
								List<TemplateDetailArrayItemCriteria> criterias = filter.getCriterias();
								if(criterias != null) {
									for (TemplateDetailArrayItemCriteria criteria : criterias) {
										NormalCriteria nCriteria = new NormalCriteria();
										nCriteria.setFieldId(criteria.getFieldId());
										nCriteria.setValue(criteria.getDefaultValue());
										nCriteria.setComparator(criteria.getComparator());
										if(isRelation && criteria.getFieldKey().startsWith(composite.getName())) {
											nCriteria.setFieldName(criteria.getFieldKey().substring(composite.getName().length() + 1));
										}else {
											nCriteria.setFieldName(criteria.getFieldKey());
										}
										nCriterias.add(nCriteria);
									}
								}
						});
						
						if(!nCriterias.isEmpty()) {
							if(composite != null) {
								if(isRelation) {
									//字段组是一个关系
									//创建关系条件的context
									BizFusionContext relationContext = fFactory.getModuleConfig(moduleName).createRelationContext(composite.getName(), user);
									
									EntityCriteriaFactory criteriaFactory = lCriteriaFactory.appendCriterias(nCriterias, dtmpl.getModule(), relationContext);
									criteriasMap.put(relationContext.getMappingName(), criteriaFactory.getCriterias());
								}else {
									//字段组是一个多值属性
									BizFusionContext fusionContext = fFactory.getModuleConfig(dtmpl.getModule()).getCurrentContext(user);
									EntityCriteriaFactory criteriaFactory = lCriteriaFactory.appendCriterias(nCriterias, dtmpl.getModule(), fusionContext);
									criteriasMap.put(fusionContext.getMappingName() + "." + composite.getName(), criteriaFactory.getCriterias());
								}
							}
						}
					}
					
				}
				
			}
		}
		
		return criteriasMap;
	}
	
}
