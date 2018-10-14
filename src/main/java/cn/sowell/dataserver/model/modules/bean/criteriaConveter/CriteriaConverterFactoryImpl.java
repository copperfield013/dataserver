package cn.sowell.dataserver.model.modules.bean.criteriaConveter;

import java.util.ArrayList;
import java.util.List;

import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public class CriteriaConverterFactoryImpl implements CriteriaConverterFactory
{

	List<CriteriaConverter> converters = new ArrayList<>();
	
	public CriteriaConverterFactoryImpl() {
		converters.add(new EqualsCriteriaConverter());
		converters.add(new InequalsCriteriaConverter());
		converters.add(new IsNullCriteriaConverter());
		converters.add(new IsNotNullCriteriaConverter());
		converters.add(new TextLikeCriteriaConverter());
		converters.add(new TextLeftLikeCriteriaConverter());
		converters.add(new TextRightLikeCriteriaConverter());
		converters.add(new DateBeforeCriteriaConverter());
		converters.add(new DateAfterCriteriaConverter());
		converters.add(new LabelIncludeAnyConverter());
		converters.add(new LabelIncludeAllConverter());
		converters.add(new LabelNotIncludeAnyConverter());
		converters.add(new DateRangeInCriteriaConverter());
		converters.add(new RangeInCriteriaConverter());
		converters.add(new RelationExistCriteriaConverter());
	}
	
	
	public void setExtendConverters(List<CriteriaConverter> extendConverters) {
		converters.addAll(0, extendConverters);
	}
	
	
	@Override
	public CriteriaConverter getConverter(NormalCriteria nCriteria) {
		for (CriteriaConverter converter : converters) {
			if(converter.support(nCriteria)) {
				return converter;
			}
		}
		return null;
	}
	
}
