package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.Set;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

public class LabelNotIncludeAnyConverter extends LabelIncludeAnyConverter{
	
	public LabelNotIncludeAnyConverter() {
		super(new String[] {"l1n", "ms1n"});
	}
	
	@Override
	protected Criteria getRelationCriteria(CriteriaFactory relationCriteriaFactory, String fieldNameInRelation,
			String value) {
		Set<String> valueSet = getValueSet(value);
		return relationCriteriaFactory.createNotIncludeQueryCriteria(fieldNameInRelation, valueSet);
	}

	@Override
	protected Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value) {
		Set<String> valueSet = getValueSet(value);
		return cFactory.createNotIncludeQueryCriteria(fieldName, valueSet);
	}

}
