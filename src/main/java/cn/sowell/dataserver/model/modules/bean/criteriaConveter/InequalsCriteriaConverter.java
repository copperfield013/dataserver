package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.HashSet;
import java.util.Set;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

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
	protected Criteria getRelationCriteria(CriteriaFactory relationCriteriaFactory, String fieldNameInRelation,
			String value) {
		return relationCriteriaFactory.createInequalQueryCriteria(fieldNameInRelation, value);
	}

	@Override
	protected Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value) {
		return cFactory.createInequalQueryCriteria(fieldName, value);
	}

}
