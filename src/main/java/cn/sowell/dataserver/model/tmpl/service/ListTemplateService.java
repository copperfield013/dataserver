package cn.sowell.dataserver.model.tmpl.service;

import cn.sowell.dataserver.model.tmpl.duplicator.ModuleTemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

public interface ListTemplateService extends OpenTemplateService<TemplateListTemplate>, RelateToTemplateGroup, ModuleTemplateDuplicator {
	


}
