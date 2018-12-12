package cn.sowell.dataserver.model.abc.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.abc.mapping.entity.Entity;
import com.abc.rrc.query.queryrecord.criteria.Criteria;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.RelationEntityPropertyParser;
import cn.sowell.dataserver.model.modules.bean.EntityPagingQueryProxy;
import cn.sowell.dataserver.model.modules.bean.ExportDataPageInfo;
import cn.sowell.dataserver.model.modules.pojo.EntityHistoryItem;
import cn.sowell.dataserver.model.tmpl.bean.QueryEntityParameter;

public interface ABCExecuteService {

	/**
	 * 查询指定模块的
	 * @param param
	 * @return
	 */
	List<Entity> queryModuleEntities(QueryEntityParameter param);
	

	/**
	 * 查询人口的历史信息
	 * @param module
	 * @param code
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	List<EntityHistoryItem> queryHistory(String moduleName, String code, Integer pageNo, Integer pageSize,
			UserIdentifier user);


	/**
	 * 
	 * @param param
	 * @return
	 */
	ModuleEntityPropertyParser getHistoryEntityParser(QueryEntityParameter param, UserIdentifier user);
	/**
	 * 根据模块名和code获得实体对象
	 * @param module
	 * @param code
	 * @return
	 */
	Entity getModuleEntity(String moduleName, String code, UserIdentifier user);

	/**
	 * 根据code删除实体
	 * @param code
	 */
	void delete(String moduleName, String code, UserIdentifier user);
	
	/**
	 * 批量删除
	 * @param moduleName
	 * @param codes
	 * @param user
	 */
	void remove(String moduleName, Set<String> codes, UserIdentifier user);

	/**
	 * 保存或者修改数据库中的实体对象
	 * @param module
	 * @param propMap
	 * @return
	 */
	String mergeEntity(String module, Map<String, Object> propMap, UserIdentifier user);
	
	String fuseEntity(String module, Map<String, Object> map, UserIdentifier user);
	
	/**
	 * 根据模块和code获得对应的的实体的转化对象
	 * @param module
	 * @param code
	 * @return
	 */
	ModuleEntityPropertyParser getModuleEntityParser(String module, String code, UserIdentifier user);
	ModuleEntityPropertyParser getModuleEntityParser(String module, String code, UserIdentifier user, Object propertyGetterArgument);

	ModuleEntityPropertyParser getModuleEntityParser(String module, Entity entity, UserIdentifier user);
	ModuleEntityPropertyParser getModuleEntityParser(String module, Entity entity, UserIdentifier user, Object propertyGetterArgument);

	EntityPagingQueryProxy getModuleQueryProxy(String module, List<Criteria> cs, ExportDataPageInfo ePageInfo, UserIdentifier user);


	RelationEntityPropertyParser getRelationEntityParser(String moduleName, String relationName, Entity entity, UserIdentifier user);


	Entity getModuleRelationEntity(String moduleName, String relationName, String code, UserIdentifier user);


	RelationEntityPropertyParser getRelationEntityParser(String moduleName, String relationName, String code,
			UserIdentifier user);





}
