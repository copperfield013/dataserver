package cn.sowell.dataserver.model.modules.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.sowell.copframe.dto.page.CommonPageInfo;
import cn.sowell.copframe.dto.page.PageInfo;

public class EntityViewCriteria {
	private String module;
	private Set<CriteriaEntry> criteriaEntries = new HashSet<>();
	
	private List<Long> viewFieldIds = new ArrayList<>();
	
	private PageInfo pageInfo = new CommonPageInfo();
	
	public static class CriteriaEntry{
		private Long fieldId;
		private String comparator;
		private String value;
		private String relationLabel;
		private String fieldName;
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
		public Long getFieldId() {
			return fieldId;
		}
		public void setFieldId(Long fieldId) {
			this.fieldId = fieldId;
		}
		String getFieldName() {
			return fieldName;
		}
		void setFieldName(String fieldName) {
			this.fieldName = fieldName;
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
	


}
