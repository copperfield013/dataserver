package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.HashSet;
import java.util.Set;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

public class LabelIncludeAnyConverter extends ComparatorCriteriaConverter{

	@Override
	public String getComparator() {
		return "l1";
	}
	
	@Override
	protected Criteria getRelationCriteria(CriteriaFactory cFactory, String compositeName, String relationLabel,
			String suffix, String value) {
		Set<String> valueSet = getValueSet(value);
		return cFactory.createIncludeQueryCriteria(
				compositeName, 
				relationLabel,
				suffix, 
				valueSet
				);
	}

	protected Set<String> getValueSet(String value) {
		Set<String> valueSet = new HashSet<>();
		if(value != null) {
			for(String val : value.split(",")) {
				valueSet.add(val);
			}
		}
		return valueSet;
	}

	@Override
	protected Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value) {
		Set<String> valueSet = getValueSet(value);
		return cFactory.createIncludeQueryCriteria(fieldName, valueSet);
	}

}
