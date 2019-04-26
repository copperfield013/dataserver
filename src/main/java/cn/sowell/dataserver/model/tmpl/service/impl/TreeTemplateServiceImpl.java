package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.dataserver.model.modules.service.view.TreeNodeContext;
import cn.sowell.dataserver.model.tmpl.bean.NodeTemplateSelector;
import cn.sowell.dataserver.model.tmpl.bean.TreeNodeStyle;
import cn.sowell.dataserver.model.tmpl.bean.TreeStyle;
import cn.sowell.dataserver.model.tmpl.manager.TreeTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.TreeTemplateManager.TreeRelationComposite;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeNode;
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
				matcher.appendReplacement(buffer, FormatUtils.coalesce(propertyValue, ""));
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
					//匹配根节点
					if(nodeContext.isRoot() && nodeTmpl.getIsRootNode() != null && 1 == nodeTmpl.getIsRootNode()) {
						return true;
					}
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
	public TreeRelationComposite getNodeRelationTemplate(Long nodeRelationTemplateId) {
		return getManager().getNodeRelationTemplate(nodeRelationTemplateId);
	}
	
	
	@Override
	public TreeStyle getTreeNodeStyle(TemplateTreeTemplate ttmpl) {
		if(ttmpl != null) {
			TreeStyle style = new TreeStyle();
			style.setDefaultNodeColor(ttmpl.getDefaultNodeColor());
			if(ttmpl.getNodes() != null) {
				for (TemplateTreeNode node : ttmpl.getNodes()) {
					TreeNodeStyle nodeStyle = new TreeNodeStyle();
					nodeStyle.setNodeId(node.getId());
					nodeStyle.setNodeColor(node.getNodeColor());
					style.getNodeStyles().add(nodeStyle);
				}
			}
			return style;
		}
		return null;
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
	
	@Override
	public List<TemplateTreeTemplate> queryByNodeModule(String nodeModule) {
		return getManager().queryByNodeModule(nodeModule);
	}


	@Override
	public TemplateTreeNode getDefaultNodeTemplate(TemplateTreeTemplate ttmpl) {
		//构造根节点的上下文
		TreeNodeContext nodeContext = new TreeNodeContext(ttmpl);
		//根据上下文获得节点模板
		return analyzeNodeTemplate(nodeContext);
	}

}
