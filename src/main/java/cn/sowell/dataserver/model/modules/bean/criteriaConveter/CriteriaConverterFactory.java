package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public interface CriteriaConverterFactory {

	CriteriaConverter getConverter(NormalCriteria nCriteria);

}
