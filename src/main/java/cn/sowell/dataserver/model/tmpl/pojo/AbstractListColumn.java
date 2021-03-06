package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class AbstractListColumn {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="tmpl_id")
	private Long templateId;
	
	@Column(name="c_title")
	private String title;
	
	@Column(name="field_id")
	private Integer fieldId;
	
	//@Column(name="c_field_key")
	@Transient
	private String fieldKey;
	
	@Transient
	private boolean fieldAvailable = true;
	
	@Column(name="c_spec_field")
	private String specialField;
	
	@Column(name="c_order")
	private Integer order;
	
	@Column(name="c_orderable")
	private Integer orderable;
	
	@Column(name="c_view_option")
	private String viewOption;
	
	@Column(name="create_time")
	private Date createTime;
	
	@Column(name="update_time")
	private Date updateTime;
	
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
	public Integer getFieldId() {
		return fieldId;
	}
	public void setFieldId(Integer fieldId) {
		this.fieldId = fieldId;
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
	public Integer getOrderable() {
		return orderable;
	}
	public void setOrderable(Integer orderable) {
		this.orderable = orderable;
	}
	public String getViewOption() {
		return viewOption;
	}
	public void setViewOption(String viewOption) {
		this.viewOption = viewOption;
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
	public String getSpecialField() {
		return specialField;
	}
	public void setSpecialField(String specialField) {
		this.specialField = specialField;
	}
	
	public void setFieldUnavailable() {
		this.fieldAvailable = false;
	}
	
	public boolean getFieldAvailable() {
		return this.fieldAvailable;
	}
}
