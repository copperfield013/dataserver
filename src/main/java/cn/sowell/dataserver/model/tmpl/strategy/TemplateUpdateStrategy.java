package cn.sowell.dataserver.model.tmpl.strategy;

import cn.sowell.dataserver.model.tmpl.pojo.Cachable;

public interface TemplateUpdateStrategy<T extends Cachable> {

	void update(T template);

	Long create(T template);
	
}
