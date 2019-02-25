package cn.sowell.dataserver.model.tmpl.dao;

import java.util.List;
import java.util.Map;

import cn.sowell.dataserver.model.cachable.dao.CachableDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailArrayItemCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailArrayItemFilter;

public interface ArrayItemFilterDao extends CachableDao<TemplateDetailArrayItemFilter>{

	Map<Long, List<TemplateDetailArrayItemCriteria>> queryAllCriterias();

	List<TemplateDetailArrayItemCriteria> queryCriterias(Long id);

}
