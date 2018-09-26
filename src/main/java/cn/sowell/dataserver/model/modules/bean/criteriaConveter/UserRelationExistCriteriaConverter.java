package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.List;

import javax.annotation.Resource;

import com.abc.application.BizFusionContext;
import com.abc.application.FusionContext;
import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;
import com.abc.query.criteria.QueryCriteria;
import com.beust.jcommander.internal.Lists;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.impl.ABCNodeProxy;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public class UserRelationExistCriteriaConverter implements CriteriaConverter{

	public static interface UserCodeSupplier {
		String getUserCode();
	}
	
	@Resource
	UserCodeSupplier userCodeSupplier;
	
	@Override
	public boolean support(NormalCriteria nCriteria) {
		return userCodeSupplier != null && nCriteria.getCompositeId() != null && "re2".equals(nCriteria.getComparator());
	}

	@Override
	public void invokeAddCriteria(BizFusionContext fusionContext, NormalCriteria nCriteria, List<Criteria> cs) {
		if(nCriteria.getComposite() != null && TextUtils.hasText(nCriteria.getValue())) {
			String compositeName = nCriteria.getComposite().getName();
			CriteriaFactory cFactory = new CriteriaFactory(fusionContext);
			
			String mappingName = fusionContext.getABCNode().getRelation(compositeName).getFullTitle();
			BizFusionContext relationFusionContext = new BizFusionContext();
			relationFusionContext.setMappingName(mappingName);
			relationFusionContext.setSource(FusionContext.SOURCE_COMMON);
			relationFusionContext.setToEntityRange(FusionContext.ENTITY_CONTENT_RANGE_INTERSECTION);
			
			CriteriaFactory relationCriteriaFactory = new CriteriaFactory(relationFusionContext);
			String userCode = getUserCode();
			QueryCriteria userCodeCriteria = relationCriteriaFactory.createQueryCriteria(ABCNodeProxy.CODE_PROPERTY_NAME, userCode);
			
			cs.add(cFactory.createRelationCriteria(compositeName, nCriteria.getValue(), Lists.newArrayList(userCodeCriteria)));
			
		}
	}

	private String getUserCode() {
		if(userCodeSupplier != null) {
			return userCodeSupplier.getUserCode();
		}
		return null;
	}

	public UserCodeSupplier getUserCodeSupplier() {
		return userCodeSupplier;
	}

	public void setUserCodeSupplier(UserCodeSupplier userCodeSupplier) {
		this.userCodeSupplier = userCodeSupplier;
	}

}
