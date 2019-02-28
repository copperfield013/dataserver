package cn.sowell.dataserver.model.modules.service;

import cn.sowell.datacenter.entityResolver.ModuleConfigStructure;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;

public interface ModulesService {

	/**
	 * 根据模块名获得模块数据
	 * @param moduleKey
	 * @return
	 */
	ModuleMeta getModule(String moduleName);

	/**
	 * 
	 * @param moduleName
	 * @return
	 */
	ModuleMeta getStatModule(String moduleName);
	

	boolean getModuleEntityWritable(String moduleName);

	ModuleMeta getCompositeRelatedModule(String moduleName, Long compositeId);

	ModuleConfigStructure getModuleConfigStructure(String moduleName);

}
