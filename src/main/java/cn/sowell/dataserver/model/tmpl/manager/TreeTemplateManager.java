package cn.sowell.dataserver.model.tmpl.manager;

import java.util.List;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeNode;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeRelation;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeTemplate;

public interface TreeTemplateManager extends ModuleCachableManager<TemplateTreeTemplate>{

	List<TemplateTreeTemplate> queryByNodeModule(String nodeModule);

	TreeRelationComposite getNodeRelationTemplate(Long nodeRelationTemplateId);
	
	public static class TreeRelationComposite{
		private TemplateTreeTemplate treeTemplate;
		private TemplateTreeNode nodeTemplate;
		private TemplateTreeRelation relationTempalte;
		public TreeRelationComposite(TemplateTreeTemplate treeTemplate, TemplateTreeNode nodeTemplate,
				TemplateTreeRelation reltionTempalte) {
			super();
			this.treeTemplate = treeTemplate;
			this.nodeTemplate = nodeTemplate;
			this.relationTempalte = reltionTempalte;
		}
		public TemplateTreeTemplate getTreeTemplate() {
			return treeTemplate;
		}
		public void setTreeTemplate(TemplateTreeTemplate treeTemplate) {
			this.treeTemplate = treeTemplate;
		}
		public TemplateTreeNode getNodeTemplate() {
			return nodeTemplate;
		}
		public void setNodeTemplate(TemplateTreeNode nodeTemplate) {
			this.nodeTemplate = nodeTemplate;
		}
		public TemplateTreeRelation getRelationTempalte() {
			return relationTempalte;
		}
		public void setRelationTempalte(TemplateTreeRelation reltionTempalte) {
			this.relationTempalte = reltionTempalte;
		}
	}
}
