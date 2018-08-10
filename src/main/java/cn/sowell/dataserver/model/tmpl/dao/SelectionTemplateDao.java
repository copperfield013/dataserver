package cn.sowell.dataserver.model.tmpl.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;

public interface SelectionTemplateDao {

	List<TemplateSelectionTemplate> queryTemplates();

	Map<Long, List<TemplateSelectionColumn>> queryColumnsMap();

	Map<Long, Set<TemplateSelectionCriteria>> queryCriteriasMap();

	List<TemplateSelectionColumn> getColumnsByTmplId(Long tmplId);

	Set<TemplateSelectionCriteria> getCriteriaByTmplId(Long tmplId);

}
