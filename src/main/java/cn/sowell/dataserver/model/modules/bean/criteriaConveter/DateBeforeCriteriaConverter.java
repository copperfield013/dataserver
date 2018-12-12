package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import com.abc.rrc.query.criteria.BetweenSymbol;
import com.abc.rrc.query.criteria.EntityCriteriaFactory;

public class DateBeforeCriteriaConverter extends MultiSupportComparatorCriteriaConverter {

	public DateBeforeCriteriaConverter() {
		super(new String[]{"d2", "n2"});
	}
	

	@Override
	protected void addNormalCriteria(EntityCriteriaFactory cFactory, String fieldName, String value) {
		cFactory.addBetweenCriteria(fieldName, null, value, BetweenSymbol.BETWEEN);
	}
	
	@Override
	protected void appendRelationCriterias(EntityCriteriaFactory relationEntityFactory, String suffix, String value) {
		relationEntityFactory.addBetweenCriteria(suffix, null, value, BetweenSymbol.BETWEEN);
	}
	

}
