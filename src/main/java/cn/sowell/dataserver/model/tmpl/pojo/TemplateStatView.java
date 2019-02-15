package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="t_sa_tmpl_stat_view")
public class TemplateStatView extends Cachable{
	
	@Column(name="stat_ltmpl_id")
	private Long statListTemplateId;
	

	public Long getStatListTemplateId() {
		return statListTemplateId;
	}

	public void setStatListTemplateId(Long statListTemplateId) {
		this.statListTemplateId = statListTemplateId;
	}

	
}
