package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.List;
import java.util.Set;

import com.abc.application.BizFusionContext;
import com.abc.query.criteria.Criteria;

import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public class LabelIncludeAllConverter extends LabelIncludeAnyConverter{

	public LabelIncludeAllConverter() {
		super(new String[] {"l2"});
	}
	
	@Override
	public void invokeAddCriteria(BizFusionContext fusionContext,
			NormalCriteria nCriteria, List<Criteria> cs) {
		Set<String> valueSet = getValueSet(nCriteria.getValue());
		valueSet.forEach(label->{
			NormalCriteria nc = nCriteria.clone();
			nc.setValue(label);
			super.invokeAddCriteria(fusionContext, nc, cs);
		});
	}
	
	

}
