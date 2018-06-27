package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;
import cn.sowell.dataserver.model.tmpl.service.TemplateCacheFactory;

@Service
public class TemplateCacheFactoryImpl implements TemplateCacheFactory{

	Map<String, Set<TemplateListTemplate>> ltmplCacheMap;
	
	@Override
	public List<TemplateListTemplate> queryListTemplateList(String module, Long userId, PageInfo pageInfo) {
		if(ltmplCacheMap.containsKey(module)) {
		}
		return null;
	}

}
