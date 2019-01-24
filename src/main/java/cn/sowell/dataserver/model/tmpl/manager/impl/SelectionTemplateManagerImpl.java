package cn.sowell.dataserver.model.tmpl.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import cn.sowell.datacenter.entityResolver.Composite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.tmpl.dao.SelectionTemplateDao;
import cn.sowell.dataserver.model.tmpl.manager.SelectionTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToListTemplate.PreparedToListTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;

@Component
public class SelectionTemplateManagerImpl 
	extends AbstractListTemplateManager<TemplateSelectionTemplate, TemplateSelectionColumn, TemplateSelectionCriteria>
	implements SelectionTemplateManager{

	@Autowired
	public SelectionTemplateManagerImpl(@Autowired SelectionTemplateDao dao, @Autowired ModuleCachableMetaSupportor metaSupportor) {
		super(dao, metaSupportor);
	}

	@Override
	protected TemplateSelectionTemplate createCachablePojo() {
		return new TemplateSelectionTemplate();
	}
	
	@Override
	protected void beforeHandleCache(TemplateSelectionTemplate latest,
			PreparedToListTemplate<TemplateSelectionColumn, TemplateSelectionCriteria> prepareToCache) {
		Assert.notNull(latest.getCompositeId());
		DictionaryComposite composite = prepareToCache.getReferData().getCompositeMap().get(latest.getCompositeId());
		if(composite != null && Composite.RELATION_ADD_TYPE.equals(composite.getAddType())) {
			latest.setRelationName(composite.getName());
		}
	}
	
	@Override
	protected void updateTemplate(TemplateSelectionTemplate origin, TemplateSelectionTemplate template) {
		origin.setMultiple(template.getMultiple());
		origin.setNonunique(template.getNonunique());
	}

}
