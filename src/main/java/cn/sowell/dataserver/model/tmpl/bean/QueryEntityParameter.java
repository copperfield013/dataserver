package cn.sowell.dataserver.model.tmpl.bean;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.PageInfo;

public class QueryEntityParameter {
	private List<Criteria> criterias;
	private PageInfo pageInfo;
	private String module;
	private String code;
	private String relationName;
	private Date historyTime;
	private Long historyId;
	private UserIdentifier user;
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public PageInfo getPageInfo() {
		return pageInfo;
	}
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	public List<Criteria> getCriterias() {
		return criterias;
	}
	public void setCriterias(List<Criteria> criterias) {
		this.criterias = criterias;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Date getHistoryTime() {
		return historyTime;
	}
	public void setHistoryTime(Date historyTime) {
		this.historyTime = historyTime;
	}
	public UserIdentifier getUser() {
		return user;
	}
	public void setUser(UserIdentifier user) {
		this.user = user;
	}
	public String getRelationName() {
		return relationName;
	}
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	public Long getHistoryId() {
		return historyId;
	}
	public void setHistoryId(Long historyId) {
		this.historyId = historyId;
	}
	
	
	
}
