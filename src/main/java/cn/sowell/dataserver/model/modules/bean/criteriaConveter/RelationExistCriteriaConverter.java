package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.List;

import com.abc.application.BizFusionContext;
import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public class RelationExistCriteriaConverter implements CriteriaConverter{

	@Override
	public boolean support(NormalCriteria nCriteria) {
		return nCriteria.getCompositeId() != null && "re1".equals(nCriteria.getComparator());
	}

	@Override
	public void invokeAddCriteria(BizFusionContext fusionContext, NormalCriteria nCriteria, List<Criteria> cs) {
		if(nCriteria.getComposite() != null && TextUtils.hasText(nCriteria.getValue())) {
			String compositeName = nCriteria.getComposite().getName();
			CriteriaFactory cFactory = new CriteriaFactory(fusionContext);
			cs.add(cFactory.createRelationCriteria(compositeName, nCriteria.getValue(), null));
			
		}
	}

}
