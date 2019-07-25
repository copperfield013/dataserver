package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import cho.carbon.meta.enun.operator.BetweenOperator;
import cho.carbon.query.entity.factory.EnGroupJunctionFactory;

public class DateAfterCriteriaConverter extends MultiSupportComparatorCriteriaConverter {

	public DateAfterCriteriaConverter() {
		super(new String[] {"d3", "n3"});
	}
	

	@Override
	protected void addNormalCriteria(EnGroupJunctionFactory cFactory, String fieldName, String value) {
		cFactory.addBetween(fieldName, value, null, BetweenOperator.BETWEEN);
	}
	
}
