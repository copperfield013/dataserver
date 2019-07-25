package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.Date;

import cho.carbon.meta.enun.operator.BetweenOperator;
import cho.carbon.query.entity.factory.EnGroupJunctionFactory;
import cn.sowell.copframe.utils.date.CommonDateFormat;
import cn.sowell.copframe.utils.date.FrameDateFormat;

public class DateRangeInCriteriaConverter extends ComparatorCriteriaConverter {

	private FrameDateFormat dateFormat = new CommonDateFormat();


	@Override
	public String getComparator() {
		return "dr1";
	}

	protected String[] getDateRange(String val) {
		Date[] range = dateFormat.splitDateRange(val);
		String[] rangeStr = new String[2];
		rangeStr[0] = dateFormat.formatDate(range[0]);
		rangeStr[1] = dateFormat.formatDate(range[1]);
		return rangeStr;
	}
	
	
	@Override
	protected void addNormalCriteria(EnGroupJunctionFactory cFactory, String fieldName, String value) {
		String[] rangeStr = getDateRange(value);
		if(rangeStr[0] != null || rangeStr[1] != null) {
			cFactory.addBetween(fieldName, rangeStr[0], rangeStr[1], BetweenOperator.BETWEEN);
		}	
	}
	

}
