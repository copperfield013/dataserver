package cn.sowell.dataserver.model.tmpl.dao;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;

public interface DetailTemplateDao extends OpenDetailTemplateDao<TemplateDetailTemplate, TemplateDetailFieldGroup, TemplateDetailField>{

	TemplateDetailTemplate getDetailTemplateByGroupId(Long templateGroupId);
}
