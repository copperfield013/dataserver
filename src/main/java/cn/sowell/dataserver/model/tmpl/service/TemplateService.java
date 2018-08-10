package cn.sowell.dataserver.model.tmpl.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;

public interface TemplateService {

	List<TemplateDetailTemplate> queryDetailTemplates(String module);

	/**
	 * 根据模板id获得列表模板对象
	 * @param tmplId
	 * @return
	 */
	TemplateListTemplate getListTemplate(long tmplId);
	
	
	/**
	 * 创建或者更新详情模板
	 * @param data
	 */
	public <T extends AbstractTemplate> Long mergeTemplate(T template);

	List<TemplateListTemplate> queryListTemplateList(String module, UserIdentifier user);

	/**
	 * 获得该模块所有的模板组合
	 * @param module
	 * @return
	 */
	List<TemplateGroup> queryTemplateGroups(String module);

	/**
	 * 保存模板组
	 * @param group
	 * @param userIdentifier 
	 */
	void saveGroup(TemplateGroup group, UserIdentifier userIdentifier);

	TemplateGroup getTemplateGroup(Long groupId);

	/**
	 * 移除模板组合
	 * @param groupId
	 */
	void removeTemplateGroup(Long groupId);

	/**
	 * 根据模块和模板组的key获得对应的模板
	 * @param module
	 * @param templateGroupKey
	 * @return
	 */
	TemplateGroup getTemplateGroup(String module, String templateGroupKey);

	Map<String, List<TemplateGroup>> queryTemplateGroups(Set<String> moduleNames);


	TemplateDetailTemplate getDetailTemplate(long tmplId);


	TemplateDetailTemplate getDetailTemplateByGroupId(Long templateGroupId);


	void removeListTemplate(Long ltmplId);


	void removeDetailTemplate(Long dtmplId);

	void clearCache();

	void loadCache();

	/**
	 * 获得各个详情模板关联的所有模板组合
	 * @param dtmplIds
	 * @return
	 */
	Map<Long, Set<TemplateGroup>> getDetailTemplateRelatedGroupsMap(Set<Long> dtmplIds);

	/**
	 * 获得各个列表模板关联的所有模板组合
	 * @param ltmplIds
	 * @return
	 */
	Map<Long, Set<TemplateGroup>> getListTemplateRelatedGroupsMap(Set<Long> ltmplIds);

	Set<TemplateGroup> getListTemplateRelatedGroups(Long ltmplId);

	Set<TemplateGroup> getDetailTemplateRelatedGroups(Long dtmplId);

	void switchAllGroupsDetailTemplate(Long dtmplId, Long targetDtmplId);

	void switchAllGroupsListTemplate(Long ltmplId, Long targetLtmplId);

	TemplateSelectionTemplate getSelectionTemplate(Long stmplId);

}
