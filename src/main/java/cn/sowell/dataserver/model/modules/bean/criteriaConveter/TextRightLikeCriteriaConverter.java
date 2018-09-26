package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

public class TextRightLikeCriteriaConverter extends ComparatorCriteriaConverter {

	@Override
	public String getComparator() {
		return "t3";
	}
	
	@Override
	protected Criteria getRelationCriteria(CriteriaFactory relationCriteriaFactory, String fieldNameInRelation,
			String value) {
		return relationCriteriaFactory.createRightLikeQueryCriteria(fieldNameInRelation, value);
	}

	@Override
	protected Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value) {
		return cFactory.createRightLikeQueryCriteria(fieldName, value);
	}

}
