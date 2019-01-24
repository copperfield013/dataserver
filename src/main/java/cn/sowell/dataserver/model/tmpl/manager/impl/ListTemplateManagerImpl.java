package cn.sowell.dataserver.model.tmpl.manager.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.tmpl.dao.ListTemplateDao;
import cn.sowell.dataserver.model.tmpl.manager.ListTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.TemplateGroupManager;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToListTemplate.PreparedToListTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

@Component
public class ListTemplateManagerImpl 
	extends AbstractListTemplateManager<TemplateListTemplate, TemplateListColumn, TemplateListCriteria>
	implements ListTemplateManager{

	@Resource
	TemplateGroupManager tmplGroupManager;
	
	public ListTemplateManagerImpl(@Autowired ListTemplateDao dao, @Autowired ModuleCachableMetaSupportor metaSupportor) {
		super(dao, metaSupportor);
	}

	@Override
	protected void whenCriteriaFieldNull(TemplateListCriteria criteria,
			PreparedToListTemplate<TemplateListColumn, TemplateListCriteria> prepareToCache) {
		if(criteria.getCompositeId() != null) {
			criteria.setComposite(prepareToCache.getReferData().getCompositeMap().get(criteria.getCompositeId()));
		}
	}
	
	@Override
	protected TemplateListTemplate createCachablePojo() {
		return new TemplateListTemplate();
	}
	
	@Override
	protected void updateCriteria(TemplateListCriteria originCriteria, TemplateListCriteria criteria) {
		originCriteria.setCompositeId(criteria.getCompositeId());
	}


}
