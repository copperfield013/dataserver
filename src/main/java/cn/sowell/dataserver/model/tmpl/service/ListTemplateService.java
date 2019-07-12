package cn.sowell.dataserver.model.tmpl.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sowell.dataserver.model.tmpl.duplicator.ModuleTemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

public interface ListTemplateService extends OpenTemplateService<TemplateListTemplate>, RelateToTemplateGroup, ModuleTemplateDuplicator {

	Map<String, List<TemplateListTemplate>> queryByModuleNames(Set<String> moduleNames);
	


}
