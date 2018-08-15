package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.Date;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

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
	protected Criteria getRelationCriteria(CriteriaFactory cFactory, String compositeName, String relationLabel,
			String suffix, String value) {
		String[] rangeStr = getDateRange(value);
		return cFactory.createOpenBetweenQueryCriteria(
				compositeName, 
				relationLabel,
				suffix,
				rangeStr[0],
				rangeStr[1]);
	}
	


	@Override
	protected Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value) {
		String[] rangeStr = getDateRange(value);
		return cFactory.createOpenBetweenQueryCriteria(fieldName, rangeStr[0], rangeStr[1]);
	}

}
