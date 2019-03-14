package cn.sowell.dataserver.model.modules.service.view;

import java.util.List;

import cn.sowell.datacenter.entityResolver.CEntityPropertyParser;

public class EntityQuery {
	private String moduleName;
	private String parentEntityCode;
	private String relationName;
	private Integer pageSize;
	public String getModuleName() {
		return moduleName;
	}
	public EntityQuery setModuleName(String moduleName) {
		this.moduleName = moduleName;
		return this;
	}
	public String getParentEntityCode() {
		return parentEntityCode;
	}
	public EntityQuery setParentEntityCode(String parentEntityCode) {
		this.parentEntityCode = parentEntityCode;
		return this;
	}
	public String getRelationName() {
		return relationName;
	}
	public EntityQuery setRelationName(String relationName) {
		this.relationName = relationName;
		return this;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public EntityQuery setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}
	public List<CEntityPropertyParser> list() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
