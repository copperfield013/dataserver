package cn.sowell.dataserver.model.modules.service.view;

public class ListTemplateEntityViewCriteria extends EntityViewCriteria{
	private Long listTemplateId;
	private Long templateGroupId;
	private String templateGroupKey;
	
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
	public String getTemplateGroupKey() {
		return templateGroupKey;
	}
	public void setTemplateGroupKey(String templateGroupKey) {
		this.templateGroupKey = templateGroupKey;
	}
}
