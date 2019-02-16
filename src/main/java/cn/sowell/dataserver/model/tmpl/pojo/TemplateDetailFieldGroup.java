package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="t_sa_tmpl_detail_fieldgroup")
public class TemplateDetailFieldGroup extends AbstractDetailFieldGroup<TemplateDetailField>{
	@Column(name="rdtmpl_id")
	private Long relationDetailTemplateId;

	public Long getRelationDetailTemplateId() {
		return relationDetailTemplateId;
	}

	public void setRelationDetailTemplateId(Long relationDetailTemplateId) {
		this.relationDetailTemplateId = relationDetailTemplateId;
	}

}
