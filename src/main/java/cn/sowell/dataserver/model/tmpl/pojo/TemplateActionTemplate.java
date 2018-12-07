package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_action_template")
public class TemplateActionTemplate extends AbstractTemplate{
	
	
	@Transient
	private List<TemplateActionFieldGroup> groups = new ArrayList<TemplateActionFieldGroup>();
	public List<TemplateActionFieldGroup> getGroups() {
		return groups;
	}
	public void setGroups(List<TemplateActionFieldGroup> groups) {
		this.groups = groups;
	}
}
