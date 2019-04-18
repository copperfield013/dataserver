package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="t_sa_tmpl_tree_relation_criteria")
public class TemplateTreeRelationCriteria extends SuperTemplateListCriteria{
	public static final String FILTER_MODE_LABEL = "label";
	public static final String FILTER_MODE_FIELD = "field";

	@Column(name="c_filter_mode")
	private String filterMode;
	
	@Column(name="c_filter_label")
	private String filterLabels;
	
	@Column(name="c_exclude_label")
	private Integer isExcludeLabel;

	public String getFilterLabels() {
		return filterLabels;
	}

	public void setFilterLabels(String filterLabels) {
		this.filterLabels = filterLabels;
	}

	public Integer getIsExcludeLabel() {
		return isExcludeLabel;
	}

	public void setIsExcludeLabel(Integer isExcludeLabel) {
		this.isExcludeLabel = isExcludeLabel;
	}

	public String getFilterMode() {
		return filterMode;
	}

	public void setFilterMode(String filterMode) {
		this.filterMode = filterMode;
	}
}
