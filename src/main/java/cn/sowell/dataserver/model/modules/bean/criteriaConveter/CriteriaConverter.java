package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.List;

import com.abc.application.BizFusionContext;
import com.abc.query.criteria.Criteria;

import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public interface CriteriaConverter {

	boolean support(NormalCriteria nCriteria);

	void invokeAddCriteria(BizFusionContext fusionContext, NormalCriteria nCriteria,
			List<Criteria> cs);

}
