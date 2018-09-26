package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

public class TextLeftLikeCriteriaConverter extends ComparatorCriteriaConverter {

	@Override
	public String getComparator() {
		return "t2";
	}

	
	@Override
	protected Criteria getRelationCriteria(CriteriaFactory relationCriteriaFactory, String fieldNameInRelation,
			String value) {
		return relationCriteriaFactory.createLeftLikeQueryCriteria(fieldNameInRelation, value);
	}

	@Override
	protected Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value) {
		return cFactory.createLeftLikeQueryCriteria(fieldName, value);
	}

}
