package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import com.abc.rrc.query.criteria.EntityCriteriaFactory;
import com.abc.rrc.query.criteria.MultiAttrCriteriaFactory;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public class RelationExistCriteriaConverter implements CriteriaConverter{

	@Override
	public boolean support(NormalCriteria nCriteria) {
		return nCriteria.getCompositeId() != null && "re1".equals(nCriteria.getComparator());
	}

	@Override
	public void invokeAddCriteria(EntityCriteriaFactory criteriaFactory,
			NormalCriteria nCriteria) {
		if(nCriteria.getComposite() != null && TextUtils.hasText(nCriteria.getValue())) {
			String compositeName = nCriteria.getComposite().getName();
			criteriaFactory.getRelationCriteriaFacotry(compositeName)
				.getEntityUnRecursionCriteriaFactory()
				.setIncludeRType(nCriteria.getValue());
			//criteriaFactory.addRelationCriteria(compositeName, nCriteria.getValue(), null);
		}
	}

	@Override
	public void invokeAddCriteria(MultiAttrCriteriaFactory arrayItemCriteriaFactory, NormalCriteria nCriteria) {
		throw new UnsupportedOperationException();
	}
	
	

}
