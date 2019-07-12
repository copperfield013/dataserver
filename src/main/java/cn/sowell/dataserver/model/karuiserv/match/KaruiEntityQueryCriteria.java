package cn.sowell.dataserver.model.karuiserv.match;

import java.util.LinkedHashMap;
import java.util.Map;

public class KaruiEntityQueryCriteria {

	private Map<Long, String> requrestLtmplCriteriaMap = new LinkedHashMap<Long, String>();
	
	public Map<Long, String> getRequrestLtmplCriteriaMap() {
		return requrestLtmplCriteriaMap;
	}

	public void putRequrestLtmplCriteria(Long ltmplFieldId, String criteriaValue) {
		this.requrestLtmplCriteriaMap.put(ltmplFieldId, criteriaValue);
	}

}
