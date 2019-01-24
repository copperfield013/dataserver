package cn.sowell.dataserver.model.tmpl.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import cn.sowell.dataserver.model.tmpl.duplicator.ModuleTemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupAction;

public interface TemplateGroupService extends OpenTemplateService<TemplateGroup>, ModuleTemplateDuplicator{

	Stream<TemplateGroup> allStream();

	void updateAllGroupsListTemplate(Long listTemplateId, Long targetListTemplateId);

	void updateAllGroupsDetailTemplate(Long tmplId, Long targetTmplId);

	TemplateGroupAction getTempateGroupAction(Long actionId);

	Map<String, List<TemplateGroup>> queryModuleGroups(Set<String> moduleNames);

	void bindTemplateGroupReloadEvent(Consumer<TemplateGroup> consumer);

}
