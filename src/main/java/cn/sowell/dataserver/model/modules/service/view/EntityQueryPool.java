package cn.sowell.dataserver.model.modules.service.view;

import java.util.HashMap;
import java.util.Map;

import cn.sowell.copframe.common.UserIdentifier;

public class EntityQueryPool {
	private UserIdentifier user;
	
	Map<String, EntityQuery> queryMap = new HashMap<>();
	
	public EntityQueryPool(UserIdentifier user) {
		super();
		this.user = user;
	}

	public EntityQuery regist() {
		EntityQuery query = new EntityQuery(user);
		queryMap.put(query.getKey(), query);
		return query;
	}

	public EntityQuery getQuery(String key) {
		return queryMap.get(key);
	}

}
