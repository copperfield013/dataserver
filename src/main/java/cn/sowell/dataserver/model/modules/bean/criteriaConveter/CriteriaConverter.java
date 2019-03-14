package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import com.abc.rrc.query.criteria.EntityCriteriaFactory;
import com.abc.rrc.query.criteria.MultiAttrCriteriaFactory;

import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public interface CriteriaConverter {

	boolean support(NormalCriteria nCriteria);

	void invokeAddCriteria(EntityCriteriaFactory criteriaFactory,
			NormalCriteria nCriteria);

	void invokeAddCriteria(MultiAttrCriteriaFactory arrayItemCriteriaFactory, NormalCriteria nCriteria);

}
