package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.Set;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

public class LabelNotIncludeAnyConverter extends LabelIncludeAnyConverter{

	@Override
	public String getComparator() {
		return "l1n";
	}
	
	@Override
	protected Criteria getRelationCriteria(CriteriaFactory cFactory, String compositeName, String relationLabel,
			String suffix, String value) {
		Set<String> valueSet = getValueSet(value);
		return cFactory.createNotIncludeQueryCriteria(
				compositeName, 
				relationLabel,
				suffix, 
				valueSet
				);
	}

	@Override
	protected Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value) {
		Set<String> valueSet = getValueSet(value);
		return cFactory.createNotIncludeQueryCriteria(fieldName, valueSet);
	}

}
