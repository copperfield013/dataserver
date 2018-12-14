package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_action_arrayentity")
public class TemplateActionArrayEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="tmpl_field_group_id")
	private Long tmplFieldGroupId;
	
	
	@Column(name="c_index")
	private Integer index;
	
	@Column(name="c_relation_entity_code")
	private String relationEntityCode;
	
	@Column(name="c_relation_label")
	private String relationLabel;
	
	@Transient
	private List<TemplateActionArrayEntityField> fields = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTmplFieldGroupId() {
		return tmplFieldGroupId;
	}

	public void setTmplFieldGroupId(Long tmplFieldGroupId) {
		this.tmplFieldGroupId = tmplFieldGroupId;
	}


	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getRelationEntityCode() {
		return relationEntityCode;
	}

	public void setRelationEntityCode(String relationEntityCode) {
		this.relationEntityCode = relationEntityCode;
	}

	public String getRelationLabel() {
		return relationLabel;
	}

	public void setRelationLabel(String relationLabel) {
		this.relationLabel = relationLabel;
	}

	public List<TemplateActionArrayEntityField> getFields() {
		return fields;
	}

	public void setFields(List<TemplateActionArrayEntityField> fields) {
		this.fields = fields;
	}
}
