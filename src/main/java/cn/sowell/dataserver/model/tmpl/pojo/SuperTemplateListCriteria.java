package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;

import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;

@MappedSuperclass
public abstract class SuperTemplateListCriteria extends AbstractListCriteria {

	@Column(name = "composite_id")
	protected Integer compositeId;
	
	@Transient
	@JSONField(serialize=false)
	private DictionaryComposite composite;

	public SuperTemplateListCriteria() {
		super();
	}

	public Integer getCompositeId() {
		return compositeId;
	}

	public void setCompositeId(Integer compositeId) {
		this.compositeId = compositeId;
	}

	public DictionaryComposite getComposite() {
		return composite;
	}

	public void setComposite(DictionaryComposite composite) {
		this.composite = composite;
	}

}