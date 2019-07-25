package cn.sowell.dataserver.model.abc.service;

import java.util.Set;
import java.util.function.Consumer;

import cho.carbon.query.entity.factory.EntityConJunctionFactory;
import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.PageInfo;

public class SelectionEntityQueyrParameter extends AbstractPureEntityQueryParameter {
	private Set<String> entityCodes;
	private PageInfo pageInfo;
	private Consumer<EntityConJunctionFactory> conjunctionFactoryConsumer;
	public SelectionEntityQueyrParameter(String moduleName, String relationName, UserIdentifier user) {
		super(moduleName, user);
		setRelationName(relationName);
	}
	public PageInfo getPageInfo() {
		return pageInfo;
	}
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	public Set<String> getEntityCodes() {
		return entityCodes;
	}
	public void setEntityCodes(Set<String> entityCodes) {
		this.entityCodes = entityCodes;
	}
	public Consumer<EntityConJunctionFactory> getConjunctionFactoryConsumer() {
		return conjunctionFactoryConsumer;
	}
	public void setConjunctionFactoryConsumer(Consumer<EntityConJunctionFactory> conjunctionFactoryConsumer) {
		this.conjunctionFactoryConsumer = conjunctionFactoryConsumer;
	}

}
