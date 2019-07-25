package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.Collection;

import org.springframework.util.Assert;

import cho.carbon.query.entity.factory.EnGroup2DJunctionFactory;
import cho.carbon.query.entity.factory.EnGroupJunctionFactory;
import cho.carbon.query.entity.factory.EnRightRelationCriterionFactory;
import cho.carbon.query.entity.factory.EnRightRelationJunctionFactory;
import cho.carbon.query.entity.factory.EntityConJunctionFactory;
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
	protected abstract void addNormalCriteria(EnGroupJunctionFactory cFactory, String fieldName, String value);
	
	@Override
	public boolean support(NormalCriteria nCriteria) {
		String comparator = getComparator();
		Assert.notNull(comparator, "ComparatorCriteriaConverter的实现类的getComparator()方法不能返回null");
		return (!shouldHasValue() || TextUtils.hasText(nCriteria.getValue())) && comparator.equalsIgnoreCase(nCriteria.getComparator());
	}
	

	/**
	 * 只能支持两层的字段条件
	 */
	@Override
	public void invokeAddCriteria(EntityConJunctionFactory conjunctionFactory, NormalCriteria nCriteria) {
		Assert.notNull(conjunctionFactory, "criteriaFactory不能为null");
		DictionaryComposite composite = nCriteria.getComposite();
		if(composite != null && composite.getRelationSubdomain() != null) {
			String compositeName = composite.getName();
			String suffix = nCriteria.getFieldName().substring(compositeName.length() + 1);
			
			if(composite.getRelationSubdomain() == null) {
				//多值属性
				EnGroup2DJunctionFactory group2DCriteriaFactory = conjunctionFactory.getGroup2DCriteriaFactory(composite.getName());
				addNormalCriteria(group2DCriteriaFactory, nCriteria.getFieldName(), nCriteria.getValue());
			}else {
				//关系属性
				EnRightRelationJunctionFactory relationCriteriaFactory = conjunctionFactory.getRighterCriteriaFactory(compositeName);
				EnRightRelationCriterionFactory rightRelationCriterionFactory = relationCriteriaFactory.getRightRelationCriterionFactory();
				if(nCriteria.getRelationLabel() != null && !nCriteria.getRelationLabel().isEmpty()) {
					Collection<String> relationTypes = TextUtils.split(nCriteria.getRelationLabel(), ",");
					rightRelationCriterionFactory.setInRelationTypes(relationTypes);
				}
				//具体字段筛选
				addNormalCriteria(rightRelationCriterionFactory.getRightJunctionFactory().getGroupFactory(), suffix, nCriteria.getValue());
			}
		}else {
			addNormalCriteria(conjunctionFactory.getGroupFactory(), nCriteria.getFieldName(), nCriteria.getValue());
		}
	}
	
	
	
	

}
