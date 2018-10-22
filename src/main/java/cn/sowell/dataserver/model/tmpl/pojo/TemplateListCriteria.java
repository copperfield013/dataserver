package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;

import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;

@Entity
@Table(name="t_sa_tmpl_list_criteria")
public class TemplateListCriteria {
	
	public TemplateListCriteria() {}
	
	public TemplateListCriteria(Long id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="tmpl_id")
	private Long templateId;
	
	@Column(name="c_title")
	private String title;
	
	@Column(name="field_id")
	private Long fieldId;
	
	@Column(name="composite_id")
	private Long compositeId;
	
	@Transient
	@JSONField(serialize=false)
	private DictionaryComposite composite;
	
	//@Column(name="c_field_key")
	@Transient
	private String fieldKey;
	
	@Column(name="c_relation")
	private String relation;
	
	@Column(name="c_query_show")
	private Integer queryShow;
	
	@Column(name="c_comparator")
	private String comparator;
	
	@Column(name="c_input_type")
	private String inputType;
	
	@Column(name="c_relation_label")
	private String relationLabel;
	
	@Column(name="c_order")
	private Integer order;
	
	@Column(name="c_view_option")
	private String viewOption;
	
	@Column(name="c_def_val")
	private String defaultValue;
	
	@Column(name="c_placeholder")
	private String placeholder;
	
	@Column(name="create_time")
	@JSONField(serialize=false)
	private Date createTime;
	
	@Column(name="update_time")
	@JSONField(serialize=false)
	private Date updateTime;
	
	@Column(name="create_user_id")
	@JSONField(serialize=false)
	private Long createUserId;

	@Transient
	private boolean fieldAvailable = true;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getFieldId() {
		return fieldId;
	}
	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}
	public String getInputType() {
		return inputType;
	}
	public void setInputType(String inputType) {
		this.inputType = inputType;
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
	public Long getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}
	public String getFieldKey() {
		return fieldKey;
	}
	public void setFieldKey(String fieldKey) {
		this.fieldKey = fieldKey;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public String getViewOption() {
		return viewOption;
	}
	public void setViewOption(String viewOption) {
		this.viewOption = viewOption;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	public String getComparator() {
		return comparator;
	}
	public void setComparator(String comparator) {
		this.comparator = comparator;
	}
	public String getPlaceholder() {
		return placeholder;
	}
	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public Integer getQueryShow() {
		return queryShow;
	}
	public void setQueryShow(Integer queryShow) {
		this.queryShow = queryShow;
	}

	public String getRelationLabel() {
		return relationLabel;
	}

	public void setRelationLabel(String relationLabel) {
		this.relationLabel = relationLabel;
	}
	
	public void setFieldUnavailable() {
		this.fieldAvailable = false;
	}
	
	public boolean getFieldAvailable() {
		return this.fieldAvailable;
	}

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
