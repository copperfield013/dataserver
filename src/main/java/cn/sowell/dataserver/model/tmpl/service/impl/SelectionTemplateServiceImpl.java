package cn.sowell.dataserver.model.tmpl.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.tmpl.duplicator.impl.SelectionTemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.manager.SelectionTemplateManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;
import cn.sowell.dataserver.model.tmpl.service.SelectionTemplateService;

@Service
public class SelectionTemplateServiceImpl extends AbstractTemplateService<TemplateSelectionTemplate, SelectionTemplateManager> implements SelectionTemplateService{

	@Resource
	SelectionTemplateDuplicator duplicator;
	
	
	@Autowired
	public SelectionTemplateServiceImpl(
			@Autowired SelectionTemplateManager manager) {
		super(manager);
	}

	@Override
	public Long copy(Long stmplId, DictionaryComposite targetComposite) {
		Long newTmplId = duplicator.copy(stmplId, targetComposite);
		return newTmplId;
	}

}
