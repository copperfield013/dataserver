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
@Table(name="t_sa_tmpl_selection_template")
public class TemplateSelectionTemplate extends AbstractTemplate{
	
	@Transient
	private String relationName;
	
	@Column(name="c_multiple")
	private Integer multiple;
	
	@Column(name="composite_id")
	private Long compositeId;
	
	@Column(name="c_nonunique")
	private Integer nonunique;
	
	@Column(name="c_def_pagesize")
	private Integer defaultPageSize;
	
	@Column(name="def_order_field_id")
	private Long defaultOrderFieldId;
	
	@Column(name="c_def_order_dir")
	private String defaultOrderDirection;
	
	@Transient
	private List<TemplateSelectionColumn> columns = new ArrayList<TemplateSelectionColumn>();
	
	@Transient
	private Set<TemplateSelectionCriteria> criterias = new LinkedHashSet<TemplateSelectionCriteria>();
	
	
	public List<TemplateSelectionColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<TemplateSelectionColumn> columns) {
		this.columns = columns;
	}
	public Set<TemplateSelectionCriteria> getCriterias() {
		return criterias;
	}
	public void setCriterias(Set<TemplateSelectionCriteria> criterias) {
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
	public String getRelationName() {
		return relationName;
	}
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	public Integer getNonunique() {
		return nonunique;
	}
	public void setNonunique(Integer nonunique) {
		this.nonunique = nonunique;
	}
	public Long getCompositeId() {
		return compositeId;
	}
	public void setCompositeId(Long compositeId) {
		this.compositeId = compositeId;
	}
	public Integer getMultiple() {
		return multiple;
	}
	public void setMultiple(Integer multiple) {
		this.multiple = multiple;
	}
	
}
