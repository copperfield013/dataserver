package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_sa_tmpl_detail_fieldgroup_treenode")
public class TemplateDetailFieldGroupTreeNode {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="fieldgroup_id")
	private Long fieldGroupId;
	
	@Column(name="node_tmpl_id")
	private Long nodeTemplateId;
	
	@Column(name="c_order")
	private Integer order;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFieldGroupId() {
		return fieldGroupId;
	}

	public void setFieldGroupId(Long fieldGroupId) {
		this.fieldGroupId = fieldGroupId;
	}

	public Long getNodeTemplateId() {
		return nodeTemplateId;
	}

	public void setNodeTemplateId(Long nodeTemplateId) {
		this.nodeTemplateId = nodeTemplateId;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	};
}
