package cn.sowell.dataserver.model.karuiserv.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.sowell.dataserver.model.tmpl.pojo.Cachable;

@Entity
@Table(name="t_sa_ks_json_meta")
public class KaruiServJsonMeta extends Cachable {
	
	@Column(name="c_meta_json")
	private String metaJson;
	
	@Column(name="c_desc")
	private String description;
	
	
	public String getMetaJson() {
		return metaJson;
	}
	public void setMetaJson(String metaJson) {
		this.metaJson = metaJson;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
