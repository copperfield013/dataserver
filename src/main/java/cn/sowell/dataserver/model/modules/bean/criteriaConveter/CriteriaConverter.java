package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.List;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public interface CriteriaConverter {

	boolean support(NormalCriteria nCriteria);

	void invokeAddCriteria(CriteriaFactory criteriaFactory, NormalCriteria nCriteria, List<Criteria> cs);

}
