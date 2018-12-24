package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import com.abc.rrc.query.criteria.CommonSymbol;
import com.abc.rrc.query.criteria.EntityCriteriaFactory;

public class TextLeftLikeCriteriaConverter extends ComparatorCriteriaConverter {

	@Override
	public String getComparator() {
		return "t2";
	}

	
	@Override
	protected void addNormalCriteria(EntityCriteriaFactory cFactory, String fieldName, String value) {
		cFactory.addCriteria(fieldName, value, CommonSymbol.LIKE_LEFT);
	}
	
	@Override
	protected void appendRelationCriterias(EntityCriteriaFactory relationEntityFactory, String suffix, String value) {
		relationEntityFactory.addCriteria(suffix, value, CommonSymbol.LIKE_LEFT);
	}

}
