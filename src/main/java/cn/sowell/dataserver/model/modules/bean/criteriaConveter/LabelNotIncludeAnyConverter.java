package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.Set;

import com.abc.rrc.query.criteria.EntityCriteriaFactory;
import com.abc.rrc.query.criteria.IMultiAttrCriteriaFactory;
import com.abc.rrc.query.criteria.IncludeSymbol;

public class LabelNotIncludeAnyConverter extends LabelIncludeAnyConverter{
	
	public LabelNotIncludeAnyConverter() {
		super(new String[] {"l1n", "ms1n"});
	}
	
	
	@Override
	protected void addNormalCriteria(IMultiAttrCriteriaFactory cFactory, String fieldName, String value) {
		Set<String> valueSet = getValueSet(value);
		cFactory.addIncludeCriteria(fieldName, valueSet, IncludeSymbol.NOT_INCLUDES);
	}
	
	@Override
	protected void appendRelationCriterias(EntityCriteriaFactory relationEntityFactory, String suffix, String value) {
		Set<String> valueSet = getValueSet(value);
		relationEntityFactory.addIncludeCriteria(suffix, valueSet, IncludeSymbol.NOT_INCLUDES);
	}

}
