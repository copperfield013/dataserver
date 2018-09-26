package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.HashSet;
import java.util.Set;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public abstract class MultiSupportComparatorCriteriaConverter extends ComparatorCriteriaConverter{
	Set<String> comparators = new HashSet<>();
	
	public MultiSupportComparatorCriteriaConverter(String[] comparatorNames) {
		for (String comparatorName : comparatorNames) {
			comparators.add(comparatorName);
		}
	}
	
	@Override
	public final String getComparator() {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public final boolean support(NormalCriteria nCriteria) {
		return (!shouldHasValue() || TextUtils.hasText(nCriteria.getValue())) && comparators.contains(nCriteria.getComparator());
	}
}
