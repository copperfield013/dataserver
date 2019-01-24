package cn.sowell.dataserver.model.tmpl.manager;

import java.util.List;
import java.util.Map;

import cn.sowell.dataserver.model.tmpl.pojo.FilteredDictionary;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDictionaryFilter;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;

public interface DictionaryFilterManager extends ModuleCachableManager<TemplateDictionaryFilter>{
	FilteredDictionary prefilter(long filterId);
	FilteredDictionary filter(long tmplFilterId);
	Map<Long, List<TemplateGroup>> queryRelatedGroupsMap(List<TemplateDictionaryFilter> filters);
}
