package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.dataserver.model.modules.service.view.TreeNodeContext;
import cn.sowell.dataserver.model.tmpl.bean.NodeTemplateSelector;
import cn.sowell.dataserver.model.tmpl.manager.TreeTemplateManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeNode;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeRelation;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeTemplate;
import cn.sowell.dataserver.model.tmpl.service.TreeTemplateService;

@Service
public class TreeTemplateServiceImpl
	extends AbstractTemplateService<TemplateTreeTemplate, TreeTemplateManager>
	implements TreeTemplateService{

	@Autowired
	protected TreeTemplateServiceImpl(@Autowired TreeTemplateManager manager) {
		super(manager);
	}
	
	final Pattern pattern = Pattern.compile("\\$\\{([^\\}]+)\\}");
	
	@Override
	public String parserNodeText(String text, ModuleEntityPropertyParser entity) {
		Assert.notNull(entity, "entity不能为空");
		try {
			Matcher matcher = pattern.matcher(text);
			StringBuffer buffer = new StringBuffer();
			Map<String, String> map = entity.getSmap();
			while(matcher.find()) {
				String propertyName = matcher.group(1);
				String propertyValue = map.get(propertyName);
				matcher.appendReplacement(buffer, propertyValue);
			}
			return buffer.toString();
		} catch (Exception e) {
			return entity.getTitle() + "(文本解析失败)";
		}
	}
	
	
	@Override
	public TemplateTreeNode analyzeNodeTemplate(TreeNodeContext nodeContext) {
		TemplateTreeTemplate ttmpl = nodeContext.getTreeTemplate();
		return ttmpl.getNodes().stream()
				.filter(nodeTmpl->nodeContext.getModuleName().equals(nodeTmpl.getModuleName()))
				.filter(nodeTmpl->{
					String selector = nodeTmpl.getSelector();
					return createSelector(selector).match(nodeContext);
				}).findFirst().orElse(null);
	}
	

	private NodeTemplateSelector createSelector(String selector) {
		return el->true;
	}
	
	@Override
	public TemplateTreeNode getNodeTemplate(String moduleName, Long nodeId) {
		List<TemplateTreeTemplate> tmpls = getManager().queryByModule(moduleName);
		for (TemplateTreeTemplate tmpl : tmpls) {
			List<TemplateTreeNode> nodes = tmpl.getNodes();
			if(nodes != null) {
				for (TemplateTreeNode node : nodes) {
					if(nodeId.longValue() == node.getId()) {
						return node;
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public TreeRelationComposite getNodeRelationTemplate(String moduleName, Long nodeRelationTemplateId) {
		List<TemplateTreeTemplate> tmpls = getManager().queryByModule(moduleName);
		for (TemplateTreeTemplate tmpl : tmpls) {
			List<TemplateTreeNode> nodes = tmpl.getNodes();
			if(nodes != null) {
				for (TemplateTreeNode node : nodes) {
					List<TemplateTreeRelation> rels = node.getRelations();
					if(rels != null) {
						for (TemplateTreeRelation rel : rels) {
							if(nodeRelationTemplateId.longValue() == rel.getId()) {
								return new TreeRelationComposite(tmpl, node, rel);
							}
						}
					}
				}
			}
		}
		return null;
	}
	
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

	
	@Override
	public String generateNodesCSS(TemplateTreeTemplate ttmpl) {
		List<TemplateTreeNode> nodes = ttmpl.getNodes();
		StringBuffer css = new StringBuffer();
		ttmpl.getDefaultNodeColor();
		writeDefaultNodeCSS(ttmpl, css);
		for (TemplateTreeNode node : nodes) {
			writeNodeCSS(node, css);
		}
		return css.toString();
	}
	
	private void writeDefaultNodeCSS(TemplateTreeTemplate ttmpl, StringBuffer css) {
		String liCss = ".cpf-tree-id-" + ttmpl.getId();
		css.append(liCss + " li>a>b{");
		css.append("background-color:" + ttmpl.getDefaultNodeColor() + ";");
		css.append("}");
	}


	private void writeNodeCSS(TemplateTreeNode node, StringBuffer css) {
		String liClass = "li.tree-node-id-" + node.getId();
		css.append(liClass + ">a>b{");
		css.append("background-color:" + node.getNodeColor() + ";");
		css.append("}");
	}
	

	
	

	@Override
	public Long copy(Long tmplId, String targetReference) {
		// TODO Auto-generated method stub
		return null;
	}

}
