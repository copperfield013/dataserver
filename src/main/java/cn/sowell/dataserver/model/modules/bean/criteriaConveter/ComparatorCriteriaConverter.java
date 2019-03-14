package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import org.springframework.util.Assert;

import com.abc.rrc.query.criteria.EntityCriteriaFactory;
import com.abc.rrc.query.criteria.EntityRelationCriteriaFactory;
import com.abc.rrc.query.criteria.EntityUnRecursionCriteriaFactory;
import com.abc.rrc.query.criteria.IMultiAttrCriteriaFactory;
import com.abc.rrc.query.criteria.MultiAttrCriteriaFactory;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public abstract class ComparatorCriteriaConverter implements CriteriaConverter{

	/**
	 * 不能返回null
	 * @return
	 */
	public abstract String getComparator();
	public boolean shouldHasValue() {
		return true;
	}
	protected abstract void addNormalCriteria(IMultiAttrCriteriaFactory cFactory, String fieldName, String value);
	
	@Override
	public boolean support(NormalCriteria nCriteria) {
		String comparator = getComparator();
		Assert.notNull(comparator, "ComparatorCriteriaConverter的实现类的getComparator()方法不能返回null");
		return (!shouldHasValue() || TextUtils.hasText(nCriteria.getValue())) && comparator.equalsIgnoreCase(nCriteria.getComparator());
	}
	

	@Override
	public void invokeAddCriteria(EntityCriteriaFactory criteriaFactory, NormalCriteria nCriteria) {
		Assert.notNull(criteriaFactory, "criteriaFactory不能为null");
		DictionaryComposite composite = nCriteria.getComposite();
		if(composite != null && composite.getRelationSubdomain() != null) {
			String compositeName = composite.getName();
			String suffix = nCriteria.getFieldName().substring(compositeName.length() + 1);
			EntityRelationCriteriaFactory relationCriteriaFactory = criteriaFactory.getRelationCriteriaFacotry(compositeName);
			//关系名筛选
			EntityUnRecursionCriteriaFactory unrecursionCriteriaFactory = 
					relationCriteriaFactory.getEntityUnRecursionCriteriaFactory()
					.setIncludeRType(nCriteria.getRelationLabel());
			EntityCriteriaFactory rightCriteriaFactory = unrecursionCriteriaFactory.getRightEntityCriteriaFactory();
			//具体字段筛选
			appendRelationCriterias(rightCriteriaFactory, suffix, nCriteria.getValue());
			/*
			
			String mappingName = fusionContext.getABCNode().getRelation(compositeName).getFullTitle();
			BizFusionContext relationFusionContext = new BizFusionContext();
			relationFusionContext.setMappingName(mappingName);
			relationFusionContext.setSource(FusionContext.SOURCE_COMMON);
			relationFusionContext.setToEntityRange(FusionContext.ENTITY_CONTENT_RANGE_INTERSECTION);
			
			EntityCriteriaFactory relationEntityFactory = new EntityCriteriaFactory(relationFusionContext);
			appendRelationCriterias(relationEntityFactory, suffix, nCriteria.getValue());
			if(relationEntityFactory.getCriterias() != null) {
				criteriaFactory.addRelationCriteria(compositeName, nCriteria.getRelationLabel(), relationEntityFactory.getCriterias());
			}*/
		}else {
			addNormalCriteria(criteriaFactory, nCriteria.getFieldName(), nCriteria.getValue());
		}
	}
	
	@Override
	public void invokeAddCriteria(MultiAttrCriteriaFactory arrayItemCriteriaFactory, NormalCriteria nCriteria) {
		Assert.notNull(arrayItemCriteriaFactory, "criteriaFactory不能为null");
		addNormalCriteria(arrayItemCriteriaFactory, nCriteria.getFieldName(), nCriteria.getValue());
	}
	
	protected void appendRelationCriterias(EntityCriteriaFactory relationEntityFactory, String suffix, String value) {
		
	}

}
