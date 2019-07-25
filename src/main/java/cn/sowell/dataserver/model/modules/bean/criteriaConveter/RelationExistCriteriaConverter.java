package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import cho.carbon.query.entity.factory.EntityConJunctionFactory;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public class RelationExistCriteriaConverter implements CriteriaConverter{

	@Override
	public boolean support(NormalCriteria nCriteria) {
		return "re1".equals(nCriteria.getComparator()) && nCriteria.getCompositeId() != null && nCriteria.getComposite().getRelationSubdomain() != null;
	}

	@Override
	public void invokeAddCriteria(EntityConJunctionFactory conjunctionFactory,
			NormalCriteria nCriteria) {
		if(nCriteria.getComposite() != null && TextUtils.hasText(nCriteria.getValue())) {
			String compositeName = nCriteria.getComposite().getName();
			conjunctionFactory.getRighterCriteriaFactory(compositeName).getRightRelationCriterionFactory().setInRelationTypes(nCriteria.getValue());
		}
	}

}
