package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_sa_tmpl_dictfilter_criteria")
public class TemplateDictionaryFilterCriteria {
	@Id
	private Long id;
	
	@Column(name="filter_id")
	private Long filterId;
	
	@Column(name="field_id")
	private Long fieldId;
	
	@Column(name="composite_id")
	private Long compositeId;
	
	@Column(name="c_field_type")
	private String fieldType;
	
	@Column(name="c_field_query")
	private String fieldQuery;
	
	@Column(name="c_composite_query")
	private String compositeQuery;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFieldId() {
		return fieldId;
	}
	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}
	public Long getCompositeId() {
		return compositeId;
	}
	public void setCompositeId(Long compositeId) {
		this.compositeId = compositeId;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getFieldQuery() {
		return fieldQuery;
	}
	public void setFieldQuery(String fieldQuery) {
		this.fieldQuery = fieldQuery;
	}
	public String getCompositeQuery() {
		return compositeQuery;
	}
	public void setCompositeQuery(String compositeQuery) {
		this.compositeQuery = compositeQuery;
	}
	public Long getFilterId() {
		return filterId;
	}
	public void setFilterId(Long filterId) {
		this.filterId = filterId;
	}
}
