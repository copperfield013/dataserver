package cn.sowell.dataserver.model.tmpl.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

public interface TemplateService {

	
	/**
	 * 根据模板id获得列表模板对象
	 * @param tmplId
	 * @return
	 */
	TemplateListTemplate getListTemplate(long tmplId);
	
	
	/**
	 * 移除列表模板
	 * @param user
	 * @param tmplId
	 */
	void removeTemplate(UserIdentifier user, Long tmplId, String tmplType);
	
	/**
	 * 创建或者更新详情模板
	 * @param data
	 */
	public <T extends AbstractTemplate> Long mergeTemplate(T template);

	/**
	 * 
	 * @param module
	 * @param user
	 * @param pageInfo
	 * @param loadDetail
	 * @return
	 */
	List<TemplateDetailTemplate> getAllDetailTemplateList(String module,
			UserIdentifier user,
			PageInfo pageInfo,
			boolean loadDetail);

	/**
	 * 根据模板id获得模板对象。该方法适用于在不知道模板的类型的前提下使用。
	 * 如果已经知道模板的类型，请使用对应的方法{@link #getDetailTemplate(long)}或{@link #getListTemplate(long)}
	 * @param tmplId
	 * @return
	 */
	AbstractTemplate getTemplate(long tmplId, String tmplType);
	
	List<TemplateListTemplate> queryLtmplList(String module, UserIdentifier user);

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

}
