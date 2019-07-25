package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import cho.carbon.meta.enun.operator.UnaryOperator;
import cho.carbon.query.entity.factory.EnGroupJunctionFactory;

public class TextLikeCriteriaConverter extends ComparatorCriteriaConverter {

	@Override
	public String getComparator() {
		return "t1";
	}

	@Override
	protected void addNormalCriteria(EnGroupJunctionFactory cFactory, String fieldName, String value) {
		cFactory.addCommon(fieldName, value, UnaryOperator.LIKE);
	}
	
}
