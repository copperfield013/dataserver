package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_tmpl_list_template")
public class TemplateListTempalte extends AbstractTemplate{
	
	@Column(name="c_def_pagesize")
	private Integer defaultPageSize;
	
	@Column(name="def_order_field_id")
	private Long defaultOrderFieldId;
	
	@Column(name="c_def_order_dir")
	private String defaultOrderDirection;
	
	
	@Column(name="c_unmodifiable")
	private Integer unmodifiable;
	
	
	@Transient
	private Set<TemplateListColumn> columns = new LinkedHashSet<TemplateListColumn>();
	
	@Transient
	private Set<TemplateListCriteria> criterias = new LinkedHashSet<TemplateListCriteria>();
	public Integer getUnmodifiable() {
		return unmodifiable;
	}
	public void setUnmodifiable(Integer unmodifiable) {
		this.unmodifiable = unmodifiable;
	}
	public Set<TemplateListColumn> getColumns() {
		return columns;
	}
	public void setColumns(Set<TemplateListColumn> columns) {
		this.columns = columns;
	}
	public Set<TemplateListCriteria> getCriterias() {
		return criterias;
	}
	public void setCriterias(Set<TemplateListCriteria> criterias) {
		this.criterias = criterias;
	}
	public Integer getDefaultPageSize() {
		return defaultPageSize;
	}
	public void setDefaultPageSize(Integer defaultPageSize) {
		this.defaultPageSize = defaultPageSize;
	}
	public Long getDefaultOrderFieldId() {
		return defaultOrderFieldId;
	}
	public void setDefaultOrderFieldId(Long defaultOrderFieldId) {
		this.defaultOrderFieldId = defaultOrderFieldId;
	}
	public String getDefaultOrderDirection() {
		return defaultOrderDirection;
	}
	public void setDefaultOrderDirection(String defaultOrderDirection) {
		this.defaultOrderDirection = defaultOrderDirection;
	}
	
}
