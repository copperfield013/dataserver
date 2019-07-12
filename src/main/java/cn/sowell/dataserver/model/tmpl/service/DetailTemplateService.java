package cn.sowell.dataserver.model.tmpl.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sowell.dataserver.model.tmpl.duplicator.ModuleTemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;

public interface DetailTemplateService extends OpenTemplateService<TemplateDetailTemplate>, RelateToTemplateGroup, ModuleTemplateDuplicator{

	TemplateDetailFieldGroup getFieldGroup(Long groupId);

	Map<String, List<TemplateDetailTemplate>> queryByModuleNames(Set<String> moduleNames);

}
