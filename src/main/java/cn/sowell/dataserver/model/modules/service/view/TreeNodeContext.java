package cn.sowell.dataserver.model.modules.service.view;

import java.util.List;

import org.springframework.util.Assert;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeTemplate;
import cn.sowell.dataserver.model.tmpl.service.impl.TreeTemplateServiceImpl.TreeRelationComposite;

public class TreeNodeContext {
	private List<String> path;
	private TreeRelationComposite relationComposite;
	private TemplateTreeTemplate treeTemplate;
	public TreeNodeContext(TreeRelationComposite relationComposite) {
		Assert.notNull(relationComposite);
		this.relationComposite = relationComposite;
	}
	public TreeNodeContext(TemplateTreeTemplate treeTemplate) {
		Assert.notNull(treeTemplate);
		this.treeTemplate = treeTemplate;
	}
	public String getModuleName() {
		if(this.relationComposite != null) {
			return relationComposite.getRelationTempalte().getRelationModule();
		}
		return treeTemplate.getModule();
	}
	public String getRelationName() {
		if(this.relationComposite != null) {
			return relationComposite.getRelationTempalte().getRelationName();
		}
		return null;
	}
	public List<String> getPath() {
		return path;
	}
	public void setPath(List<String> path) {
		Assert.state(this.treeTemplate != null && (path == null || path.isEmpty()), "根节点的上下文不能设置有长度的path");
		Assert.state(this.relationComposite != null && path != null && !path.isEmpty(), "非根节点的上下文，path不能为空");
		this.path = path;
	}
	public TemplateTreeTemplate getTreeTemplate() {
		if(this.relationComposite != null) {
			return this.relationComposite.getTreeTemplate();
		}
		return this.treeTemplate;
	}
}
