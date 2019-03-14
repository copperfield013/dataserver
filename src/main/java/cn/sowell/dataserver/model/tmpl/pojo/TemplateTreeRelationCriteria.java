package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;

import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;

@Entity
@Table(name="t_sa_tmpl_tree_relation_criteria")
public class TemplateTreeRelationCriteria extends AbstractListCriteria{
	@Column(name="composite_id")
	private Long compositeId;
	
	@Transient
	@JSONField(serialize=false)
	private DictionaryComposite composite;

	public Long getCompositeId() {
		return compositeId;
	}

	public void setCompositeId(Long compositeId) {
		this.compositeId = compositeId;
	}

	public DictionaryComposite getComposite() {
		return composite;
	}

	public void setComposite(DictionaryComposite composite) {
		this.composite = composite;
	}
}
