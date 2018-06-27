package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import cn.sowell.dataserver.model.tmpl.pojo.AbstractTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTempalte;
import cn.sowell.dataserver.model.tmpl.strategy.TemplateDetailUpdateStrategy;
import cn.sowell.dataserver.model.tmpl.strategy.TemplateListUpdateStrategy;
import cn.sowell.dataserver.model.tmpl.strategy.TemplateUpdateStrategy;

@Repository
public class TemplateUpdateStrategyFactory {

	@Resource
	ApplicationContext context;
	
	Map<Class<?>, TemplateUpdateStrategyParameter> strategyMap = new HashMap<Class<?>, TemplateUpdateStrategyParameter>();
	
	public TemplateUpdateStrategyFactory() {
		add(TemplateDetailTemplate.class, TemplateDetailUpdateStrategy.class, true);
		add(TemplateListTempalte.class, TemplateListUpdateStrategy.class, true);
	}
	
	private void add(Class<?> templateClass, Class<?> strategyClass, boolean isSingleton) {
		TemplateUpdateStrategyParameter param = new TemplateUpdateStrategyParameter();
		param.strategyClass = strategyClass;
		param.isSingleton = isSingleton;
		strategyMap.put(templateClass, param);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends AbstractTemplate> TemplateUpdateStrategy<T> getStrategy(T template) {
		if(strategyMap.containsKey(template.getClass())) {
			TemplateUpdateStrategyParameter param = strategyMap.get(template.getClass());
			if(!param.isSingleton || param.strategy == null) {
				param.strategy = (TemplateUpdateStrategy<T>) context.getAutowireCapableBeanFactory().createBean(param.strategyClass);
			}
			return (TemplateUpdateStrategy<T>) param.strategy;
		}
		return null;
		
	}
	
	private class TemplateUpdateStrategyParameter{
		boolean isSingleton;
		Class<?> strategyClass;
		TemplateUpdateStrategy<?> strategy; 
	}

}
