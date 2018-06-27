package cn.sowell.dataserver.model.tmpl.strategy;

import cn.sowell.dataserver.model.tmpl.pojo.AbstractTemplate;

public interface TemplateUpdateStrategy<T extends AbstractTemplate> {

	void update(T template);

	Long create(T template);
	
}
