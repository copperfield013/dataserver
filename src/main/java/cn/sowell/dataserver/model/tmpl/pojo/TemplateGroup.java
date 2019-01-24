package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_group")
public class TemplateGroup extends Cachable{
	
	
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
	
	@Column(name="c_disabled")
	private Integer disabled;
	
	@Column(name="imp_dict_filter_id")
	private Long importDictionaryFilterId;
	
	@Transient
	private TemplateGroupDictionaryFilter importDictionaryFilter;
	
	@Column(name="exp_dict_filter_id")
	private Long exportDictionaryFilterId;
	
	@Transient
	private TemplateGroupDictionaryFilter exportDictionaryFilter;
	
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
	
	@Transient
	private List<TemplateGroupPremise> premises = new ArrayList<>();
	
	@Transient
	private List<TemplateGroupAction> actions = new ArrayList<>();


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


	public Integer getDisabled() {
		return disabled;
	}

	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
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


	public Long getExportDictionaryFilterId() {
		return exportDictionaryFilterId;
	}

	public void setExportDictionaryFilterId(Long exportDictionaryFilterId) {
		this.exportDictionaryFilterId = exportDictionaryFilterId;
	}

	public Long getImportDictionaryFilterId() {
		return importDictionaryFilterId;
	}

	public void setImportDictionaryFilterId(Long importDictionaryFilterId) {
		this.importDictionaryFilterId = importDictionaryFilterId;
	}

	public TemplateGroupDictionaryFilter getImportDictionaryFilter() {
		return importDictionaryFilter;
	}

	public void setImportDictionaryFilter(TemplateGroupDictionaryFilter importDictionaryFilter) {
		this.importDictionaryFilter = importDictionaryFilter;
	}

	public TemplateGroupDictionaryFilter getExportDictionaryFilter() {
		return exportDictionaryFilter;
	}

	public void setExportDictionaryFilter(TemplateGroupDictionaryFilter exportDictionaryFilter) {
		this.exportDictionaryFilter = exportDictionaryFilter;
	}
	
}
