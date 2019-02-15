package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class AbstractListTemplate<COL extends AbstractListColumn, CRI extends AbstractListCriteria> extends Cachable{
	@Column(name="c_def_pagesize")
	private Integer defaultPageSize;
	
	@Column(name="def_order_field_id")
	private Long defaultOrderFieldId;
	
	@Column(name="c_def_order_dir")
	private String defaultOrderDirection;

	@Transient
	private List<COL> columns = new ArrayList<COL>();
	
	@Transient
	private List<CRI> criterias = new ArrayList<CRI>();
	
	
	public Integer getDefaultPageSize() {
		return defaultPageSize;
	}

	public void setDefaultPageSize(Integer defaultPageSize) {
		this.defaultPageSize = defaultPageSize;
	}

	public Long getDefaultOrderFieldId() {
		return defaultOrderFieldId;
	}

	public void setDefaultOrderFieldId(Long defaultOrderFieldId) {
		this.defaultOrderFieldId = defaultOrderFieldId;
	}

	public String getDefaultOrderDirection() {
		return defaultOrderDirection;
	}

	public void setDefaultOrderDirection(String defaultOrderDirection) {
		this.defaultOrderDirection = defaultOrderDirection;
	}

	public List<COL> getColumns() {
		return columns;
	}

	public void setColumns(List<COL> columns) {
		this.columns = columns;
	}

	public List<CRI> getCriterias() {
		return criterias;
	}

	public void setCriterias(List<CRI> criterias) {
		this.criterias = criterias;
	}
}
