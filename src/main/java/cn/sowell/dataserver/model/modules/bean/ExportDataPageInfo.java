package cn.sowell.dataserver.model.modules.bean;

import cn.sowell.copframe.dto.page.PageInfo;

public class ExportDataPageInfo {
	private PageInfo pageInfo;
	private Integer rangeStart;
	private Integer rangeEnd;
	private String scope;
	//每次查询的条数
	private Integer queryCacheCount = 30;
	public PageInfo getPageInfo() {
		return pageInfo;
	}
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	public Integer getRangeStart() {
		return rangeStart;
	}
	public void setRangeStart(Integer rangeStart) {
		this.rangeStart = rangeStart;
	}
	public Integer getRangeEnd() {
		return rangeEnd;
	}
	public void setRangeEnd(Integer rangeEnd) {
		this.rangeEnd = rangeEnd;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public Integer getQueryCacheCount() {
		return queryCacheCount;
	}
	public void setQueryCacheCount(Integer queryCacheCount) {
		this.queryCacheCount = queryCacheCount;
	}
	
}
