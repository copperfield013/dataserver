package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

public class DateBeforeCriteriaConverter extends ComparatorCriteriaConverter {

	@Override
	public String getComparator() {
		return "d2";
	}

	@Override
	protected Criteria getRelationCriteria(CriteriaFactory cFactory, String compositeName, String relationLabel,
			String suffix, String value) {
		return cFactory.createOpenBetweenQueryCriteria(
				compositeName, 
				relationLabel,
				suffix,
				null,
				value);
	}

	@Override
	protected Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value) {
		return cFactory.createOpenBetweenQueryCriteria(fieldName, null, value);
	}

}
