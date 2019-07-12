package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.sowell.dataserver.model.tmpl.duplicator.impl.DetailTemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.manager.DetailTemplateManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.service.DetailTemplateService;
import cn.sowell.dataserver.model.tmpl.service.TemplateGroupService;

@Service
public class DetailTemplateServiceImpl extends AbstractRelateToGroupService<TemplateDetailTemplate, DetailTemplateManager> implements DetailTemplateService{

	@Resource
	DetailTemplateDuplicator duplicator;
	
	@Autowired
	protected DetailTemplateServiceImpl(
			@Autowired DetailTemplateManager manager, 
			@Autowired TemplateGroupService tmplGroupService
			) {
		super(manager, tmplGroupService);
	}

	@Override
	public void switchAllRelatedGroups(Long tmplId, Long targetTmplId) {
		getTemplateGroupService().updateAllGroupsDetailTemplate(tmplId, targetTmplId);
	}

	@Override
	public Long copy(Long dtmplId, String targetModuleName) {
		Long newTmplId = duplicator.copy(dtmplId, targetModuleName);
		return newTmplId;
	}

	@Override
	protected boolean isRelatedGroup(Long tmplId, TemplateGroup tmplGroup) {
		return tmplId.equals(tmplGroup.getDetailTemplateId());
	}

	@Override
	public TemplateDetailFieldGroup getFieldGroup(Long groupId) {
		return getManager().getFieldGroup(groupId);
	}

	@Override
	public Map<String, List<TemplateDetailTemplate>> queryByModuleNames(Set<String> moduleNames) {
		Map<String, List<TemplateDetailTemplate>> map = new HashMap<String, List<TemplateDetailTemplate>>();
		for (String moduleName : moduleNames) {
			map.put(moduleName, getManager().queryByModule(moduleName));
		}
		return map;
	}
	

}
