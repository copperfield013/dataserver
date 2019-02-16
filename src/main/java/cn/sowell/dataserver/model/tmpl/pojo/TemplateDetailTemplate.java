package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_detail_template")
public class TemplateDetailTemplate extends AbstractDetailTemplate<TemplateDetailFieldGroup, TemplateDetailField>{
	/**
	 * 详情模板私有，不在列表中显示
	 */
	@Transient
	public static final int RANGE_PRIVATE = 1;
	
	
	@Column(name="c_range")
	private Integer range;


	public Integer getRange() {
		return range;
	}


	public void setRange(Integer range) {
		this.range = range;
	}
}
