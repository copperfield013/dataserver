package cn.sowell.dataserver.model.tmpl.service;

import java.util.Map;
import java.util.Set;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatList;

public interface StatListTemplateService extends OpenTemplateService<TemplateStatList>{

	Map<Long, TemplateStatList> getTemplateMap(Set<Long> ltmplIdSet);
	
}
