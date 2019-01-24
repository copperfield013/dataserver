package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="t_sa_tmpl_list_template")
public class TemplateListTemplate extends AbstractListTemplate<TemplateListColumn, TemplateListCriteria>{
	
	
	@Column(name="c_unmodifiable")
	private Integer unmodifiable;
	
	public Integer getUnmodifiable() {
		return unmodifiable;
	}
	public void setUnmodifiable(Integer unmodifiable) {
		this.unmodifiable = unmodifiable;
	}
}
