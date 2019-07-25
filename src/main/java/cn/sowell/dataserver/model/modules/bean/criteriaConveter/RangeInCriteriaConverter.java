package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import org.apache.commons.lang.StringUtils;

import cho.carbon.meta.enun.operator.BetweenOperator;
import cho.carbon.query.entity.factory.EnGroupJunctionFactory;

public class RangeInCriteriaConverter extends ComparatorCriteriaConverter {


	@Override
	public String getComparator() {
		return "rg1";
	}

	protected String[] getRange(String val) {
		String[] rangeStr = new String[2];
		if(val != null) {
			if(val.contains("~")) {
				rangeStr = val.split("~", 2);
			}else {
				rangeStr[0] = val;
			}
			rangeStr[0] = StringUtils.isEmpty(rangeStr[0])? null: rangeStr[0];
			rangeStr[1] = StringUtils.isEmpty(rangeStr[1])? null: rangeStr[1];
		}
		return rangeStr;
	}
	
	@Override
	protected void addNormalCriteria(EnGroupJunctionFactory cFactory, String fieldName, String value) {
		String[] rangeStr = getRange(value);
		if(rangeStr[0] != null || rangeStr[1] != null) {
			cFactory.addBetween(fieldName, rangeStr[0], rangeStr[1], BetweenOperator.BETWEEN);
		}	
	}
	

}
