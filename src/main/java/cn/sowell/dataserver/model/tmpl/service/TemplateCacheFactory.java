package cn.sowell.dataserver.model.tmpl.service;

import java.util.List;

import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

public interface TemplateCacheFactory {

	/**
	 * 查询所有列表模板
	 * @param userId
	 * @param serializable 
	 * @param pageInfo
	 * @return
	 */
	List<TemplateListTemplate> queryListTemplateList(String module, Long id, PageInfo pageInfo);
	
	
	
	
}
