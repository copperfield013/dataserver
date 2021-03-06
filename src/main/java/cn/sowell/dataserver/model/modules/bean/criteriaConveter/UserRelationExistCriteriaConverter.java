package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import javax.annotation.Resource;

import cho.carbon.meta.enun.operator.UnaryOperator;
import cho.carbon.query.entity.factory.EnRightRelationJunctionFactory;
import cho.carbon.query.entity.factory.EntityConJunctionFactory;
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
	public void invokeAddCriteria(EntityConJunctionFactory conjunctionFactory,
			NormalCriteria nCriteria) {
		if(nCriteria.getComposite() != null && TextUtils.hasText(nCriteria.getValue())) {
			String compositeName = nCriteria.getComposite().getName();
			
			EnRightRelationJunctionFactory rightCriteriaFactory = conjunctionFactory.getRighterCriteriaFactory(compositeName);
			
			String userCode = getUserCode();
			rightCriteriaFactory.getRightRelationCriterionFactory()
				.getRightJunctionFactory().getGroupFactory()
				.addCommon(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL, userCode, UnaryOperator.EQUAL);
			
			
			/*
			 * EntityRelationCriteriaFactory relationCriteriaFactory =
			 * criteriaFactory.getRelationCriteriaFacotry(compositeName);
			 * EntityUnRecursionCriteriaFactory unrecursionCriteriaFactory =
			 * relationCriteriaFactory.getEntityUnRecursionCriteriaFactory();
			 * 
			 * String userCode = getUserCode(); unrecursionCriteriaFactory
			 * .setIncludeRType(nCriteria.getValue()).getRightEntityCriteriaFactory()
			 * .addCriteria(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL, userCode,
			 * CommonSymbol.EQUAL) ;
			 */
			
			/*
			String mappingName = fusionContext.getABCNode().getRelation(compositeName).getFullTitle();
			BizFusionContext relationFusionContext = new BizFusionContext();
			relationFusionContext.setMappingName(mappingName);
			relationFusionContext.setSource(FusionContext.SOURCE_COMMON);
			relationFusionContext.setToEntityRange(FusionContext.ENTITY_CONTENT_RANGE_INTERSECTION);
			
			EntityCriteriaFactory relationCriteriaFactory = new EntityCriteriaFactory(relationFusionContext);
			String userCode = getUserCode();
			
			relationCriteriaFactory.addCriteria(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL, userCode, CommonSymbol.EQUAL);
			criteriaFactory.addRelationCriteria(compositeName, nCriteria.getValue(), relationCriteriaFactory.getCriterias());*/
			
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
