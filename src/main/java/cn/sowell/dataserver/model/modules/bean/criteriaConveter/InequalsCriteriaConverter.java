package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.HashSet;
import java.util.Set;

import com.abc.rrc.query.criteria.CommonSymbol;
import com.abc.rrc.query.criteria.EntityCriteriaFactory;
import com.abc.rrc.query.criteria.IMultiAttrCriteriaFactory;

import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public class InequalsCriteriaConverter extends ComparatorCriteriaConverter{

	Set<String> comparators = new HashSet<>();
	
	public InequalsCriteriaConverter() {
		comparators.add("t4n");
		comparators.add("s1n");
		comparators.add("d1n");
	}
	
	@Override
	public String getComparator() {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public boolean support(NormalCriteria nCriteria) {
		return comparators.contains(nCriteria.getComparator());
	}

	@Override
	protected void addNormalCriteria(IMultiAttrCriteriaFactory cFactory, String fieldName, String value) {
		cFactory.addCriteria(fieldName, value, CommonSymbol.INEQUAL);
	}
	
	@Override
	protected void appendRelationCriterias(EntityCriteriaFactory relationEntityFactory, String suffix, String value) {
		relationEntityFactory.addCriteria(suffix, value, CommonSymbol.INEQUAL);
	}
	

}
