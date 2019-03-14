package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import com.abc.rrc.query.criteria.EntityCriteriaFactory;
import com.abc.rrc.query.criteria.IMultiAttrCriteriaFactory;
import com.abc.rrc.query.criteria.NullSymbol;

public class IsNullCriteriaConverter extends ComparatorCriteriaConverter {

	@Override
	public String getComparator() {
		return "none1n";
	}
	
	@Override
	public boolean shouldHasValue() {
		return false;
	}

	@Override
	protected void addNormalCriteria(IMultiAttrCriteriaFactory cFactory, String fieldName, String value) {
		cFactory.addNullCriteria(fieldName, NullSymbol.ISNULL);
	}
	
	@Override
	protected void appendRelationCriterias(EntityCriteriaFactory relationEntityFactory, String suffix, String value) {
		relationEntityFactory.addNullCriteria(suffix, NullSymbol.ISNULL);
	}
	

}
