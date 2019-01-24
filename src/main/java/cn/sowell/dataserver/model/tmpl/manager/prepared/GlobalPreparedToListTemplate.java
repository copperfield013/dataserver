package cn.sowell.dataserver.model.tmpl.manager.prepared;

import java.util.List;
import java.util.Map;

import cn.sowell.dataserver.model.cachable.prepare.PreparedToCache;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListColumn;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListCriteria;

public class GlobalPreparedToListTemplate<COL extends AbstractListColumn, CRI extends AbstractListCriteria> extends PreparedToCache{
	Map<Long, List<COL>> columnsMap;
	Map<Long, List<CRI>> criteriasMap;
	public Map<Long, List<CRI>> getCriteriasMap() {
		return criteriasMap;
	}
	public void setCriteriasMap(Map<Long, List<CRI>> criteriasMap) {
		this.criteriasMap = criteriasMap;
	}
	public Map<Long, List<COL>> getColumnsMap() {
		return columnsMap;
	}
	public void setColumnsMap(Map<Long, List<COL>> columnsMap) {
		this.columnsMap = columnsMap;
	}
	
	public static class PreparedToListTemplate<COL, CRI> extends PreparedToCache{
		List<COL> columns;
		List<CRI> criterias;
		public List<COL> getColumns() {
			return columns;
		}
		public void setColumns(List<COL> columns) {
			this.columns = columns;
		}
		public List<CRI> getCriterias() {
			return criterias;
		}
		public void setCriterias(List<CRI> criterias) {
			this.criterias = criterias;
		}
	}
	
}
