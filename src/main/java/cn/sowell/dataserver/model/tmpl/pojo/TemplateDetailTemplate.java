package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_detail_template")
public class TemplateDetailTemplate extends AbstractTemplate{
	
	
	@Transient
	private List<TemplateDetailFieldGroup> groups = new ArrayList<TemplateDetailFieldGroup>();
	public List<TemplateDetailFieldGroup> getGroups() {
		return groups;
	}
	public void setGroups(List<TemplateDetailFieldGroup> groups) {
		this.groups = groups;
	}
}
