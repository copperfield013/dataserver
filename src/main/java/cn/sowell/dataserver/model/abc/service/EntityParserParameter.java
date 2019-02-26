package cn.sowell.dataserver.model.abc.service;

import cn.sowell.copframe.common.UserIdentifier;

public class EntityParserParameter {
	private String moduleName;
	private UserIdentifier user;
	private Object propertyGetterArgument;
	private String relationName;
	public EntityParserParameter(String moduleName, UserIdentifier user) {
		super();
		this.moduleName = moduleName;
		this.user = user;
	}
	public EntityParserParameter(String moduleName, UserIdentifier user, Object propertyGetterArgument) {
		super();
		this.moduleName = moduleName;
		this.user = user;
		this.propertyGetterArgument = propertyGetterArgument;
	}
	public EntityParserParameter(String moduleName, String relationName,  UserIdentifier user,
			Object propertyGetterArgument) {
		super();
		this.relationName = relationName;
		this.moduleName = moduleName;
		this.user = user;
		this.propertyGetterArgument = propertyGetterArgument;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public UserIdentifier getUser() {
		return user;
	}
	public void setUser(UserIdentifier user) {
		this.user = user;
	}
	public Object getPropertyGetterArgument() {
		return propertyGetterArgument;
	}
	public void setPropertyGetterArgument(Object propertyGetterArgument) {
		this.propertyGetterArgument = propertyGetterArgument;
	}
	public String getRelationName() {
		return relationName;
	}
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	
}
