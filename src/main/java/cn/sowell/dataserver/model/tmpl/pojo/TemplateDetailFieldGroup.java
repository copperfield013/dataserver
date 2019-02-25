package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="t_sa_tmpl_detail_fieldgroup")
public class TemplateDetailFieldGroup extends AbstractDetailFieldGroup<TemplateDetailField>{
	@Column(name="rdtmpl_id")
	private Long relationDetailTemplateId;
	
	@Column(name="rabc_tmpl_group_id")
	private Long rabcTemplateGroupId;
	
	@Column(name="c_rabc_uncreatable")
	private Integer rabcUncreatable;
	
	@Column(name="c_rabc_unupdatable")
	private Integer rabcUnupdatable; 
	
	@Column(name="arrayitem_filter_id")
	private Long arrayItemFilterId;

	public Long getRelationDetailTemplateId() {
		return relationDetailTemplateId;
	}

	public void setRelationDetailTemplateId(Long relationDetailTemplateId) {
		this.relationDetailTemplateId = relationDetailTemplateId;
	}

	public Long getRabcTemplateGroupId() {
		return rabcTemplateGroupId;
	}

	public void setRabcTemplateGroupId(Long rabcTemplateGroupId) {
		this.rabcTemplateGroupId = rabcTemplateGroupId;
	}

	public Integer getRabcUncreatable() {
		return rabcUncreatable;
	}

	public void setRabcUncreatable(Integer rabcUncreatable) {
		this.rabcUncreatable = rabcUncreatable;
	}

	public Integer getRabcUnupdatable() {
		return rabcUnupdatable;
	}

	public void setRabcUnupdatable(Integer rabcUnupdatable) {
		this.rabcUnupdatable = rabcUnupdatable;
	}

	public Long getArrayItemFilterId() {
		return arrayItemFilterId;
	}

	public void setArrayItemFilterId(Long arrayItemFilterId) {
		this.arrayItemFilterId = arrayItemFilterId;
	}


}
