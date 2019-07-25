package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.List;

import cho.carbon.query.entity.factory.EntityConJunctionFactory;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public class LabelIncludeAllConverter extends LabelIncludeAnyConverter{

	public LabelIncludeAllConverter() {
		super(new String[] {"l2"});
	}
	
	@Override
	public void invokeAddCriteria(EntityConJunctionFactory conjunctionFactory,
			NormalCriteria nCriteria) {
		List<String> valueSet = getValues(nCriteria.getValue());
		valueSet.forEach(label->{
			NormalCriteria nc = nCriteria.clone();
			nc.setValue(label);
			super.invokeAddCriteria(conjunctionFactory, nc);
		});
	}
	
}
