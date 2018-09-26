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

@Entity
@Table(name="t_sa_tmpl_detail_field")
public class TemplateDetailField {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="field_id")
	private Long fieldId;
	
	@Column(name="group_id")
	private Long groupId;

	@Transient
	@JSONField(name="name")
	private String fieldName;
	
	@Transient
	private boolean fieldAvailable = true;
	
	//字段是否可以被编辑
	@Transient
	private String fieldAccess;
	
	//如果是一对多字段，那么要判断新建的记录的该字段是否可以编辑
	@Transient
	private String additionAccess;
	
	@Column(name="c_title")
	private String title;
	
	@Column(name="c_order")
	private Integer order;
	
	@Column(name="c_col_num")
	private Integer colNum;
	
	@Column(name="c_unmodifiable")
	private Integer unmodifiable;
	
	@Column(name="update_time")
	private Date updateTime;
	
	@Column(name="c_view_value")
	@JSONField(name="dv")
	private String viewValue;
	
	
	@Transient
	@Column(name="optgroup_id")
	private Long optionGroupId;
	
	@Transient
	private String optionGroupKey;
	
	@Transient
	@Column(name="c_type")
	private String type;

	@Column(name="c_validators")
	private String validators;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFieldId() {
		return fieldId;
	}
	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public Integer getColNum() {
		return colNum;
	}
	public void setColNum(Integer colNum) {
		this.colNum = colNum;
	}
	public Integer getUnmodifiable() {
		return unmodifiable;
	}
	public void setUnmodifiable(Integer unmodifiable) {
		this.unmodifiable = unmodifiable;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getViewValue() {
		return viewValue;
	}
	public void setViewValue(String viewValue) {
		this.viewValue = viewValue;
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
	public void setFieldUnavailable() {
		this.fieldAvailable = false;
	}
	
	public boolean getFieldAvailable() {
		return this.fieldAvailable;
	}
	public String getValidators() {
		return validators;
	}
	public void setValidators(String validators) {
		this.validators = validators;
	}
	public String getFieldAccess() {
		return fieldAccess;
	}
	public void setFieldAccess(String fieldAccess) {
		this.fieldAccess = fieldAccess;
	}
	public String getAdditionAccess() {
		return additionAccess;
	}
	public void setAdditionAccess(String additionAccess) {
		this.additionAccess = additionAccess;
	}
	public String getOptionGroupKey() {
		return optionGroupKey;
	}
	public void setOptionGroupKey(String optionGroupKey) {
		this.optionGroupKey = optionGroupKey;
	}
	
}
