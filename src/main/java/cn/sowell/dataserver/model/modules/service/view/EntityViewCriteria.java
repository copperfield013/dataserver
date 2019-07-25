package cn.sowell.dataserver.model.modules.service.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.CommonPageInfo;
import cn.sowell.copframe.dto.page.PageInfo;

public class EntityViewCriteria {
	private String module;
	private String relationName;
	private Set<CriteriaEntry> criteriaEntries = new LinkedHashSet<>();
	
	private List<Long> viewFieldIds = new ArrayList<>();
	
	private Set<String> existCodes ;
	
	private PageInfo pageInfo = new CommonPageInfo();
	private UserIdentifier user;
	
	private Map<Long, String> templateCriteriaMap = new HashMap<>();
	
	public static class CriteriaEntry{
		private Integer fieldId;
		private Integer compositeId; 
		private String comparator;
		private String value;
		private String relationLabel;
		public String getComparator() {
			return comparator;
		}
		public void setComparator(String comparator) {
			this.comparator = comparator;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getRelationLabel() {
			return relationLabel;
		}
		public void setRelationLabel(String relationLabel) {
			this.relationLabel = relationLabel;
		}
		public Integer getFieldId() {
			return fieldId;
		}
		public void setFieldId(Integer fieldId) {
			this.fieldId = fieldId;
		}
		public Integer getCompositeId() {
			return compositeId;
		}
		public void setCompositeId(Integer compositeId) {
			this.compositeId = compositeId;
		}
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}


	public Set<CriteriaEntry> getCriteriaEntries() {
		return criteriaEntries;
	}


	public void setCriteriaEntries(Set<CriteriaEntry> criteriaEntries) {
		this.criteriaEntries = criteriaEntries;
	}


	public String getModule() {
		return module;
	}


	public void setModule(String module) {
		this.module = module;
	}

	public List<Long> getViewFieldIds() {
		return viewFieldIds;
	}

	public void setViewFieldIds(List<Long> viewFieldIds) {
		this.viewFieldIds = viewFieldIds;
	}

	public UserIdentifier getUser() {
		return this.user;
	}

	public void setUser(UserIdentifier user) {
		this.user = user;
	}

	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public Set<String> getExistCodes() {
		return existCodes;
	}

	public void setExistCodes(Set<String> existCodes) {
		this.existCodes = existCodes;
	}

	public Map<Long, String> getTemplateCriteriaMap() {
		return templateCriteriaMap;
	}

	public void setTemplateCriteriaMap(Map<Long, String> templateCriteriaMap) {
		this.templateCriteriaMap = templateCriteriaMap;
	}

	
	


}
