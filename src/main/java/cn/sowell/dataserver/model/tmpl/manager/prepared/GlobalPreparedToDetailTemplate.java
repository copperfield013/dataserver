package cn.sowell.dataserver.model.tmpl.manager.prepared;

import java.util.List;
import java.util.Map;

import cn.sowell.dataserver.model.cachable.prepare.PreparedToCache;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractDetailFieldGroup;

public class GlobalPreparedToDetailTemplate<GT extends AbstractDetailFieldGroup<FT>, FT extends AbstractDetailField> extends PreparedToCache{
	private Map<Long, List<GT>> tmplFieldGroupsMap;
	private Map<Long, List<FT>> groupFieldsMap ;
	public Map<Long, List<GT>> getTmplFieldGroupsMap() {
		return tmplFieldGroupsMap;
	}
	public void setTmplFieldGroupsMap(Map<Long, List<GT>> tmplFieldGroupsMap) {
		this.tmplFieldGroupsMap = tmplFieldGroupsMap;
	}
	public Map<Long, List<FT>> getGroupFieldsMap() {
		return groupFieldsMap;
	}
	public void setGroupFieldsMap(Map<Long, List<FT>> groupFieldsMap) {
		this.groupFieldsMap = groupFieldsMap;
	}
	
	public static class PreparedToDetailTemplate<GT, FT> extends PreparedToCache{
		private List<GT> fieldGroups;
		private Map<Long, List<FT>> groupFieldsMap ;
		public List<GT> getFieldGroups() {
			return fieldGroups;
		}

		public void setFieldGroups(List<GT> fieldGroups) {
			this.fieldGroups = fieldGroups;
		}

		public Map<Long, List<FT>> getGroupFieldsMap() {
			return groupFieldsMap;
		}

		public void setGroupFieldsMap(Map<Long, List<FT>> groupFieldsMap) {
			this.groupFieldsMap = groupFieldsMap;
		}
	}
}
