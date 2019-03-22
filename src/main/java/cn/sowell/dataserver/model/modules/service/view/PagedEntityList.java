package cn.sowell.dataserver.model.modules.service.view;

import java.util.List;

import org.springframework.util.Assert;

import cn.sowell.datacenter.entityResolver.CEntityPropertyParser;

public class PagedEntityList {

	List<CEntityPropertyParser> parsers;
	private Integer pageNo;
	private boolean isEndList = false;
	private EntityQuery query;
	
	public PagedEntityList(EntityQuery entityQuery) {
		Assert.notNull(entityQuery);
		this.query = entityQuery;
	}

	void setParsers(List<CEntityPropertyParser> parsers) {
		this.parsers = parsers;
	}

	public List<CEntityPropertyParser> getParsers() {
		return parsers;
	}

	public boolean getIsEndList() {
		return isEndList;
	}

	void setIsEndList(boolean isEndList) {
		this.isEndList = isEndList;
	}

	public String getEntityModule() {
		return this.query.getModuleName();
	}

	public EntityQuery getQuery() {
		return query;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

}
