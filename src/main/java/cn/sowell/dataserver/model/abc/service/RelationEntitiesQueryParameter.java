package cn.sowell.dataserver.model.abc.service;

import java.util.function.Consumer;

import com.abc.rrc.query.criteria.EntityCriteriaFactory;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.PageInfo;

public class RelationEntitiesQueryParameter {
	private String moduleName;
	private String parentEntityCode;
	private String relationName;
	private UserIdentifier user;
	private PageInfo pageInfo;
	private Consumer<EntityCriteriaFactory> criteriaFactoryConsumer;
	
	public RelationEntitiesQueryParameter(String moduleName, String relationName, String parentEntityCode, UserIdentifier user) {
		this.moduleName = moduleName;
		this.relationName = relationName;
		this.parentEntityCode = parentEntityCode;
		this.user = user;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public RelationEntitiesQueryParameter setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
		return this;
	}

	public String getModuleName() {
		return moduleName;
	}

	public String getParentEntityCode() {
		return parentEntityCode;
	}

	public String getRelationName() {
		return relationName;
	}

	public UserIdentifier getUser() {
		return user;
	}

	public Consumer<EntityCriteriaFactory> getCriteriaFactoryConsumer() {
		return criteriaFactoryConsumer;
	}

	public RelationEntitiesQueryParameter setCriteriaFactoryConsumer(Consumer<EntityCriteriaFactory> criteriaFactoryConsumer) {
		this.criteriaFactoryConsumer = criteriaFactoryConsumer;
		return this;
	}


}
