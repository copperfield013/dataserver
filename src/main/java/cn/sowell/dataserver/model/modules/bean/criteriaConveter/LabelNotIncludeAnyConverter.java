package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.List;

import cho.carbon.meta.enun.operator.IncludeOperator;
import cho.carbon.query.entity.factory.EnGroupJunctionFactory;

public class LabelNotIncludeAnyConverter extends LabelIncludeAnyConverter{
	
	public LabelNotIncludeAnyConverter() {
		super(new String[] {"l1n", "ms1n"});
	}
	
	
	@Override
	protected void addNormalCriteria(EnGroupJunctionFactory cFactory, String fieldName, String value) {
		List<String> valueSet = getValues(value);
		cFactory.addInclude(fieldName, valueSet, IncludeOperator.EXCLUDES);
	}
}
