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
	protected Criteria getRelationCriteria(CriteriaFactory relationCriteriaFactory, String fieldNameInRelation,
			String value) {
		String[] rangeStr = getDateRange(value);
		if(rangeStr[0] != null || rangeStr[1] != null) {
			return relationCriteriaFactory.createOpenBetweenQueryCriteria(fieldNameInRelation, rangeStr[0], rangeStr[1]);
		}else {
			return null;
		}
	}
	


	@Override
	protected Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value) {
		String[] rangeStr = getDateRange(value);
		if(rangeStr[0] != null || rangeStr[1] != null) {
			return cFactory.createOpenBetweenQueryCriteria(fieldName, rangeStr[0], rangeStr[1]);
		}else {
			return null;
		}
	}

}
