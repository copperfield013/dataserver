package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@Table(name="t_sa_tmpl_action_field")
public class TemplateActionField extends AbstractDetailField{
	@Transient
	@JSONField(serialize=false)
	private List<TemplateActionArrayEntityField> arrayEntityFields = new ArrayList<>();
	
	public List<TemplateActionArrayEntityField> getArrayEntityFields() {
		return arrayEntityFields;
	}
	public void setArrayEntityFields(List<TemplateActionArrayEntityField> arrayEntityFields) {
		this.arrayEntityFields = arrayEntityFields;
	}
}
