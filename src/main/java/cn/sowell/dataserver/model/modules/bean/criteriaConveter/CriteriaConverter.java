package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import cho.carbon.query.entity.factory.EntityConJunctionFactory;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public interface CriteriaConverter {

	boolean support(NormalCriteria nCriteria);

	void invokeAddCriteria(EntityConJunctionFactory conjunctionFactory,
			NormalCriteria nCriteria);

}
