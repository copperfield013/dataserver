package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.Assert;

import com.abc.application.BizFusionContext;
import com.abc.application.FusionContext;
import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public abstract class ComparatorCriteriaConverter implements CriteriaConverter{

	/**
	 * 不能返回null
	 * @return
	 */
	public abstract String getComparator();
	public boolean shouldHasValue() {
		return true;
	}
	protected abstract Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value);
	
	@Override
	public boolean support(NormalCriteria nCriteria) {
		String comparator = getComparator();
		Assert.notNull(comparator, "ComparatorCriteriaConverter的实现类的getComparator()方法不能返回null");
		return (!shouldHasValue() || TextUtils.hasText(nCriteria.getValue())) && comparator.equalsIgnoreCase(nCriteria.getComparator());
	}
	

	@Override
	public void invokeAddCriteria(BizFusionContext fusionContext, NormalCriteria nCriteria, List<Criteria> cs) {
		Assert.notNull(fusionContext, "fusionContext不能为null");
		CriteriaFactory criteriaFactory = new CriteriaFactory(fusionContext);
		DictionaryComposite composite = nCriteria.getComposite();
		Criteria criteria = null;
		if(composite != null && composite.getRelationSubdomain() != null) {
			String compositeName = composite.getName();
			String suffix = nCriteria.getFieldName().substring(compositeName.length() + 1);
			String mappingName = fusionContext.getABCNode().getRelation(compositeName).getFullTitle();
			BizFusionContext relationFusionContext = new BizFusionContext();
			relationFusionContext.setMappingName(mappingName);
			relationFusionContext.setSource(FusionContext.SOURCE_COMMON);
			relationFusionContext.setToEntityRange(FusionContext.ENTITY_CONTENT_RANGE_INTERSECTION);
			Collection<Criteria> relCriterias = getRelationCriterias(new CriteriaFactory(relationFusionContext), suffix, nCriteria.getValue());
			if(relCriterias != null) {
				criteria = criteriaFactory.createRelationCriteria(compositeName, nCriteria.getRelationLabel(), relCriterias);
			}
		}else {
			criteria = getNormalCriteria(criteriaFactory, nCriteria.getFieldName(), nCriteria.getValue());
		}
		if(criteria != null) {
			cs.add(criteria);
		}
	}
	protected Collection<Criteria> getRelationCriterias(CriteriaFactory relationCriteriaFactory, String fieldNameInRelation,
			String value){
		Criteria criteria = getRelationCriteria(relationCriteriaFactory, fieldNameInRelation, value);
		if(criteria != null) {
			return wrap(criteria);
		}
		return null;
	}
	

	protected Criteria getRelationCriteria(CriteriaFactory relationCriteriaFactory, String fieldNameInRelation,
			String value) {
		return null;
	}
	protected Collection<Criteria> wrap(Criteria... criterias) {
		ArrayList<Criteria> cs = new ArrayList<>();
		for (Criteria c : criterias) {
			if(c != null) {
				cs.add(c);
			}
		}
		return cs;
	}

	
	

}
