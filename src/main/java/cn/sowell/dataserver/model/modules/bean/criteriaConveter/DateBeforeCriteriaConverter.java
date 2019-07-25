package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import cho.carbon.meta.enun.operator.BetweenOperator;
import cho.carbon.query.entity.factory.EnGroupJunctionFactory;

public class DateBeforeCriteriaConverter extends MultiSupportComparatorCriteriaConverter {

	public DateBeforeCriteriaConverter() {
		super(new String[]{"d2", "n2"});
	}
	

	@Override
	protected void addNormalCriteria(EnGroupJunctionFactory cFactory, String fieldName, String value) {
		cFactory.addBetween(fieldName, null, value, BetweenOperator.BETWEEN);
	}
	

}
