package cn.sowell.dataserver.model.abc.service;

import java.util.List;
import java.util.Set;

import com.abc.rrc.query.queryrecord.criteria.Criteria;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.PageInfo;

public class EntitiesQueryParameter extends AbstractEntityQueryParameter{
	private Set<String> entityCodes;
	private List<Criteria> mainCriterias;
	private PageInfo pageInfo;
	public EntitiesQueryParameter(String moduleName, UserIdentifier user) {
		super(moduleName, user);
	}
	public EntitiesQueryParameter(String moduleName, UserIdentifier user, List<Criteria> mainCriterias) {
		super(moduleName, user);
		this.mainCriterias = mainCriterias;
	}
	public List<Criteria> getMainCriterias() {
		return mainCriterias;
	}
	public void setMainCriterias(List<Criteria> mainCriterias) {
		this.mainCriterias = mainCriterias;
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
}
