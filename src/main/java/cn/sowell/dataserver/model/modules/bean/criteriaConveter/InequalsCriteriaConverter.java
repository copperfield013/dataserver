package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.HashSet;
import java.util.Set;

import cho.carbon.meta.enun.operator.UnaryOperator;
import cho.carbon.query.entity.factory.EnGroupJunctionFactory;
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
	protected void addNormalCriteria(EnGroupJunctionFactory cFactory, String fieldName, String value) {
		cFactory.addCommon(fieldName, value, UnaryOperator.INEQUAL);
	}
	
}
