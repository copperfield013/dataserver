package cn.sowell.dataserver.model.tmpl.manager.impl;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.sowell.dataserver.model.cachable.manager.AbstractModuleCacheManager;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.tmpl.dao.StatViewDao;
import cn.sowell.dataserver.model.tmpl.manager.StatListTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.StatViewManager;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToStatView;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToStatView.PreparedToStatView;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatView;

@Component
public class StatViewManagerImpl
	extends AbstractModuleCacheManager<TemplateStatView, StatViewDao, GlobalPreparedToStatView, PreparedToStatView>
	implements StatViewManager{

	@Resource
	StatListTemplateManager stmplManager;
	private Set<Consumer<TemplateStatView>> consumers = new LinkedHashSet<Consumer<TemplateStatView>>();
	
	
	static Logger logger = Logger.getLogger(StatViewManagerImpl.class);
	
	
	@Autowired
	public StatViewManagerImpl(@Autowired StatViewDao dao, @Autowired ModuleCachableMetaSupportor metaSupportor) {
		super(dao, metaSupportor);
	}

	@Override
	protected GlobalPreparedToStatView getGlobalPreparedToCache() {
		return null;
	}

	@Override
	protected PreparedToStatView extractPrepare(GlobalPreparedToStatView globalPreparedToCache,
			TemplateStatView cachable) {
		return null;
	}

	@Override
	protected PreparedToStatView getPreparedToCache(TemplateStatView cachable) {
		return null;
	}

	@Override
	protected void handlerCache(TemplateStatView latest, PreparedToStatView prepareToCache) {
	}

	@Override
	protected TemplateStatView createCachablePojo() {
		return new TemplateStatView();
	}

	@Override
	protected Long doCreate(TemplateStatView cachable) {
		return getDao().getNormalOperateDao().save(cachable);
	}

	@Override
	protected void doUpdate(TemplateStatView cachable) {
		getDao().getNormalOperateDao().update(cachable);
	}
	
	@Override
	protected void afterReloadCache(TemplateStatView statView) {
		triggerStatViewReloadEvent(statView);
	}

	private void triggerStatViewReloadEvent(TemplateStatView statView) {
		for (Consumer<TemplateStatView> consumer : consumers ) {
			try {
				consumer.accept(statView);
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}
	
	@Override
	public void bindStatViewReloadEvent(Consumer<TemplateStatView> consumer) {
		consumers.add(consumer);
	}


}
