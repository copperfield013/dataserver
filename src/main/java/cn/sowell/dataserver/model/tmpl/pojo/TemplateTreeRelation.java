package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_tree_relation")
public class TemplateTreeRelation{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="c_title")
	private String title;
	
	@Column(name="node_id")
	private Long nodeId;
	
	@Column(name="c_rel_name")
	private String relationName;
	
	@Column(name="c_order")
	private Integer order;
	
	@Transient
	private String relationModule;
	
	@Transient
	private List<TemplateTreeRelationCriteria> criterias;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public List<TemplateTreeRelationCriteria> getCriterias() {
		return criterias;
	}
	public void setCriterias(List<TemplateTreeRelationCriteria> criterias) {
		this.criterias = criterias;
	}
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	public String getRelationName() {
		return relationName;
	}
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	public String getRelationModule() {
		return relationModule;
	}
	public void setRelationModule(String relationModule) {
		this.relationModule = relationModule;
	}
	
}
