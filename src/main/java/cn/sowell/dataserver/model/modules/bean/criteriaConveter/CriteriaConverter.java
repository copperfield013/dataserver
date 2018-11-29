package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import com.abc.application.BizFusionContext;
import com.abc.rrc.query.criteria.EntityCriteriaFactory;

import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public interface CriteriaConverter {

	boolean support(NormalCriteria nCriteria);

	void invokeAddCriteria(BizFusionContext fusionContext, EntityCriteriaFactory criteriaFactory,
			NormalCriteria nCriteria);

}
