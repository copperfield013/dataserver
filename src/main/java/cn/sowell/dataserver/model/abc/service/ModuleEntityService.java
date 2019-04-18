package cn.sowell.dataserver.model.abc.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.abc.mapping.entity.Entity;
import com.abc.rrc.query.entity.EntitySortedPagedQuery;
import com.abc.rrc.query.entity.RelationEntitySPQuery;

import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.RabcModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.RelSelectionEntityPropertyParser;
import cn.sowell.dataserver.model.modules.bean.EntityPagingIterator;
import cn.sowell.dataserver.model.modules.bean.EntityPagingQueryProxy;
import cn.sowell.dataserver.model.modules.bean.ExportDataPageInfo;
import cn.sowell.dataserver.model.modules.pojo.EntityHistoryItem;
import cn.sowell.dataserver.model.modules.service.view.EntityItem;
import cn.sowell.dataserver.model.modules.service.view.PagedEntityList;

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
	RelSelectionEntityPropertyParser getRelationEntityParser(EntityQueryParameter param);
	
	
	ModuleEntityPropertyParser toEntityParser(Entity entity, EntityParserParameter parameter);
	
	RelSelectionEntityPropertyParser toRelationParser(Entity entity, EntityParserParameter parameter);
	
	RabcModuleEntityPropertyParser toRabcEntityParser(Entity entity, EntityParserParameter parameter);
	
	List<EntityItem> convertEntityItems(PagedEntityList el);
	
	/**********************************************************
	 * ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 查询关系单体 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	 **********************************************************/
	
	
	/**********************************************************
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 查询实体列表 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 **********************************************************/
	
	EntitySortedPagedQuery getNormalSortedEntitiesQuery(EntitiesQueryParameter param);
	
	EntitySortedPagedQuery getQuickSortedEntitiesQuery(EntitiesQueryParameter queryParam);
	
	List<Entity> queryModuleEntities(EntitiesQueryParameter param);
	
	RelationEntitySPQuery getRelationEntitiesQuery(RelationEntitiesQueryParameter queryParam);
	
	EntitySortedPagedQuery getSelectionEntitiesQuery(SelectionEntityQueyrParameter queryParam);
	
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
	Map<String, RelSelectionEntityPropertyParser> queryRelationEntityParsers(EntitiesQueryParameter param);
	
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
