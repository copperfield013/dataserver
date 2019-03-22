package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_tree_node")
public class TemplateTreeNode {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="ttmpl_id")
	private Long treeTemplateId;
	
	@Column(name="c_selector")
	private String selector;
	
	@Column(name="c_text")
	private String text;
	
	@Column(name="c_module_name")
	private String moduleName;
	
	@Column(name="tmpl_group_id")
	private Long templateGroupId;
	
	@Transient
	private String templateGroupTitle;
	
	@Column(name="c_hide_detail_btn")
	private Integer hideDetailButton;
	
	@Column(name="c_hide_update_btn")
	private Integer hideUpdateButton;
	
	@Column(name="c_node_color")
	private String nodeColor;
	
	@Column(name="c_order")
	private Integer order;
	
	@Transient
	private List<TemplateTreeRelation> relations;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSelector() {
		return selector;
	}
	public void setSelector(String selector) {
		this.selector = selector;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public List<TemplateTreeRelation> getRelations() {
		return relations;
	}
	public void setRelations(List<TemplateTreeRelation> relations) {
		this.relations = relations;
	}
	public String getNodeColor() {
		return nodeColor;
	}
	public void setNodeColor(String nodeColor) {
		this.nodeColor = nodeColor;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public Long getTreeTemplateId() {
		return treeTemplateId;
	}
	public void setTreeTemplateId(Long treeTemplateId) {
		this.treeTemplateId = treeTemplateId;
	}
	public Long getTemplateGroupId() {
		return templateGroupId;
	}
	public void setTemplateGroupId(Long templateGroupId) {
		this.templateGroupId = templateGroupId;
	}
	public String getTemplateGroupTitle() {
		return templateGroupTitle;
	}
	public void setTemplateGroupTitle(String templateGroupTitle) {
		this.templateGroupTitle = templateGroupTitle;
	}
	public Integer getHideDetailButton() {
		return hideDetailButton;
	}
	public void setHideDetailButton(Integer hideDetailButton) {
		this.hideDetailButton = hideDetailButton;
	}
	public Integer getHideUpdateButton() {
		return hideUpdateButton;
	}
	public void setHideUpdateButton(Integer hideUpdateButton) {
		this.hideUpdateButton = hideUpdateButton;
	}
}
