package cn.sowell.dataserver.model.modules.service.impl;

import java.util.HashMap;
import java.util.Map;

public class ListTemplateEntityViewCriteria extends EntityViewCriteria{
	private Long listTemplateId;
	private Long templateGroupId;
	private String templateGroupKey;
	private Map<Long, String> listTemplateCriteria = new HashMap<>();
	public Long getListTemplateId() {
		return listTemplateId;
	}
	public void setListTemplateId(Long listTemplateId) {
		this.listTemplateId = listTemplateId;
	}
	public Long getTemplateGroupId() {
		return templateGroupId;
	}
	public void setTemplateGroupId(Long templateGroupId) {
		this.templateGroupId = templateGroupId;
	}
	public Map<Long, String> getListTemplateCriteria() {
		return listTemplateCriteria;
	}
	public void setListTemplateCriteria(Map<Long, String> listTemplateCriteria) {
		this.listTemplateCriteria = listTemplateCriteria;
	}
	public String getTemplateGroupKey() {
		return templateGroupKey;
	}
	public void setTemplateGroupKey(String templateGroupKey) {
		this.templateGroupKey = templateGroupKey;
	}
}
