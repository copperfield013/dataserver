package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;

import cn.sowell.copframe.utils.FormatUtils;

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
	private Integer fieldId;
	
	@Transient
	private String fieldName;
	
	@Transient
	@JSONField(serialize=false)
	private TemplateActionArrayEntity arrayEntity;
	
	
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
		ArrayEntityProxy proxy = arrayEntity.getArrayEntityProxy();
		if(proxy != null) {
			return FormatUtils.toString(proxy.getFieldValue(this.getFieldName()));
		}
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getFieldId() {
		return fieldId;
	}

	public void setFieldId(Integer fieldId) {
		this.fieldId = fieldId;
	}

	public TemplateActionArrayEntity getArrayEntity() {
		return arrayEntity;
	}

	public void setArrayEntity(TemplateActionArrayEntity arrayEntity) {
		this.arrayEntity = arrayEntity;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
