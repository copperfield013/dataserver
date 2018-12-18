package cn.sowell.dataserver.model.tmpl.service;

import java.util.Map;
import java.util.Set;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupAction;

public interface ActionTemplateService {

	int doAction(TemplateActionTemplate atmpl, Set<String> codes, boolean isTransaction, UserIdentifier currentUser);

	Map<String, Object> coverActionFields(TemplateGroupAction groupAction, Map<String, Object> map);

}
