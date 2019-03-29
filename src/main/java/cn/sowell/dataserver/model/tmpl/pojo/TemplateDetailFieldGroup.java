package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_detail_fieldgroup")
public class TemplateDetailFieldGroup extends AbstractDetailFieldGroup<TemplateDetailField>{
	public static String DIALOG_SELECT_TYPE_STMPL = "stmpl";
	public static String DIALOG_SELECT_TYPE_TTMPL = "ttmpl";
	public static String DIALOG_SELECT_TYPE_LTMPL = "ltmpl";
	
	@Column(name="c_dialog_select_type")
	private String dialogSelectType;
	
	@Column(name="rabc_tree_tmpl_id")
	private Long rabcTreeTemplateId;
	
	@Transient
	private String rabcTreeTemplateTitle;
	
	@Transient
	private List<TemplateDetailFieldGroupTreeNode> rabcTreeNodes;
	
	@Column(name="rabc_tmpl_group_id")
	private Long rabcTemplateGroupId;
	
	@Transient
	private String rabcTemplateGroupTitle;
	
	@Column(name="c_rabc_uncreatable")
	private Integer rabcUncreatable;
	
	@Column(name="c_rabc_unupdatable")
	private Integer rabcUnupdatable; 
	
	@Column(name="arrayitem_filter_id")
	private Long arrayItemFilterId;

	public Long getRabcTemplateGroupId() {
		return rabcTemplateGroupId;
	}

	public void setRabcTemplateGroupId(Long rabcTemplateGroupId) {
		this.rabcTemplateGroupId = rabcTemplateGroupId;
	}

	public Integer getRabcUncreatable() {
		return rabcUncreatable;
	}

	public void setRabcUncreatable(Integer rabcUncreatable) {
		this.rabcUncreatable = rabcUncreatable;
	}

	public Integer getRabcUnupdatable() {
		return rabcUnupdatable;
	}

	public void setRabcUnupdatable(Integer rabcUnupdatable) {
		this.rabcUnupdatable = rabcUnupdatable;
	}

	public Long getArrayItemFilterId() {
		return arrayItemFilterId;
	}

	public void setArrayItemFilterId(Long arrayItemFilterId) {
		this.arrayItemFilterId = arrayItemFilterId;
	}

	public String getRabcTemplateGroupTitle() {
		return rabcTemplateGroupTitle;
	}

	public void setRabcTemplateGroupTitle(String rabcTemplateGroupTitle) {
		this.rabcTemplateGroupTitle = rabcTemplateGroupTitle;
	}

	public Long getRabcTreeTemplateId() {
		return rabcTreeTemplateId;
	}

	public void setRabcTreeTemplateId(Long rabcTreeTemplateId) {
		this.rabcTreeTemplateId = rabcTreeTemplateId;
	}

	public List<TemplateDetailFieldGroupTreeNode> getRabcTreeNodes() {
		return rabcTreeNodes;
	}

	public void setRabcTreeNodes(List<TemplateDetailFieldGroupTreeNode> rabcTreeNodes) {
		this.rabcTreeNodes = rabcTreeNodes;
	}

	public String getRabcTreeTemplateTitle() {
		return rabcTreeTemplateTitle;
	}

	public void setRabcTreeTemplateTitle(String rabcTreeTemplateTitle) {
		this.rabcTreeTemplateTitle = rabcTreeTemplateTitle;
	}

	public String getDialogSelectType() {
		return dialogSelectType;
	}

	public void setDialogSelectType(String dialogSelectType) {
		this.dialogSelectType = dialogSelectType;
	}


}
