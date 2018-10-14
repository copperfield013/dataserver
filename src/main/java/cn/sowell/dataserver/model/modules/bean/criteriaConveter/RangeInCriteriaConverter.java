package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import org.apache.commons.lang.StringUtils;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

import cn.sowell.copframe.utils.TextUtils;

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
	protected Criteria getRelationCriteria(CriteriaFactory relationCriteriaFactory, String fieldNameInRelation,
			String value) {
		String[] rangeStr = getRange(value);
		if(rangeStr[0] != null || rangeStr[1] != null) {
			return relationCriteriaFactory.createOpenBetweenQueryCriteria(fieldNameInRelation, rangeStr[0], rangeStr[1]);
		}else {
			return null;
		}
	}
	


	@Override
	protected Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value) {
		String[] rangeStr = getRange(value);
		if(rangeStr[0] != null || rangeStr[1] != null) {
			return cFactory.createOpenBetweenQueryCriteria(fieldName, rangeStr[0], rangeStr[1]);
		}else {
			return null;
		}
	}

}
