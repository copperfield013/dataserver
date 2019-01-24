package cn.sowell.dataserver.model.tmpl.service;

import java.util.List;

import cn.sowell.dataserver.model.tmpl.pojo.Cachable;

public interface OpenTemplateService<T extends Cachable> {

	/**
	 * 查询模块下的所有模板
	 * @param moduleName
	 * @param user
	 * @return
	 */
	List<T> queryAll(String moduleName);

	/**
	 * 根据id获得模板
	 * @param ltmplId
	 * @return
	 */
	T getTemplate(Long tmplId);

	/**
	 * 移除模板
	 * @param ltmplId
	 */
	void remove(Long tmplId);

	/**
	 * 创建或者修改模板
	 * @param tmpl
	 * @return 
	 */
	Long merge(T tmpl);
}