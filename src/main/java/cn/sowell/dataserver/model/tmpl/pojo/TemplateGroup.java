package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_group")
public class TemplateGroup {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="c_title")
	private String title;
	
	@Column(name="c_module")
	private String module;
	
	@Column(name="list_tmpl_id")
	private Long listTemplateId;
	
	@Transient
	@Column(name="list_tmpl_title")
	private String listTemplateTitle;
	
	@Column(name="detail_tmpl_id")
	private Long detailTemplateId;
	
	@Transient
	@Column(name="detail_tmpl_title")
	private String detailTemplateTitle;
	
	@Column(name="c_key", updatable=false)
	private String key;
	
	@Column(name="c_authority")
	private String authority;
	
	@Column(name="c_disabled")
	private Integer disabled;
	
	@Column(name="create_user_code")
	private String createUserCode;
	
	
	@Column(name="c_hide_create_btn")
	private Integer hideCreateButton;
	
	@Column(name="c_hide_import_btn")
	private Integer hideImportButton;
	
	@Column(name="c_hide_export_btn")
	private Integer hideExportButton;
	
	@Column(name="c_hide_query_btn")
	private Integer hideQueryButton;
	
	@Column(name="c_hide_delete_btn")
	private Integer hideDeleteButton;
	
	@Column(name="c_hide_save_btn")
	private Integer hideSaveButton;
	
	@Column(name="create_time")
	private Date createTime;
	
	@Column(name="update_time")
	private Date updateTime;
	
	@Transient
	private List<TemplateGroupPremise> premises = new ArrayList<>();
	
	@Transient
	private List<TemplateGroupAction> actions = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Long getListTemplateId() {
		return listTemplateId;
	}

	public void setListTemplateId(Long listTemplateId) {
		this.listTemplateId = listTemplateId;
	}

	public String getListTemplateTitle() {
		return listTemplateTitle;
	}

	public void setListTemplateTitle(String listTemplateTitle) {
		this.listTemplateTitle = listTemplateTitle;
	}

	public Long getDetailTemplateId() {
		return detailTemplateId;
	}

	public void setDetailTemplateId(Long detailTemplateId) {
		this.detailTemplateId = detailTemplateId;
	}

	public String getDetailTemplateTitle() {
		return detailTemplateTitle;
	}

	public void setDetailTemplateTitle(String detailTemplateTitle) {
		this.detailTemplateTitle = detailTemplateTitle;
	}


	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public Integer getDisabled() {
		return disabled;
	}

	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<TemplateGroupPremise> getPremises() {
		return premises;
	}

	public void setPremises(List<TemplateGroupPremise> premises) {
		this.premises = premises;
	}

	public String getCreateUserCode() {
		return createUserCode;
	}

	public void setCreateUserCode(String createUserCode) {
		this.createUserCode = createUserCode;
	}

	public Integer getHideCreateButton() {
		return hideCreateButton;
	}

	public void setHideCreateButton(Integer hideCreateButton) {
		this.hideCreateButton = hideCreateButton;
	}

	public Integer getHideImportButton() {
		return hideImportButton;
	}

	public void setHideImportButton(Integer hideImportButton) {
		this.hideImportButton = hideImportButton;
	}

	public Integer getHideExportButton() {
		return hideExportButton;
	}

	public void setHideExportButton(Integer hideExportButton) {
		this.hideExportButton = hideExportButton;
	}

	public List<TemplateGroupAction> getActions() {
		return actions;
	}

	public void setActions(List<TemplateGroupAction> actions) {
		this.actions = actions;
	}

	public Integer getHideQueryButton() {
		return hideQueryButton;
	}

	public void setHideQueryButton(Integer hideQueryButton) {
		this.hideQueryButton = hideQueryButton;
	}

	public Integer getHideDeleteButton() {
		return hideDeleteButton;
	}

	public void setHideDeleteButton(Integer hideDeleteButton) {
		this.hideDeleteButton = hideDeleteButton;
	}

	public Integer getHideSaveButton() {
		return hideSaveButton;
	}

	public void setHideSaveButton(Integer hideSaveButton) {
		this.hideSaveButton = hideSaveButton;
	}


	
}
