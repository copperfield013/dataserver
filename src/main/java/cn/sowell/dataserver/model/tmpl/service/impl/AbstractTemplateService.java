package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cn.sowell.dataserver.model.tmpl.manager.ModuleCachableManager;
import cn.sowell.dataserver.model.tmpl.pojo.Cachable;
import cn.sowell.dataserver.model.tmpl.service.OpenTemplateService;

public abstract class AbstractTemplateService<T extends Cachable, M extends ModuleCachableManager<T>> implements OpenTemplateService<T> {

	private final M manager;
	
	protected AbstractTemplateService(M manager) {
		Assert.notNull(manager);
		this.manager = manager;
	}
	
	@Transactional(propagation=Propagation.NEVER)
	protected M getManager() {
		return this.manager;
	}
	
	
	@Override
	public List<T> queryAll(String moduleName) {
		return manager.queryByModule(moduleName);
	}

	@Override
	public T getTemplate(Long tmplId) {
		return manager.get(tmplId);
	}

	@Override
	public void remove(Long tmplId) {
		manager.remove(tmplId);
	}

	@Override
	public Long merge(T tmpl) {
		return manager.merge(tmpl);
	}
	
}
