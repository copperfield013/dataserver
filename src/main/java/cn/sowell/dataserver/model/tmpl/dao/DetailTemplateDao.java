package cn.sowell.dataserver.model.tmpl.dao;

import java.util.List;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroupTreeNode;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;

public interface DetailTemplateDao extends OpenDetailTemplateDao<TemplateDetailTemplate, TemplateDetailFieldGroup, TemplateDetailField>{

	TemplateDetailTemplate getDetailTemplateByGroupId(Long templateGroupId);

	List<TemplateDetailFieldGroupTreeNode> queryAllFieldGroupTreeNodes();

	List<TemplateDetailFieldGroupTreeNode> queryFieldGroupTreeNodes(Long dtmplId);
}
