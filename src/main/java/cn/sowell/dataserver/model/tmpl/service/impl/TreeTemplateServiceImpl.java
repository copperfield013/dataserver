package cn.sowell.dataserver.model.tmpl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.sowell.dataserver.model.tmpl.manager.TreeTemplateManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeTemplate;
import cn.sowell.dataserver.model.tmpl.service.TreeTemplateService;

@Service
public class TreeTemplateServiceImpl
	extends AbstractTemplateService<TemplateTreeTemplate, TreeTemplateManager>
	implements TreeTemplateService{

	@Autowired
	protected TreeTemplateServiceImpl(@Autowired TreeTemplateManager manager) {
		super(manager);
	}

	@Override
	public Long copy(Long tmplId, String targetReference) {
		// TODO Auto-generated method stub
		return null;
	}

}
