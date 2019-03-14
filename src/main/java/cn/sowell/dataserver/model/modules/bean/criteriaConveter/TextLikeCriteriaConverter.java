package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import com.abc.rrc.query.criteria.CommonSymbol;
import com.abc.rrc.query.criteria.EntityCriteriaFactory;
import com.abc.rrc.query.criteria.IMultiAttrCriteriaFactory;

public class TextLikeCriteriaConverter extends ComparatorCriteriaConverter {

	@Override
	public String getComparator() {
		return "t1";
	}

	@Override
	protected void addNormalCriteria(IMultiAttrCriteriaFactory cFactory, String fieldName, String value) {
		cFactory.addCriteria(fieldName, value, CommonSymbol.LIKE);
	}
	
	@Override
	protected void appendRelationCriterias(EntityCriteriaFactory relationEntityFactory, String suffix, String value) {
		relationEntityFactory.addCriteria(suffix, value, CommonSymbol.LIKE);
	}
	
}
