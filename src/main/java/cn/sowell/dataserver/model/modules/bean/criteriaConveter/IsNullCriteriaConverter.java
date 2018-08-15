package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

public class IsNullCriteriaConverter extends ComparatorCriteriaConverter {

	@Override
	public String getComparator() {
		return "none1n";
	}
	
	@Override
	public boolean shouldHasValue() {
		return false;
	}

	@Override
	protected Criteria getRelationCriteria(CriteriaFactory cFactory, String compositeName, String relationLabel,
			String suffix, String value) {
		return cFactory.createIsNullQueryCriteria(
				compositeName, 
				relationLabel,
				suffix);
	}

	@Override
	protected Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value) {
		return cFactory.createIsNullQueryCriteria(fieldName);
	}

}
