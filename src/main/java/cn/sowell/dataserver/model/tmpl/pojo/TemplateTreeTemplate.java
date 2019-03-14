package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_tree")
public class TemplateTreeTemplate extends Cachable {
	@Column(name="c_def_node_color")
	private String defaultNodeColor;
	
	@Column(name="c_max_deep")
	private Integer maxDeep;
	
	@Transient
	private List<TemplateTreeNode> nodes;
	public String getDefaultNodeColor() {
		return defaultNodeColor;
	}
	public void setDefaultNodeColor(String defaultNodeColor) {
		this.defaultNodeColor = defaultNodeColor;
	}
	public Integer getMaxDeep() {
		return maxDeep;
	}
	public void setMaxDeep(Integer maxDeep) {
		this.maxDeep = maxDeep;
	}
	public List<TemplateTreeNode> getNodes() {
		return nodes;
	}
	public void setNodes(List<TemplateTreeNode> nodes) {
		this.nodes = nodes;
	}
	
}
