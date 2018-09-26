package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.HashSet;
import java.util.Set;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public class EqualsCriteriaConverter extends ComparatorCriteriaConverter{

	Set<String> comparators = new HashSet<>();
	
	public EqualsCriteriaConverter() {
		comparators.add("equals");
		comparators.add("t4");
		comparators.add("s1");
		comparators.add("d1");
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
	protected Criteria getRelationCriteria(CriteriaFactory relationCriteriaFactory, String fieldNameInRelation,
			String value) {
		return relationCriteriaFactory.createQueryCriteria(fieldNameInRelation, value);
	}

	@Override
	protected Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value) {
		return cFactory.createQueryCriteria(fieldName, value);
	}

}
