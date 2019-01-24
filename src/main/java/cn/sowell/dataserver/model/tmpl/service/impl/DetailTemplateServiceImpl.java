package cn.sowell.dataserver.model.tmpl.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.sowell.dataserver.model.tmpl.duplicator.impl.DetailTemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.manager.DetailTemplateManager;
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

}
