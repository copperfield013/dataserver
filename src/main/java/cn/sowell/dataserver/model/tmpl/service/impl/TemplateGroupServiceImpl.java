package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.tmpl.duplicator.impl.TemplateGroupDuplicator;
import cn.sowell.dataserver.model.tmpl.manager.TemplateGroupManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupAction;
import cn.sowell.dataserver.model.tmpl.service.TemplateGroupService;

@Service
public class TemplateGroupServiceImpl extends AbstractTemplateService<TemplateGroup, TemplateGroupManager> implements TemplateGroupService{

	@Resource
	TemplateGroupDuplicator duplicator;
	
	public TemplateGroupServiceImpl(
			@Autowired TemplateGroupManager manager) {
		super(manager);
	}

	@Override
	public Stream<TemplateGroup> allStream() {
		return getManager().allStream();
	}

	@Override
	public void updateAllGroupsListTemplate(Long listTemplateId, Long targetListTemplateId) {
		getManager().updateAllGroupsListTemplate(listTemplateId, targetListTemplateId);
	}

	@Override
	public void updateAllGroupsDetailTemplate(Long detailTemplateId, Long targetDetailTemplateId) {
		getManager().updateAllGroupsDetailTemplate(detailTemplateId, targetDetailTemplateId);
	}
	
	@Override
	public TemplateGroupAction getTempateGroupAction(Long tmplActionId) {
		return getManager().getGroupAction(tmplActionId);
	}
	
	@Override
	public Map<String, List<TemplateGroup>> queryModuleGroups(Set<String> moduleNames) {
		Set<TemplateGroup> set = allStream().filter(group->moduleNames.contains(group.getModule())).collect(Collectors.toSet());
		return CollectionUtils.toListMap(set, group->group.getModule());
	}
	
	@Override
	public void bindTemplateGroupReloadEvent(Consumer<TemplateGroup> consumer) {
		getManager().bindTemplateGroupReloadEvent(consumer);
	}

	@Override
	public Long copy(Long tmplGroupId, String targetModuleName) {
		Long newTmplId = duplicator.copy(tmplGroupId, targetModuleName);
		return newTmplId;
	}
	
}
