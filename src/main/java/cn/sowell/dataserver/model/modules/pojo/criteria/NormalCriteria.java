package cn.sowell.dataserver.model.modules.pojo.criteria;

import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListCriteria;

public class NormalCriteria {
	private TemplateListCriteria criteria;
	private String value;
	private String attributeName;
	private DictionaryComposite composite;
	
	public NormalCriteria(TemplateListCriteria criteria) {
		super();
		this.criteria = criteria;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public TemplateListCriteria getCriteria() {
		return criteria;
	}
	public DictionaryComposite getComposite() {
		return composite;
	}
	public void setComposite(DictionaryComposite composite) {
		this.composite = composite;
	}
}
