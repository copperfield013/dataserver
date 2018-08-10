package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_list_template")
public class TemplateListTemplate extends AbstractTemplate{
	
	@Column(name="c_def_pagesize")
	private Integer defaultPageSize;
	
	@Column(name="def_order_field_id")
	private Long defaultOrderFieldId;
	
	@Column(name="c_def_order_dir")
	private String defaultOrderDirection;
	
	
	@Column(name="c_unmodifiable")
	private Integer unmodifiable;
	
	
	@Transient
	private List<TemplateListColumn> columns = new ArrayList<TemplateListColumn>();
	
	@Transient
	private Set<TemplateListCriteria> criterias = new LinkedHashSet<TemplateListCriteria>();
	public Integer getUnmodifiable() {
		return unmodifiable;
	}
	public void setUnmodifiable(Integer unmodifiable) {
		this.unmodifiable = unmodifiable;
	}
	public List<TemplateListColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<TemplateListColumn> columns) {
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
