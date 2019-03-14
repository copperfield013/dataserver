package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import com.abc.rrc.query.criteria.BetweenSymbol;
import com.abc.rrc.query.criteria.EntityCriteriaFactory;
import com.abc.rrc.query.criteria.IMultiAttrCriteriaFactory;

public class DateAfterCriteriaConverter extends MultiSupportComparatorCriteriaConverter {

	public DateAfterCriteriaConverter() {
		super(new String[] {"d3", "n3"});
	}
	

	@Override
	protected void addNormalCriteria(IMultiAttrCriteriaFactory cFactory, String fieldName, String value) {
		cFactory.addBetweenCriteria(fieldName, value, null, BetweenSymbol.BETWEEN);
	}
	
	@Override
	protected void appendRelationCriterias(EntityCriteriaFactory relationEntityFactory, String suffix, String value) {
		relationEntityFactory.addBetweenCriteria(suffix, value, null, BetweenSymbol.BETWEEN);
	}
	
}
