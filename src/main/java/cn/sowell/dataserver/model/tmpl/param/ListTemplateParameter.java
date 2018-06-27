package cn.sowell.dataserver.model.tmpl.param;

import java.util.Map;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

public class ListTemplateParameter {
	private TemplateListTemplate listTemplate;
	private Map<Long, NormalCriteria> normalCriteriaMap;
	private UserIdentifier user;
	public TemplateListTemplate getListTemplate() {
		return listTemplate;
	}
	public void setListTemplate(TemplateListTemplate listTemplate) {
		this.listTemplate = listTemplate;
	}
	public Map<Long, NormalCriteria> getNormalCriteriaMap() {
		return normalCriteriaMap;
	}
	public void setNormalCriteriaMap(Map<Long, NormalCriteria> normalCriteriaMap) {
		this.normalCriteriaMap = normalCriteriaMap;
	}
	public UserIdentifier getUser() {
		return user;
	}
	public void setUser(UserIdentifier user) {
		this.user = user;
	}
}
