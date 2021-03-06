package cn.sowell.dataserver.model.tmpl.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.sowell.dataserver.model.tmpl.manager.ActionTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.ArrayItemFilterManager;
import cn.sowell.dataserver.model.tmpl.manager.DetailTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.ListTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.SelectionTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.TemplateGroupManager;
import cn.sowell.dataserver.model.tmpl.manager.TreeTemplateManager;
import cn.sowell.dataserver.model.tmpl.service.CachableTemplateService;

@Service
public class CachableTemplateServiceImpl implements CachableTemplateService{
	
	
	
	@Resource
	TemplateGroupManager tmplGroupManager;
	
	@Resource
	ListTemplateManager ltmplManager;
	
	@Resource
	DetailTemplateManager dtmplManager;
	
	@Resource
	ActionTemplateManager atmplManager;
	
	@Resource
	SelectionTemplateManager stmplManager;
	
	@Resource
	ArrayItemFilterManager arrayItemFilterManager;
	
	@Resource
	TreeTemplateManager treeManager;
	
	@Override
	public void clearCache() {
		tmplGroupManager.clearCache();
		dtmplManager.clearCache();
		ltmplManager.clearCache();
		stmplManager.clearCache();
		atmplManager.clearCache();
		treeManager.clearCache();
		arrayItemFilterManager.clearCache();
	}
	
	@Override
	public void reloadCache() {
		dtmplManager.reloadCache();
		ltmplManager.reloadCache();
		stmplManager.reloadCache();
		atmplManager.reloadCache();
		tmplGroupManager.reloadCache();
		treeManager.reloadCache();
		arrayItemFilterManager.reloadCache();
	}
	
}
