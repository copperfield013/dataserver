package cn.sowell.dataserver.model.tmpl.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.tmpl.dao.StatListTemplateDao;
import cn.sowell.dataserver.model.tmpl.manager.StatListTemplateManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatList;

@Component
public class StatListTemplateManagerImpl 
	extends AbstractListTemplateManager<TemplateStatList, TemplateStatColumn, TemplateStatCriteria>
	implements StatListTemplateManager{

	@Autowired
	public StatListTemplateManagerImpl(
			@Autowired StatListTemplateDao dao,
			@Autowired ModuleCachableMetaSupportor metaSupportor) {
		super(dao, metaSupportor);
	}

	@Override
	protected TemplateStatList createCachablePojo() {
		return new TemplateStatList();
	}
	
	@Override
	protected void updateCriteria(TemplateStatCriteria originCriteria, TemplateStatCriteria criteria) {
		super.updateCriteria(originCriteria, criteria);
		originCriteria.setFilterOccasion(criteria.getFilterOccasion());
	}

}
