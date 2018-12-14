package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_action_arrayentity_field")
public class TemplateActionArrayEntityField {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="actionarray_entity_id")
	private Long actionArrayEntityId;
	
	@Column(name="tmpl_field_id")
	private Long tmplFieldId;
	
	
	@Column(name="c_value")
	private String value;

	@Transient
	private Long fieldId;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getActionArrayEntityId() {
		return actionArrayEntityId;
	}

	public void setActionArrayEntityId(Long actionArrayEntityId) {
		this.actionArrayEntityId = actionArrayEntityId;
	}

	public Long getTmplFieldId() {
		return tmplFieldId;
	}

	public void setTmplFieldId(Long tmplFieldId) {
		this.tmplFieldId = tmplFieldId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getFieldId() {
		return fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}
}
