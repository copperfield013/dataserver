package cn.sowell.dataserver.model.abc.service;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import cho.carbon.query.entity.factory.EntityConJunctionFactory;
import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.copframe.utils.TextUtils;

public class RelationEntitiesQueryParameter {
	private String moduleName;
	private String parentEntityCode;
	private String relationName;
	private UserIdentifier user;
	private PageInfo pageInfo;
	private Consumer<EntityConJunctionFactory> criteriaFactoryConsumer;
	private Set<String> relationExcludeLabels = new HashSet<>();
	private Set<String> relationIncludeLabels = new HashSet<>();
	
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

	public Consumer<EntityConJunctionFactory> getCriteriaFactoryConsumer() {
		return criteriaFactoryConsumer;
	}

	public RelationEntitiesQueryParameter setCriteriaFactoryConsumer(Consumer<EntityConJunctionFactory> criteriaFactoryConsumer) {
		this.criteriaFactoryConsumer = criteriaFactoryConsumer;
		return this;
	}

	public void addRelationExcludeLabels(String filterLabels) {
		this.relationExcludeLabels.addAll(TextUtils.split(filterLabels, ","));
	}

	public void addRelationIncludeLabels(String filterLabels) {
		this.relationIncludeLabels.addAll(TextUtils.split(filterLabels, ","));
	}
	
	public Set<String> getRelationExcludeLabels(){
		return this.relationExcludeLabels;
	}
	
	public Set<String> getRelationIncludeLabels(){
		return this.relationIncludeLabels;
	}
	


}
