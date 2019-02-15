package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_stat_list_criteria")
public class TemplateStatCriteria extends AbstractListCriteria{
	@Transient
	public static final int FILTER_OCCASION_BEFORE = 1;
	@Transient
	public static final int FILTER_OCCASION_AFTER = 2;
	
	@Column(name="c_filter_occasion")
	private Integer filterOccasion;
	

	public Integer getFilterOccasion() {
		return filterOccasion;
	}

	public void setFilterOccasion(Integer filterOccasion) {
		this.filterOccasion = filterOccasion;
	}
}
