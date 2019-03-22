package cn.sowell.dataserver.model.abc.service;

import cn.sowell.copframe.common.UserIdentifier;

public class AbstractPureEntityQueryParameter {
	private String moduleName;
	private String relationName;
	private UserIdentifier user;
	public AbstractPureEntityQueryParameter(String moduleName, UserIdentifier user) {
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
	public String getRelationName() {
		return relationName;
	}
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	public UserIdentifier getUser() {
		return user;
	}
	public void setUser(UserIdentifier user) {
		this.user = user;
	}
	
}
