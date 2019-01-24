package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="t_sa_tmpl_dictfilter")
public class TemplateDictionaryFilter extends Cachable{
	
	public static final String BASE_RANGE_ALL = "all";
	public static final String BASE_RANGE_EMPTY = "empty";

	@Column(name="c_express")
	private String express;
	
	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

}
