package cn.sowell.dataserver.model.dict.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;

import cn.sowell.datacenter.entityResolver.Field;

@Entity
@Table(name="v_sa_dictionary_field")
public class DictionaryField implements Field{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JSONField(name="id")
	private Long id;
	
	@Column(name="composite_id")
	@JSONField(name="c_id")
	private Long compositeId;
	
	@Transient
	@JSONField(serialize=false, deserialize=false)
	private DictionaryComposite composite;
	
	@Column(name="c_full_key")
	@JSONField(name="name")
	private String fullKey;
	
	
	@Column(name="c_title")
	@JSONField(name="cname")
	private String title;
	
	@Column(name="c_type")
	@JSONField(name="type")
	private String type;
	
	@Column(name="c_abc_type")
	private String abcType;
	
	@Column(name="optgroup_id")
	private Long optionGroupId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCompositeId() {
		return compositeId;
	}
	public void setCompositeId(Long compositeId) {
		this.compositeId = compositeId;
	}
	public String getFullKey() {
		return fullKey;
	}
	public void setFullKey(String fullKey) {
		this.fullKey = fullKey;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getOptionGroupId() {
		return optionGroupId;
	}
	public void setOptionGroupId(Long optionGroupId) {
		this.optionGroupId = optionGroupId;
	}
	public String getAbcType() {
		return abcType;
	}
	public void setAbcType(String abcType) {
		this.abcType = abcType;
	}
	public DictionaryComposite getComposite() {
		return composite;
	}
	public void setComposite(DictionaryComposite composite) {
		this.composite = composite;
	}
}
