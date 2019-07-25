package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import cho.carbon.meta.enun.operator.UnaryOperator;
import cho.carbon.query.entity.factory.EnGroupJunctionFactory;

public class TextLeftLikeCriteriaConverter extends ComparatorCriteriaConverter {

	@Override
	public String getComparator() {
		return "t2";
	}

	
	@Override
	protected void addNormalCriteria(EnGroupJunctionFactory cFactory, String fieldName, String value) {
		cFactory.addCommon(fieldName, value, UnaryOperator.LIKE_LEFT);
	}
	
}
