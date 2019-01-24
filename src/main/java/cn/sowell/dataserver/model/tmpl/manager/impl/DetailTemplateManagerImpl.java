package cn.sowell.dataserver.model.tmpl.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.tmpl.dao.DetailTemplateDao;
import cn.sowell.dataserver.model.tmpl.manager.DetailTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.TemplateGroupManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;

@Component
public class DetailTemplateManagerImpl 
		extends AbstractDetailTemplateManager<TemplateDetailTemplate, TemplateDetailFieldGroup, TemplateDetailField, DetailTemplateDao>
		implements DetailTemplateManager {

	@Autowired
	public DetailTemplateManagerImpl(
			@Autowired DetailTemplateDao dao,
			@Autowired ModuleCachableMetaSupportor metaSupportor, 
			@Autowired DictionaryService dictService,
			@Autowired TemplateGroupManager tmplGroupManager) {
		super(dao, metaSupportor, dictService, tmplGroupManager);
	}

	@Override
	protected TemplateDetailTemplate createCachablePojo() {
		return new TemplateDetailTemplate();
	}
	
	@Override
	protected void afterReloadCache(TemplateDetailTemplate dtmpl) {
		tmplGroupManager.updateDetailTemplateRelatedGroups(dtmpl.getId());
	}
	
}
