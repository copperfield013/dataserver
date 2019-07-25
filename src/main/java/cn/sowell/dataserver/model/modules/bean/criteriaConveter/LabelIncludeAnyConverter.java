package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.ArrayList;
import java.util.List;

import cho.carbon.meta.enun.operator.IncludeOperator;
import cho.carbon.query.entity.factory.EnGroupJunctionFactory;

public class LabelIncludeAnyConverter extends MultiSupportComparatorCriteriaConverter{

	public LabelIncludeAnyConverter() {
		super(new String[]{"l1", "ms1"});
	}
	
	protected LabelIncludeAnyConverter(String[] comparatorNames) {
		super(comparatorNames);
	}
	

	protected List<String> getValues(String value) {
		List<String> valueSet = new ArrayList<String>();
		if(value != null) {
			for(String val : value.split(",")) {
				valueSet.add(val);
			}
		}
		return valueSet;
	}

	
	@Override
	protected void addNormalCriteria(EnGroupJunctionFactory cFactory, String fieldName, String value) {
		List<String> valueSet = getValues(value);
		cFactory.addInclude(fieldName, valueSet, IncludeOperator.INCLUDES);
	}
	
	

}
