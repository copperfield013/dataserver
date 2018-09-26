package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

public class TextLikeCriteriaConverter extends ComparatorCriteriaConverter {

	@Override
	public String getComparator() {
		return "t1";
	}

	
	@Override
	protected Criteria getRelationCriteria(CriteriaFactory relationCriteriaFactory, String fieldNameInRelation,
			String value) {
		return relationCriteriaFactory.createLikeQueryCriteria(fieldNameInRelation, value);
	}

	@Override
	protected Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value) {
		return cFactory.createLikeQueryCriteria(fieldName, value);
	}

}
