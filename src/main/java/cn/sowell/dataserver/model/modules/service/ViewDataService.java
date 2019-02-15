package cn.sowell.dataserver.model.modules.service;

import cn.sowell.dataserver.model.modules.service.view.EntityView;
import cn.sowell.dataserver.model.modules.service.view.EntityViewCriteria;

public interface ViewDataService {
	EntityView query(EntityViewCriteria criteria);
}
