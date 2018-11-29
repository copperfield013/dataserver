package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.HashSet;
import java.util.Set;

import com.abc.rrc.query.criteria.EntityCriteriaFactory;
import com.abc.rrc.query.criteria.IncludeSymbol;

public class LabelIncludeAnyConverter extends MultiSupportComparatorCriteriaConverter{

	public LabelIncludeAnyConverter() {
		super(new String[]{"l1", "ms1"});
	}
	
	protected LabelIncludeAnyConverter(String[] comparatorNames) {
		super(comparatorNames);
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
	protected void addNormalCriteria(EntityCriteriaFactory cFactory, String fieldName, String value) {
		Set<String> valueSet = getValueSet(value);
		cFactory.addIncludeCriteria(fieldName, valueSet, IncludeSymbol.INCLUDES);
	}
	
	@Override
	protected void appendRelationCriterias(EntityCriteriaFactory relationEntityFactory, String suffix, String value) {
		Set<String> valueSet = getValueSet(value);
		relationEntityFactory.addIncludeCriteria(suffix, valueSet, IncludeSymbol.INCLUDES);
	}
	

}
