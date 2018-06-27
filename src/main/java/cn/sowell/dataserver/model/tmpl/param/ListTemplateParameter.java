package cn.sowell.dataserver.model.tmpl.param;

import java.util.Map;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTempalte;

public class ListTemplateParameter {
	private TemplateListTempalte listTemplate;
	private Map<Long, NormalCriteria> normalCriteriaMap;
	private UserIdentifier user;
	public TemplateListTempalte getListTemplate() {
		return listTemplate;
	}
	public void setListTemplate(TemplateListTempalte listTemplate) {
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
