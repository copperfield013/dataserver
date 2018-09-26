package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

public class IsNotNullCriteriaConverter extends ComparatorCriteriaConverter {

	@Override
	public String getComparator() {
		return "none1";
	}
	@Override
	public boolean shouldHasValue() {
		return false;
	}

	@Override
	protected Criteria getRelationCriteria(CriteriaFactory relationCriteriaFactory, String fieldNameInRelation,
			String value) {
		return relationCriteriaFactory.createIsNotNullQueryCriteria(fieldNameInRelation);
	}

	@Override
	protected Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value) {
		return cFactory.createIsNotNullQueryCriteria(fieldName);
	}

}
