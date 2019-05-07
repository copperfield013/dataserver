package cn.sowell.dataserver.model.abc.service;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.abc.rrc.query.criteria.EntityCriteriaFactory;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public class EntitiesQueryParameter extends AbstractEntityQueryParameter{
	private Set<String> entityCodes;
	private PageInfo pageInfo;
	private Consumer<EntityCriteriaFactory> criteriaFactoryConsumer;
	private List<NormalCriteria> statNormalCriterias;
	private Set<String> statDimensions;
	public EntitiesQueryParameter(String moduleName, UserIdentifier user) {
		super(moduleName, user);
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
	public Consumer<EntityCriteriaFactory> getCriteriaFactoryConsumer() {
		return criteriaFactoryConsumer;
	}
	public void setCriteriaFactoryConsumer(Consumer<EntityCriteriaFactory> criteriaFactoryConsumer) {
		this.criteriaFactoryConsumer = criteriaFactoryConsumer;
	}
	public List<NormalCriteria> getStatNormalCriterias() {
		return this.statNormalCriterias;
	}
	public void setStatNormalCriterias(List<NormalCriteria> statNormalCriterias) {
		this.statNormalCriterias = statNormalCriterias;
	}
	public Set<String> getStatDimensions() {
		return this.statDimensions;
	}
	public void setStatDimensions(Set<String> statDimensions) {
		this.statDimensions = statDimensions;
	}
}
