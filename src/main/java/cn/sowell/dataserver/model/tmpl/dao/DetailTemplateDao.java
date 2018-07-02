package cn.sowell.dataserver.model.tmpl.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;

public interface DetailTemplateDao {
	/**
	 * 根据模板id获得所有字段组（经过排序）
	 * @param tmplId
	 * @return
	 */
	List<TemplateDetailFieldGroup> getTemplateGroups(Long dtmplId);

	List<TemplateDetailTemplate> getTemplateList(String module, UserIdentifier user, PageInfo pageInfo);

	
	/**
	 * 根据字段组的id集合获得对应的所有字段（经过排序）
	 * @param groupIdSet
	 * @return
	 */
	Map<Long, List<TemplateDetailField>> getTemplateFieldsMap(
			Set<Long> groupIdSet);

	TemplateDetailTemplate getDetailTemplateByGroupId(Long templateGroupId);
}
