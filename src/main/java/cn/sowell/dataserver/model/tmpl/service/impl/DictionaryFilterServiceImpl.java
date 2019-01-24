package cn.sowell.dataserver.model.tmpl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.sowell.dataserver.model.tmpl.manager.DictionaryFilterManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDictionaryFilter;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupDictionaryFilter;
import cn.sowell.dataserver.model.tmpl.service.DictionaryFilterService;
import cn.sowell.dataserver.model.tmpl.service.TemplateGroupService;

@Service
public class DictionaryFilterServiceImpl extends AbstractRelateToGroupService<TemplateDictionaryFilter, DictionaryFilterManager> implements DictionaryFilterService{

	@Autowired
	public DictionaryFilterServiceImpl(@Autowired DictionaryFilterManager manager, @Autowired TemplateGroupService tmplGroupService) {
		super(manager, tmplGroupService);
	}

	@Override
	public void switchAllRelatedGroups(Long tmplId, Long targetTmpId) {
		// TODO Auto-generated method stubl
	}

	@Override
	protected boolean isRelatedGroup(Long tmplId, TemplateGroup tmplGroup) {
		TemplateGroupDictionaryFilter impFilter = tmplGroup.getImportDictionaryFilter();
		if(impFilter != null && tmplId.equals(impFilter.getFilterId())) {
			return true;
		}
		TemplateGroupDictionaryFilter expFilter = tmplGroup.getExportDictionaryFilter();
		if(expFilter != null && tmplId.equals(expFilter.getFilterId())) {
			return true;
		}
		return false;
	}

}
