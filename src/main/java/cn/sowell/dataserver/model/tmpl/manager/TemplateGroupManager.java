package cn.sowell.dataserver.model.tmpl.manager;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupAction;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupDictionaryFilter;

public interface TemplateGroupManager extends ModuleCachableManager<TemplateGroup>{

	TemplateGroupDictionaryFilter getDictionaryFilter(long tmplFilterId);

	void bindTemplateGroupReloadEvent(Consumer<TemplateGroup> consumer);

	void updateListTemplateRelatedGroups(Long ltmplId);

	void updateDetailTemplateRelatedGroups(Long dtmplId);
	
	void updateActionTemplateRelatedGroups(Long atmplId);
	
	Set<TemplateGroup> getListTemplateRelatedGroups(Long ltmplId);

	Set<TemplateGroup> getDetailTemplateRelatedGroups(Long dtmplId);

	Stream<TemplateGroup> allStream();

	void updateAllGroupsListTemplate(Long listTemplateId, Long targetListTemplateId);

	void updateAllGroupsDetailTemplate(Long detailTemplateId, Long targetDetailTemplateId);

	TemplateGroupAction getGroupAction(Long tmplActionId);

	Set<TemplateGroup> getActionTemplateRelatedGroups(Long atmplId);

	


}
