package cn.sowell.dataserver.model.modules.pojo.criteria;

import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;

public class NormalCriteria implements Cloneable{
	private Long fieldId;
	private String fieldName;
	private String comparator;
	private String value;
	private String relationLabel;
	private DictionaryComposite composite;
	
	public NormalCriteria() {
		super();
	}
	
	@Override
	public NormalCriteria clone() {
		NormalCriteria criteria = new NormalCriteria();
		criteria.setFieldId(this.getFieldId());
		criteria.setFieldName(this.getFieldName());
		criteria.setComparator(this.getComparator());
		criteria.setValue(this.getValue());
		criteria.setRelationLabel(this.getRelationLabel());
		criteria.setComposite(this.getComposite());
		return criteria;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public DictionaryComposite getComposite() {
		return composite;
	}
	public void setComposite(DictionaryComposite composite) {
		this.composite = composite;
	}
	public Long getFieldId() {
		return fieldId;
	}
	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getComparator() {
		return comparator;
	}
	public void setComparator(String comparator) {
		this.comparator = comparator;
	}
	public String getRelationLabel() {
		return relationLabel;
	}
	public void setRelationLabel(String relationLabel) {
		this.relationLabel = relationLabel;
	}
}
