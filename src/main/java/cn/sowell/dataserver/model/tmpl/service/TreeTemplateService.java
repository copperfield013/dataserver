package cn.sowell.dataserver.model.tmpl.service;

import java.util.List;

import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.dataserver.model.modules.service.view.TreeNodeContext;
import cn.sowell.dataserver.model.tmpl.duplicator.ModuleTemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeNode;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeTemplate;
import cn.sowell.dataserver.model.tmpl.service.impl.TreeTemplateServiceImpl.TreeRelationComposite;

public interface TreeTemplateService extends OpenTemplateService<TemplateTreeTemplate>, ModuleTemplateDuplicator{
	
	TreeRelationComposite getNodeRelationTemplate(String moduleName, Long nodeRelationTemplateId);

	TemplateTreeNode analyzeNodeTemplate(TreeNodeContext nodeContext);

	String generateNodesCSS(TemplateTreeTemplate ttmpl);

	String parserNodeText(String text, ModuleEntityPropertyParser parser);

	TemplateTreeNode getNodeTemplate(String moduleName, Long nodeId);

	List<TemplateTreeTemplate> queryByNodeModule(String nodeModule);
	
}
