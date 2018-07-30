package cn.sowell.dataserver.model.modules.bean;

import java.util.Set;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;

public interface EntityPagingQueryProxy {
	int getTotalCount();
	int getPageSize();
	Set<ModuleEntityPropertyParser> load(int pageNo, UserIdentifier user);
}
