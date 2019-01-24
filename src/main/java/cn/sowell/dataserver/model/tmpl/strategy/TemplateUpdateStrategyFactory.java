package cn.sowell.dataserver.model.tmpl.strategy;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;

import cn.sowell.dataserver.model.tmpl.pojo.Cachable;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;

public class TemplateUpdateStrategyFactory {

	@Resource
	ApplicationContext context;
	
	Map<Class<?>, TemplateUpdateStrategyParameter> strategyMap = new HashMap<Class<?>, TemplateUpdateStrategyParameter>();
	
	public TemplateUpdateStrategyFactory() {
		this(null);
	}
	
	public <T extends Cachable> TemplateUpdateStrategyFactory(Map<Class<T>, Class<TemplateUpdateStrategy<T>>> classMap) {
		if(classMap != null) {
			classMap.forEach((tmplClass, strategyClass)->{
				add(tmplClass, strategyClass, true);
			});
		}
		add(TemplateDetailTemplate.class, TemplateDetailUpdateStrategy.class, true);
		add(TemplateListTemplate.class, TemplateListUpdateStrategy.class, true);
		add(TemplateSelectionTemplate.class, TemplateSelectionUpdateStrategy.class, true);
		add(TemplateActionTemplate.class, TemplateActionUpdateStrategy.class, true);
	}
	
	
	private void add(Class<?> templateClass, Class<?> strategyClass, boolean isSingleton) {
		TemplateUpdateStrategyParameter param = new TemplateUpdateStrategyParameter();
		param.strategyClass = strategyClass;
		param.isSingleton = isSingleton;
		strategyMap.put(templateClass, param);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Cachable> TemplateUpdateStrategy<T> getStrategy(T template) {
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
