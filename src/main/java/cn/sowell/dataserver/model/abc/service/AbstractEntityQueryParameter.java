package cn.sowell.dataserver.model.abc.service;

import java.util.Collection;
import java.util.Map;

import com.abc.rrc.query.queryrecord.criteria.Criteria;

import cn.sowell.copframe.common.UserIdentifier;

public abstract class AbstractEntityQueryParameter {
	private String moduleName;
	private String relationName;
	private Map<String, Collection<Criteria>> criteriasMap;
	private UserIdentifier user;
	public AbstractEntityQueryParameter(String moduleName, UserIdentifier user) {
		super();
		this.moduleName = moduleName;
		this.user = user;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public Map<String, Collection<Criteria>> getCriteriasMap() {
		return criteriasMap;
	}
	public void setCriteriasMap(Map<String, Collection<Criteria>> criteriasMap) {
		this.criteriasMap = criteriasMap;
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
}
