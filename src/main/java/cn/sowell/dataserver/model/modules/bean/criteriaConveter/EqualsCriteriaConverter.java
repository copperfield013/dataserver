package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.HashSet;
import java.util.Set;

import cho.carbon.meta.enun.operator.UnaryOperator;
import cho.carbon.query.entity.factory.EnGroupJunctionFactory;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public class EqualsCriteriaConverter extends ComparatorCriteriaConverter{

	Set<String> comparators = new HashSet<>();
	
	public EqualsCriteriaConverter() {
		comparators.add("equals");
		comparators.add("t4");
		comparators.add("s1");
		comparators.add("d1");
		comparators.add("n1");
	}
	
	@Override
	public String getComparator() {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public boolean support(NormalCriteria nCriteria) {
		return (!shouldHasValue() || TextUtils.hasText(nCriteria.getValue())) && comparators.contains(nCriteria.getComparator());
	}

	@Override
	protected void addNormalCriteria(EnGroupJunctionFactory cFactory, String fieldName, String value) {
		cFactory.addCommon(fieldName, value, UnaryOperator.EQUAL);
	}
	
	

}
