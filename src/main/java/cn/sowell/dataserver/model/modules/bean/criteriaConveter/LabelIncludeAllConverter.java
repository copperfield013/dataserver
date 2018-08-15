package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.List;
import java.util.Set;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public class LabelIncludeAllConverter extends LabelIncludeAnyConverter{

	@Override
	public String getComparator() {
		return "l2";
	}
	
	
	@Override
	public void invokeAddCriteria(CriteriaFactory criteriaFactory, NormalCriteria nCriteria, List<Criteria> cs) {
		Set<String> valueSet = getValueSet(nCriteria.getValue());
		valueSet.forEach(label->{
			NormalCriteria nc = nCriteria.clone();
			nc.setValue(label);
			super.invokeAddCriteria(criteriaFactory, nc, cs);
		});
	}

}
