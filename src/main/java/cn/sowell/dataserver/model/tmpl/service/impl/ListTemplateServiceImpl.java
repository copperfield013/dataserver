package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.sowell.dataserver.model.tmpl.duplicator.impl.ListTemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.manager.ListTemplateManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;
import cn.sowell.dataserver.model.tmpl.service.ListTemplateService;
import cn.sowell.dataserver.model.tmpl.service.TemplateGroupService;

@Service
public class ListTemplateServiceImpl extends AbstractRelateToGroupService<TemplateListTemplate, ListTemplateManager> implements ListTemplateService{

	@Resource
	ListTemplateDuplicator duplicator;
	
	@Autowired
	protected ListTemplateServiceImpl(
			@Autowired ListTemplateManager manager, 
			@Autowired TemplateGroupService tmplGroupService) {
		super(manager, tmplGroupService);
	}

	@Override
	public void switchAllRelatedGroups(Long tmplId, Long targetTmplId) {
		getTemplateGroupService().updateAllGroupsListTemplate(tmplId, targetTmplId);
	}

	@Override
	public Long copy(Long tmplId, String targetModuleName) {
		Long newTmplId = duplicator.copy(tmplId, targetModuleName);
		return newTmplId;
	}
	
	@Override
	protected boolean isRelatedGroup(Long tmplId, TemplateGroup tmplGroup) {
		return tmplId.equals(tmplGroup.getListTemplateId());
	}

	@Override
	public Map<String, List<TemplateListTemplate>> queryByModuleNames(Set<String> moduleNames) {
		Map<String, List<TemplateListTemplate>> map = new HashMap<String, List<TemplateListTemplate>>();
		for (String moduleName : moduleNames) {
			map.put(moduleName, getManager().queryByModule(moduleName));
		}
		return map;
			
	}
}
