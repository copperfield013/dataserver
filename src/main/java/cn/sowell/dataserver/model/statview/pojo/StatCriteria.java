package cn.sowell.dataserver.model.statview.pojo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatList;

public class StatCriteria {
	private TemplateStatList statListTemplate;
	private UserIdentifier user;
	private PageInfo pageInfo;
	private Set<Long> disabledColumnIds = new HashSet<>();
	
	private Map<Long, String> reqCriteriaMap;
	
	public TemplateStatList getStatListTemplate() {
		return statListTemplate;
	}
	public void setStatListTemplate(TemplateStatList statListTemplate) {
		this.statListTemplate = statListTemplate;
	}
	public UserIdentifier getUser() {
		return user;
	}
	public void setUser(UserIdentifier user) {
		this.user = user;
	}
	public PageInfo getPageInfo() {
		return pageInfo;
	}
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	public Set<Long> getDisabledColumnIds() {
		return disabledColumnIds;
	}
	public void setDisabledColumnIds(Set<Long> disabledColumnIds) {
		this.disabledColumnIds = disabledColumnIds;
	}
	public Map<Long, String> getReqCriteriaMap() {
		return reqCriteriaMap;
	}
	public void setReqCriteriaMap(Map<Long, String> reqCriteriaMap) {
		this.reqCriteriaMap = reqCriteriaMap;
	}
}
