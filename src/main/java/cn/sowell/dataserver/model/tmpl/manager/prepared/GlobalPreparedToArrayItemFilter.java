package cn.sowell.dataserver.model.tmpl.manager.prepared;

import java.util.List;
import java.util.Map;

import cn.sowell.dataserver.model.cachable.prepare.PreparedToCache;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailArrayItemCriteria;

public class GlobalPreparedToArrayItemFilter extends PreparedToCache{
	private Map<Long, List<TemplateDetailArrayItemCriteria>> criteriasMap;
	
	public static class PreparedToArrayItemFilter extends PreparedToCache{
		private List<TemplateDetailArrayItemCriteria> criterias;

		public List<TemplateDetailArrayItemCriteria> getCriterias() {
			return criterias;
		}

		public void setCriterias(List<TemplateDetailArrayItemCriteria> criterias) {
			this.criterias = criterias;
		}
	}

	public Map<Long, List<TemplateDetailArrayItemCriteria>> getCriteriasMap() {
		return criteriasMap;
	}

	public void setCriteriasMap(Map<Long, List<TemplateDetailArrayItemCriteria>> criteriasMap) {
		this.criteriasMap = criteriasMap;
	}
}
