package cn.sowell.dataserver.model.modules.service;

import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;

public interface ModulesService {

	//List<ModuleEntityPropertyParser> queryEntities(QueryEntityParameter param);
	
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
	
	/**
	 * 根据模块和id以及历史时间获得该时间的实体数据
	 * @param module
	 * @param code
	 * @param date
	 * @return
	 */
	//ModuleEntityPropertyParser getEntity(String module, String code, Date date, UserIdentifier user);

	/**
	 * 分页查询实体信息的历史
	 * @param module
	 * @param code
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	//List<EntityHistoryItem> queryHistory(String module, String code, Integer pageNo, Integer pageSize, UserIdentifier user);

	/**
	 * 删除实体
	 * @param module
	 * @param code
	 */
	//void deleteEntity(String moduleName, String code, UserIdentifier user);

	/**
	 * 保存实体（创建或更新）
	 * @param module
	 * @param map
	 * @return 
	 */
	//String mergeEntity(String module, Map<String, Object> map, UserIdentifier user);

	//String fuseEntity(String module, Map<String, Object> map, UserIdentifier user);
	
	/**
	 * 根据条件查找列表迭代器
	 * @param ltmpl
	 * @param criteria
	 * @param ePageInfo
	 * @param userIdentifier 
	 * @return
	 */
//	EntityPagingIterator queryIterator(TemplateListTemplate ltmpl, Set<NormalCriteria> criteria,
//			ExportDataPageInfo ePageInfo, UserIdentifier userIdentifier);

	

//	Map<String, CEntityPropertyParser> getEntityParsers(String moduleName, String relationName, Set<String> codes,
//			UserIdentifier user);
//
//	
//	ModuleEntityPropertyParser getHistoryEntityParser(String moduleName, String code, Long historyId,
//			UserIdentifier currentUser);

	//EntityHistoryItem getLastHistoryItem(String moduleName, String code, UserIdentifier user);

//	void removeEntities(String moduleName, Set<String> codes, UserIdentifier user);

	boolean getModuleEntityWritable(String moduleName);

	ModuleMeta getCompositeRelatedModule(String moduleName, Long compositeId);

}
