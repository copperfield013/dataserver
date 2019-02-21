package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import com.abc.application.BizFusionContext;
import com.abc.rrc.query.criteria.EntityCriteriaFactory;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public class RelationNotExistCriteriaConverter implements CriteriaConverter{

	@Override
	public boolean support(NormalCriteria nCriteria) {
		return nCriteria.getCompositeId() != null && "re1n".equals(nCriteria.getComparator());
	}

	@Override
	public void invokeAddCriteria(BizFusionContext fusionContext, EntityCriteriaFactory criteriaFactory,
			NormalCriteria nCriteria) {
		if(nCriteria.getComposite() != null && TextUtils.hasText(nCriteria.getValue())) {
			String compositeName = nCriteria.getComposite().getName();
			criteriaFactory.addRelationCriteria(compositeName, null, nCriteria.getValue(), null);
		}
	}
	
}
