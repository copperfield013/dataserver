package cn.sowell.dataserver.model.abc.service;

import cn.sowell.copframe.common.UserIdentifier;

public class EntityQueryParameter extends AbstractEntityQueryParameter {
	private String entityCode;
	public EntityQueryParameter(String moduleName, UserIdentifier user) {
		super(moduleName, user);
	}
	public EntityQueryParameter(String moduleName, String entityCode, UserIdentifier user) {
		super(moduleName, user);
		this.entityCode = entityCode;
	}
	public EntityQueryParameter(String moduleName, String entityCode, String relationName, UserIdentifier user) {
		super(moduleName, user);
		this.entityCode = entityCode;
		this.setRelationName(relationName);
	}
	public String getEntityCode() {
		return entityCode;
	}
	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}
}
