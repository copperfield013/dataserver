package cn.sowell.dataserver.model.tmpl.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;

public interface RelateToTemplateGroup {
	/**
	 * 查询所有引用当前模板的模板组合
	 * @param ltmplIds
	 * @return
	 */
	Map<Long, List<TemplateGroup>> getRelatedGroupsMap(Set<Long> tmplIds);

	/**
	 * 获得某个模板关联的所有模板组合
	 * @param ltmplId
	 * @return
	 */
	List<TemplateGroup> getRelatedGroups(Long tmplId);
	
	/**
	 * 将关联到某个列表的所有模板组合改成关联到以targetLtmplId为列表模板
	 * @param ltmplId
	 * @param targetLtmplId
	 */
	void switchAllRelatedGroups(Long tmplId, Long targetTmplId);
	
}
