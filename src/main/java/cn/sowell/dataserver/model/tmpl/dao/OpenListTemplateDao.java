package cn.sowell.dataserver.model.tmpl.dao;

import java.util.List;
import java.util.Map;

import cn.sowell.dataserver.model.cachable.dao.CachableDao;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListColumn;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListTemplate;

public interface OpenListTemplateDao<LT extends AbstractListTemplate<COL, CRI>, COL extends AbstractListColumn, CRI extends AbstractListCriteria> 
	extends CachableDao<LT> {
	
	public List<COL> getColumnsByTmplId(Long ltmplId); 
	
	public List<CRI> getCriteriaByTmplId(Long ltmplId);
	
	public Map<Long, List<COL>> queryColumnsMap();
	
	public Map<Long, List<CRI>> queryCriteriasMap();

	public Class<COL> getListColumnClass();

	public Class<CRI> getListCriteriaClass();
}
