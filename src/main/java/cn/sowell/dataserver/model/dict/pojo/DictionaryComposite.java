package cn.sowell.dataserver.model.dict.pojo;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;

import cn.sowell.datacenter.entityResolver.Composite;

@Entity
@Table(name="v_dictionary_composite")
public class DictionaryComposite implements Composite{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JSONField(name="c_id")
	private Long id;
	
	@Column(name="c_name")
	@JSONField(name="name")
	private String name;
	
	@Column(name="c_title")
	@JSONField(name="cname")
	private String title;
	
	@Column(name="c_module")
	private String module;
	
	@Column(name="c_is_array")
	@JSONField(name="isArray")
	private Integer isArray;
	
	@Column(name="add_type")
	private Integer addType;
	
	@Transient
	private List<DictionaryField> fields;

	@Transient
	private Set<String> relationSubdomain;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<DictionaryField> getFields() {
		return fields;
	}
	public void setFields(List<DictionaryField> fields) {
		this.fields = fields;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public Integer getIsArray() {
		return isArray;
	}
	public void setIsArray(Integer isArray) {
		this.isArray = isArray;
	}
	public void setRelationSubdomain(Set<String> relationSubdomain) {
		this.relationSubdomain = relationSubdomain;
	}
	public Set<String> getRelationSubdomain() {
		return relationSubdomain;
	}
	public Integer getAddType() {
		return addType;
	}
	public void setAddType(Integer addType) {
		this.addType = addType;
	}
	
}
