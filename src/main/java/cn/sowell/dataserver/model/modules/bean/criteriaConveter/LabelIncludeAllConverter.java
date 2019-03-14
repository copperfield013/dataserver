package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.Set;

import com.abc.rrc.query.criteria.EntityCriteriaFactory;

import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public class LabelIncludeAllConverter extends LabelIncludeAnyConverter{

	public LabelIncludeAllConverter() {
		super(new String[] {"l2"});
	}
	
	@Override
	public void invokeAddCriteria(EntityCriteriaFactory criteriaFactory,
			NormalCriteria nCriteria) {
		Set<String> valueSet = getValueSet(nCriteria.getValue());
		valueSet.forEach(label->{
			NormalCriteria nc = nCriteria.clone();
			nc.setValue(label);
			super.invokeAddCriteria(criteriaFactory, nc);
		});
	}
	
}
