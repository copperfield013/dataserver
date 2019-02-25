package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_detail_arrayitem_filter")
public class TemplateDetailArrayItemFilter extends Cachable{
	@Transient
	private List<TemplateDetailArrayItemCriteria> criterias;

	public void setCriterias(List<TemplateDetailArrayItemCriteria> criterias) {
		this.criterias = criterias;
	}

	public List<TemplateDetailArrayItemCriteria> getCriterias() {
		return criterias;
	}
}
