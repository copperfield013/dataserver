package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.HashSet;
import java.util.Set;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

public class LabelIncludeAnyConverter extends MultiSupportComparatorCriteriaConverter{

	public LabelIncludeAnyConverter() {
		super(new String[]{"l1", "ms1"});
	}
	
	protected LabelIncludeAnyConverter(String[] comparatorNames) {
		super(comparatorNames);
	}
	
	
	@Override
	protected Criteria getRelationCriteria(CriteriaFactory relationCriteriaFactory, String fieldNameInRelation,
			String value) {
		Set<String> valueSet = getValueSet(value);
		return relationCriteriaFactory.createIncludeQueryCriteria(fieldNameInRelation, valueSet);
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
