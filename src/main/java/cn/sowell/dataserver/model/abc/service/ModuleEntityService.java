package cn.sowell.dataserver.model.abc.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.abc.mapping.entity.Entity;
import com.abc.mapping.entity.RecordEntity;
import com.abc.rrc.query.entity.RelationEntitySPQuery;
import com.abc.rrc.query.entity.SortedPagedQuery;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.RabcModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.RelSelectionEntityPropertyParser;
import cn.sowell.dataserver.model.modules.bean.EntityPagingIterator;
import cn.sowell.dataserver.model.modules.bean.EntityPagingQueryProxy;
import cn.sowell.dataserver.model.modules.bean.ExportDataPageInfo;
import cn.sowell.dataserver.model.modules.pojo.EntityVersionItem;
import cn.sowell.dataserver.model.modules.service.view.EntityItem;
import cn.sowell.dataserver.model.modules.service.view.EntityQuery;
import cn.sowell.dataserver.model.modules.service.view.PagedEntityList;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;

public interface ModuleEntityService {
	/**********************************************************
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 查询单体 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 **********************************************************/
	
	/**
	 * 
	 * @param queryParam
	 * @return
	 */
	RecordEntity getEntity(EntityQueryParameter queryParam);
	
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
	ModuleEntityPropertyParser getHistoryEntityParser(EntityQueryParameter queryParam, String versionCode,
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
	RecordEntity getModuleRelationEntity(EntityQueryParameter entityQueryParam);
	
	/**
	 * 
	 * @param param
	 * @return
	 */
	RelSelectionEntityPropertyParser getRelationEntityParser(EntityQueryParameter param);
	
	
	ModuleEntityPropertyParser toEntityParser(RecordEntity entity, EntityParserParameter parameter);
	
	RelSelectionEntityPropertyParser toRelationParser(RecordEntity entity, EntityParserParameter parameter);
	
	RabcModuleEntityPropertyParser toRabcEntityParser(RecordEntity entity, EntityParserParameter parameter);
	
	List<EntityItem> convertEntityItems(PagedEntityList el);
	
	/**********************************************************
	 * ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 查询关系单体 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	 **********************************************************/
	
	
	/**********************************************************
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 查询实体列表 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 **********************************************************/
	
	SortedPagedQuery<Entity> getNormalSortedEntitiesQuery(EntitiesQueryParameter param);
	
	SortedPagedQuery<RecordEntity> getQuickSortedEntitiesQuery(EntitiesQueryParameter queryParam);
	
	List<RecordEntity> queryModuleEntities(EntitiesQueryParameter param);
	
	RelationEntitySPQuery getRabcEntitiesQuery(RelationEntitiesQueryParameter queryParam);
	
	SortedPagedQuery<Entity> getSelectionEntitiesQuery(SelectionEntityQueyrParameter queryParam);
	
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
	
	List<EntityVersionItem> queryHistory(EntityQueryParameter param, Integer pageNo, Integer pageSize);

	EntityVersionItem getLastHistoryItem(EntityQueryParameter param);
	
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

	void wrapSelectEntityQuery(EntityQuery query, TemplateDetailFieldGroup fieldGroup,
			Map<Long, String> requrestCriteriaMap);

	Map<String, RelSelectionEntityPropertyParser> loadEntities(Set<String> codeSet, TemplateDetailFieldGroup fieldGroup,
			UserIdentifier user);

	


}
