package cn.sowell.dataserver.model.modules.service;

import cn.sowell.datacenter.entityResolver.config.ModuleConfigStructure;
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

	ModuleMeta getCompositeRelatedModule(String moduleName, Integer compositeId);

	ModuleConfigStructure getModuleConfigStructure(String moduleName);
	/**
	 * 根据模块和关系名，获得关系对应的模块名（关系必须有配置RabcNode）
	 * @param moduleName
	 * @param relationName
	 * @return
	 */
	ModuleMeta getRelationModule(String moduleName, String relationName);

	

}
