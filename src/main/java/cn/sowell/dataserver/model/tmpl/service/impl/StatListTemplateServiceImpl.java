package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.sowell.dataserver.model.tmpl.manager.StatListTemplateManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatList;
import cn.sowell.dataserver.model.tmpl.service.StatListTemplateService;

@Service
public class StatListTemplateServiceImpl 
	extends AbstractTemplateService<TemplateStatList, StatListTemplateManager> 
	implements StatListTemplateService{

	@Autowired
	public StatListTemplateServiceImpl(@Autowired StatListTemplateManager manager) {
		super(manager);
	}

	@Override
	public Map<Long, TemplateStatList> getTemplateMap(Set<Long> ltmplIdSet) {
		return getManager().getTemplateMap(ltmplIdSet);
	}

}
