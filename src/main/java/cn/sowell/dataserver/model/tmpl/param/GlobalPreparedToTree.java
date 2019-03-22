package cn.sowell.dataserver.model.tmpl.param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sowell.dataserver.model.cachable.prepare.PreparedToCache;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeNode;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeRelation;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeRelationCriteria;

public class GlobalPreparedToTree extends PreparedToCache{
	private Map<Long, List<TemplateTreeNode>> nodeListMap = new LinkedHashMap<Long, List<TemplateTreeNode>>();
	private Map<Long, List<TemplateTreeRelation>> nodeRelationsMap  = new LinkedHashMap<>();
	private Map<Long, List<TemplateTreeRelationCriteria>> relationCriteriasMap  = new LinkedHashMap<>();
	private Map<String, Map<Long, DictionaryField>> moduleFieldsMap = new HashMap<>();
	private Map<String, Map<String, String>> moduleRelationModuleMap = new HashMap<>();
	private Set<String> moduleFlagSet = new HashSet<>();
	
	public static class PreparedToTree extends PreparedToCache{

		private List<TemplateTreeNode> nodeList = new ArrayList<>();
		private Map<Long, List<TemplateTreeRelation>> nodeRelationsMap = new LinkedHashMap<>();
		private Map<Long, List<TemplateTreeRelationCriteria>> relationCriteriasMap = new LinkedHashMap<>();
		private Map<String, Map<Long, DictionaryField>> moduleFieldsMap = new HashMap<>();
		private Map<String, Map<String, String>> moduleRelationModuleMap = new HashMap<>();
		public List<TemplateTreeNode> getNodeList() {
			return nodeList;
		}
		public void setNodeList(List<TemplateTreeNode> nodeList) {
			this.nodeList = nodeList;
		}
		public Map<Long, List<TemplateTreeRelation>> getNodeRelationsMap() {
			return nodeRelationsMap;
		}
		public void setNodeRelationsMap(Map<Long, List<TemplateTreeRelation>> nodeRelationsMap) {
			this.nodeRelationsMap = nodeRelationsMap;
		}
		public Map<Long, List<TemplateTreeRelationCriteria>> getRelationCriteriasMap() {
			return relationCriteriasMap;
		}
		public void setRelationCriteriasMap(Map<Long, List<TemplateTreeRelationCriteria>> relationCriteriasMap) {
			this.relationCriteriasMap = relationCriteriasMap;
		}
		public Map<String, Map<Long, DictionaryField>> getModuleFieldsMap() {
			return moduleFieldsMap;
		}
		public void setModuleFieldsMap(Map<String, Map<Long, DictionaryField>> moduleFieldsMap) {
			this.moduleFieldsMap = moduleFieldsMap;
		}
		public Map<String, Map<String, String>> getModuleRelationModuleMap() {
			return moduleRelationModuleMap;
		}
		public void setModuleRelationModuleMap(Map<String, Map<String, String>> moduleRelationModuleMap) {
			this.moduleRelationModuleMap = moduleRelationModuleMap;
		}
		public String getRelationModuleName(String moduleName, String relationName) {
			Map<String, String> relationModuleMap = this.moduleRelationModuleMap.get(moduleName);
			if(relationModuleMap != null) {
				return relationModuleMap.get(relationName);
			}
			return null;
		}
	}


	public Map<Long, List<TemplateTreeNode>> getNodeListMap() {
		return nodeListMap;
	}


	public void setNodeListMap(Map<Long, List<TemplateTreeNode>> nodeListMap) {
		this.nodeListMap = nodeListMap;
	}


	public Map<Long, List<TemplateTreeRelation>> getNodeRelationsMap() {
		return nodeRelationsMap;
	}


	public void setNodeRelationsMap(Map<Long, List<TemplateTreeRelation>> nodeRelationsMap) {
		this.nodeRelationsMap = nodeRelationsMap;
	}


	public Map<Long, List<TemplateTreeRelationCriteria>> getRelationCriteriasMap() {
		return relationCriteriasMap;
	}


	public void setRelationCriteriasMap(Map<Long, List<TemplateTreeRelationCriteria>> relationCriteriasMap) {
		this.relationCriteriasMap = relationCriteriasMap;
	}


	public Map<String, Map<Long, DictionaryField>> getModuleFieldsMap() {
		return moduleFieldsMap;
	}


	public void setModuleFieldsMap(Map<String, Map<Long, DictionaryField>> moduleFieldsMap) {
		this.moduleFieldsMap = moduleFieldsMap;
	}


	public Map<String, Map<String, String>> getModuleRelationModuleMap() {
		return moduleRelationModuleMap;
	}


	public void setModuleRelationModuleMap(Map<String, Map<String, String>> moduleRelationModuleMap) {
		this.moduleRelationModuleMap = moduleRelationModuleMap;
	}


	public Set<String> getModuleFlagSet() {
		return moduleFlagSet;
	}


	public void setModuleFlagSet(Set<String> moduleFlagSet) {
		this.moduleFlagSet = moduleFlagSet;
	}



}
