package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_selection_template")
public class TemplateSelectionTemplate extends AbstractListTemplate<TemplateSelectionColumn, TemplateSelectionCriteria>{
	
	@Transient
	private String relationName;
	
	@Column(name="c_multiple")
	private Integer multiple;
	
	@Column(name="composite_id")
	private Integer compositeId;
	
	@Column(name="c_nonunique")
	private Integer nonunique;
	
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
	public Integer getCompositeId() {
		return compositeId;
	}
	public void setCompositeId(Integer compositeId) {
		this.compositeId = compositeId;
	}
	public Integer getMultiple() {
		return multiple;
	}
	public void setMultiple(Integer multiple) {
		this.multiple = multiple;
	}
	
}
