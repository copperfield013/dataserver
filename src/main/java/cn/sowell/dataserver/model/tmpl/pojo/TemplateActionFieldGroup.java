package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_action_fieldgroup")
public class TemplateActionFieldGroup extends AbstractDetailFieldGroup<TemplateActionField> {
	@Transient
	private List<TemplateActionArrayEntity> entities = new ArrayList<TemplateActionArrayEntity>();
	
	public List<TemplateActionArrayEntity> getEntities() {
		return entities;
	}

	public void setEntities(List<TemplateActionArrayEntity> entities) {
		this.entities = entities;
	}

}
