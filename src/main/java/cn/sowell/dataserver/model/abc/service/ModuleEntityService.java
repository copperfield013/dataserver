package cn.sowell.dataserver.model.abc.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.abc.mapping.entity.Entity;

import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.RelationEntityPropertyParser;
import cn.sowell.dataserver.model.modules.bean.EntityPagingIterator;
import cn.sowell.dataserver.model.modules.bean.EntityPagingQueryProxy;
import cn.sowell.dataserver.model.modules.bean.ExportDataPageInfo;
import cn.sowell.dataserver.model.modules.pojo.EntityHistoryItem;

public interface ModuleEntityService {
	/**********************************************************
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 查询单体 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 **********************************************************/
	
	/**
	 * 
	 * @param queryParam
	 * @return
	 */
	Entity getEntity(EntityQueryParameter queryParam);
	
	/**
	 * 根据code获得实体
	 * @param queryParam
	 * @return
	 */
	ModuleEntityPropertyParser getEntityParser(EntityQueryParameter queryParam);

	/**
	 * 根据code和历史标识获得实体
	 * @param queryParam
	 * @param historyId
	 * @return
	 */
	ModuleEntityPropertyParser getHistoryEntityParser(EntityQueryParameter queryParam, Long historyId,
			Date historyTime);
	
	
	
	/**********************************************************
	 * ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 查询单体 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	 **********************************************************/
	
	
	/**********************************************************
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 查询关系单体 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 **********************************************************/
	
	/**
	 * 
	 * @param entityQueryParam
	 * @param relationName
	 * @return
	 */
	Entity getModuleRelationEntity(EntityQueryParameter entityQueryParam);
	
	/**
	 * 
	 * @param param
	 * @return
	 */
	RelationEntityPropertyParser getRelationEntityParser(EntityQueryParameter param);
	
	
	ModuleEntityPropertyParser toEntityParser(Entity entity, EntityParserParameter parameter);
	
	/**********************************************************
	 * ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 查询关系单体 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	 **********************************************************/
	
	
	/**********************************************************
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 查询实体列表 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 **********************************************************/
	
	List<Entity> queryModuleEntities(EntitiesQueryParameter param);
	
	/**
	 * 
	 * @param param
	 * @param ePageInfo
	 * @return
	 */
	EntityPagingQueryProxy getEntityExportQueryProxy(EntitiesQueryParameter param, ExportDataPageInfo ePageInfo);
	
	
	/*EntityPagingIterator queryExportIterator(TemplateListTemplate ltmpl, Set<NormalCriteria> nCriterias,
			ExportDataPageInfo ePageInfo, UserIdentifier user);*/
	
	EntityPagingIterator queryExportIterator(EntitiesQueryParameter param, ExportDataPageInfo ePageInfo);
	
	/**
	 * 
	 * @param param
	 * @param relationName
	 * @return
	 */
	Map<String, RelationEntityPropertyParser> queryRelationEntityParsers(EntitiesQueryParameter param, String relationName);
	
	/**********************************************************
	 * ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 查询实体列表 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	 **********************************************************/
	
	
	/**********************************************************
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 查询历史 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 **********************************************************/
	
	List<EntityHistoryItem> queryHistory(EntityQueryParameter param, Integer pageNo, Integer pageSize);

	EntityHistoryItem getLastHistoryItem(EntityQueryParameter param);
	
	/**********************************************************
	 * ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 查询历史 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	 **********************************************************/
	
	
	
	/**
	 * 
	 * @param param
	 * @param entityMap
	 * @return
	 */
	String fuseEntity(EntityQueryParameter param, Map<String, Object> entityMap);

	String mergeEntity(EntityQueryParameter param, Map<String, Object> entityMap);

	void delete(EntityQueryParameter param);
	
	void remove(EntitiesQueryParameter param);

	


	

	
}
