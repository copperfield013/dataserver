package cn.sowell.dataserver.model.tmpl.bean;

import cn.sowell.dataserver.model.modules.service.view.TreeNodeContext;

@FunctionalInterface
public interface NodeTemplateSelector {

	boolean match(TreeNodeContext nodeContext);
	
}
