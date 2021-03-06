package cn.sowell.dataserver.model.dict.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;

import cn.sowell.datacenter.entityResolver.Composite;
import cn.sowell.dataserver.Constants;

/*@Entity
@Table(name="v_sa_dictionary_composite")*/
public class DictionaryComposite implements Composite, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5613014342320606232L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JSONField(name="c_id")
	private Integer id;
	
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
	
	@Column(name="c_rel_module_name")
	private String relModuleName;
	
	@Column(name="c_opt")
	private String access;
	
	@Transient
	private String relationLabelAccess;
	
	@Transient
	private String relationKey;
	
	@Transient
	private List<DictionaryField> fields;

	@Transient
	private Set<String> relationSubdomain;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer integer) {
		this.id = integer;
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
	public String getAccess() {
		return access;
	}
	public void setAccess(String access) {
		this.access = access;
	}
	public String getRelationLabelAccess() {
		return relationLabelAccess;
	}
	public void setRelationLabelAccess(String relationLabelAccess) {
		this.relationLabelAccess = relationLabelAccess;
	}
	public String getRelationKey() {
		return relationKey;
	}
	public void setRelationKey(String relationKey) {
		this.relationKey = relationKey;
	}
	public String getRelModuleName() {
		return relModuleName;
	}
	public void setRelModuleName(String relModuleName) {
		this.relModuleName = relModuleName;
	}
	public String getCompositeType() {
		if(!Constants.TRUE.equals(this.getIsArray())) {
			return "normal";
		}else if(Composite.RELATION_ADD_TYPE.equals(this.getAddType())) {
			return "relation";
		}else {
			return "multiattr";
		}
	}
}
