package cn.sowell.dataserver.model.tmpl.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

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
	void remveTemplateGroup(Long groupId);

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


}
