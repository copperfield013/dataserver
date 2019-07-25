package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import cho.carbon.meta.enun.operator.NullOperator;
import cho.carbon.query.entity.factory.EnGroupJunctionFactory;

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
	protected void addNormalCriteria(EnGroupJunctionFactory cFactory, String fieldName, String value) {
		cFactory.addNull(fieldName, NullOperator.ISNULL);
	}
	
	

}
