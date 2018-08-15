package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.List;

import org.springframework.util.Assert;

import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;

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
	protected abstract Criteria getNormalCriteria(CriteriaFactory cFactory, String fieldName, String value);
	protected abstract Criteria getRelationCriteria(CriteriaFactory cFactory, String compositeName, String relationLabel, String suffix, String value);
	
	@Override
	public boolean support(NormalCriteria nCriteria) {
		String comparator = getComparator();
		Assert.notNull(comparator, "ComparatorCriteriaConverter的实现类的getComparator()方法不能返回null");
		return (!shouldHasValue() || TextUtils.hasText(nCriteria.getValue())) && comparator.equalsIgnoreCase(nCriteria.getComparator());
	}
	

	@Override
	public void invokeAddCriteria(CriteriaFactory criteriaFactory, NormalCriteria nCriteria, List<Criteria> cs) {
		Assert.notNull(criteriaFactory, "criteriaFactory不能为null");
		DictionaryComposite composite = nCriteria.getComposite();
		Criteria criteria;
		if(composite != null && composite.getRelationSubdomain() != null) {
			String compositeName = composite.getName();
			String suffix = nCriteria.getFieldName().substring(compositeName.length() + 1);
			criteria = getRelationCriteria(criteriaFactory, compositeName, 
					nCriteria.getRelationLabel(),
					suffix, 
					nCriteria.getValue());
		}else {
			criteria = getNormalCriteria(criteriaFactory, nCriteria.getFieldName(), nCriteria.getValue());
		}
		
		cs.add(criteria);
	}
	

	

	
	

}
