package cn.sowell.dataserver.model.modules.service.view;

import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;

public class EntityNode extends ModuleEntityItem {
	private String text;
	private String nodeColor;
	
	public EntityNode(ModuleEntityPropertyParser parser) {
		super(parser);
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getNodeColor() {
		return nodeColor;
	}
	public void setNodeColor(String nodeColor) {
		this.nodeColor = nodeColor;
	}
	
}
