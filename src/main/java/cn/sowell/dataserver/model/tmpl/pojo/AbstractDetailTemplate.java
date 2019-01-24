package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class AbstractDetailTemplate<GT extends AbstractDetailFieldGroup<FT>, FT extends AbstractDetailField> extends Cachable{
	
	@Transient
	private List<GT> groups = new ArrayList<GT>();
	public List<GT> getGroups() {
		return groups;
	}
	public void setGroups(List<GT> groups) {
		this.groups = groups;
	}
}
