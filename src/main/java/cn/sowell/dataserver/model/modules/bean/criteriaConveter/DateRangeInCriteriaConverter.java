package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.Date;

import com.abc.rrc.query.criteria.BetweenSymbol;
import com.abc.rrc.query.criteria.EntityCriteriaFactory;
import com.abc.rrc.query.criteria.IMultiAttrCriteriaFactory;

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
	protected void addNormalCriteria(IMultiAttrCriteriaFactory cFactory, String fieldName, String value) {
		String[] rangeStr = getDateRange(value);
		if(rangeStr[0] != null || rangeStr[1] != null) {
			cFactory.addBetweenCriteria(fieldName, rangeStr[0], rangeStr[1], BetweenSymbol.BETWEEN);
		}
	}
	
	@Override
	protected void appendRelationCriterias(EntityCriteriaFactory relationEntityFactory, String suffix, String value) {
		String[] rangeStr = getDateRange(value);
		if(rangeStr[0] != null || rangeStr[1] != null) {
			relationEntityFactory.addBetweenCriteria(suffix, rangeStr[0], rangeStr[1], BetweenSymbol.BETWEEN);
		}
	}

}
