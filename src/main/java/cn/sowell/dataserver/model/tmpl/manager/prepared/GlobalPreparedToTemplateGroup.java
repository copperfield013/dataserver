package cn.sowell.dataserver.model.tmpl.manager.prepared;

import java.util.List;
import java.util.Map;

import cn.sowell.dataserver.model.cachable.prepare.PreparedToCache;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupAction;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupDictionaryFilter;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupPremise;

public class GlobalPreparedToTemplateGroup extends PreparedToCache{
	Map<Long, List<TemplateGroupPremise>> premisesMap;
	Map<Long, List<TemplateGroupAction>> groupActionsMap;
	Map<Long, TemplateGroupDictionaryFilter> templateFilterMap;
	
	public static class PreparedToTemplateGroup extends PreparedToCache{
		private List<TemplateGroupPremise> premises;
		private List<TemplateGroupAction> actions;
		private TemplateGroupDictionaryFilter importDictionaryFilter;
		private TemplateGroupDictionaryFilter exportDictionaryFilter;
		public List<TemplateGroupPremise> getPremises() {
			return premises;
		}
		public void setPremises(List<TemplateGroupPremise> premises) {
			this.premises = premises;
		}
		public List<TemplateGroupAction> getActions() {
			return actions;
		}
		public void setActions(List<TemplateGroupAction> actions) {
			this.actions = actions;
		}
		public TemplateGroupDictionaryFilter getImportDictionaryFilter() {
			return importDictionaryFilter;
		}
		public void setImportDictionaryFilter(TemplateGroupDictionaryFilter importDictionaryFilter) {
			this.importDictionaryFilter = importDictionaryFilter;
		}
		public TemplateGroupDictionaryFilter getExportDictionaryFilter() {
			return exportDictionaryFilter;
		}
		public void setExportDictionaryFilter(TemplateGroupDictionaryFilter exportDictionaryFilter) {
			this.exportDictionaryFilter = exportDictionaryFilter;
		}
	}

	public Map<Long, List<TemplateGroupPremise>> getPremisesMap() {
		return premisesMap;
	}

	public void setPremisesMap(Map<Long, List<TemplateGroupPremise>> premisesMap) {
		this.premisesMap = premisesMap;
	}

	public Map<Long, List<TemplateGroupAction>> getGroupActionsMap() {
		return groupActionsMap;
	}

	public void setGroupActionsMap(Map<Long, List<TemplateGroupAction>> groupActionsMap) {
		this.groupActionsMap = groupActionsMap;
	}

	public Map<Long, TemplateGroupDictionaryFilter> getTemplateFilterMap() {
		return templateFilterMap;
	}

	public void setTemplateFilterMap(Map<Long, TemplateGroupDictionaryFilter> templateFilterMap) {
		this.templateFilterMap = templateFilterMap;
	}

}
