package cn.sowell.dataserver.model.tmpl.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sowell.dataserver.model.cachable.dao.CachableDao;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractDetailTemplate;

public interface OpenDetailTemplateDao<DT extends AbstractDetailTemplate<GT, FT>, GT extends AbstractDetailFieldGroup<FT>, FT extends AbstractDetailField>
	extends CachableDao<DT>{

	List<GT> queryFieldGroups();

	List<FT> queryTemplateFields();

	/**
	 * 根据模板id获得所有字段组（经过排序）
	 * @param tmplId
	 * @return
	 */
	List<GT> getTemplateGroups(Long tmplId);

	/**
	 * 根据字段组的id集合获得对应的所有字段（经过排序）
	 * @param groupIdSet
	 * @return
	 */
	Map<Long, List<FT>> getTemplateFieldsMap(Set<Long> groupIds);
}
