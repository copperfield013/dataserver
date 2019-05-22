package cn.sowell.dataserver.model.tmpl.manager;

import java.util.Map;
import java.util.Set;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatList;

public interface StatListTemplateManager extends ModuleCachableManager<TemplateStatList>{

	Map<Long, TemplateStatList> getTemplateMap(Set<Long> ltmplIdSet);

}
