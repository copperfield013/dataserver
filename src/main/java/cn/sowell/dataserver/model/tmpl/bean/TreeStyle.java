package cn.sowell.dataserver.model.tmpl.bean;

import java.util.ArrayList;
import java.util.List;

public class TreeStyle {
	private String defaultNodeColor;
	private List<TreeNodeStyle> nodeStyles = new ArrayList<>();
	public String getDefaultNodeColor() {
		return defaultNodeColor;
	}
	public void setDefaultNodeColor(String defaultNodeColor) {
		this.defaultNodeColor = defaultNodeColor;
	}
	public List<TreeNodeStyle> getNodeStyles() {
		return nodeStyles;
	}
	public void setNodeStyles(List<TreeNodeStyle> nodeStyles) {
		this.nodeStyles = nodeStyles;
	}
}
