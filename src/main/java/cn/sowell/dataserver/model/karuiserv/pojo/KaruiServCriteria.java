package cn.sowell.dataserver.model.karuiserv.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_sa_ks_criteria")
public class KaruiServCriteria {
	
	public static final String SOURCE_PATH_VAR = "path-var";
	public static final String SOURCE_PARAM = "param";
	public static final String SOURCE_CONSTANT = "constant";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="c_name")
	private String name;
	
	@Column(name="c_source")
	private String source;
	
	@Column(name="ltmpl_field_id")
	private Long ltmplFieldId;
	
	@Column(name="field_id")
	private Long fieldId;
	
	@Column(name="ks_id")
	private Long karuiServId;
	
	@Column(name="create_time")
	private Date createTime;
	
	@Column(name="update_time")
	private Date updateTime;
	
	@Column(name="c_const_val")
	private String constantValue;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getLtmplFieldId() {
		return ltmplFieldId;
	}
	public void setLtmplFieldId(Long ltmplFieldId) {
		this.ltmplFieldId = ltmplFieldId;
	}
	public Long getKaruiServId() {
		return karuiServId;
	}
	public void setKaruiServId(Long karuiServId) {
		this.karuiServId = karuiServId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getConstantValue() {
		return constantValue;
	}
	public void setConstantValue(String constantValue) {
		this.constantValue = constantValue;
	}
	public Long getFieldId() {
		return fieldId;
	}
	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}
}
