package cn.sowell.dataserver.model.modules.service;

import cn.sowell.dataserver.model.modules.service.impl.EntityView;
import cn.sowell.dataserver.model.modules.service.impl.EntityViewCriteria;

public interface ViewDataService {
	EntityView query(EntityViewCriteria criteria);
}
