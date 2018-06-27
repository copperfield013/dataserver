package cn.sowell.dataserver.model.modules.bean;

import java.util.Set;

import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public class QueryEntityCriteria {
	private Set<NormalCriteria> normalCriterias;
	public Set<NormalCriteria> getNormalCriterias() {
		return normalCriterias;
	}
	public void setNormalCriterias(Set<NormalCriteria> normalCriterias) {
		this.normalCriterias = normalCriterias;
	}
	private PageInfo pageInfo;
	public PageInfo getPageInfo() {
		return pageInfo;
	}
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
}
