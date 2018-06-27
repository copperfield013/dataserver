package cn.sowell.dataserver.model.abc.service;

import java.util.List;
import java.util.Map;

import com.abc.dto.ErrorInfomation;
import com.abc.mapping.entity.Entity;
import com.abc.query.criteria.Criteria;

import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
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
	List<EntityHistoryItem> queryHistory(String module, String code, Integer pageNo, Integer pageSize);


	/**
	 * 
	 * @param param
	 * @param errors
	 * @return
	 */
	Entity getHistoryEntity(QueryEntityParameter param, List<ErrorInfomation> errors);
	/**
	 * 根据模块名和code获得实体对象
	 * @param module
	 * @param code
	 * @return
	 */
	Entity getModuleEntity(String module, String code);

	/**
	 * 根据code删除实体
	 * @param code
	 */
	void delete(String code);

	/**
	 * 保存或者修改数据库中的实体对象
	 * @param module
	 * @param propMap
	 * @return
	 */
	String mergeEntity(String module, Map<String, Object> propMap);
	
	String fuseEntity(String module, Map<String, Object> map);
	
	/**
	 * 根据模块和code获得对应的的实体的转化对象
	 * @param module
	 * @param code
	 * @return
	 */
	ModuleEntityPropertyParser getModuleEntityParser(String module, String code);

	ModuleEntityPropertyParser getModuleEntityParser(String module, Entity entity);

	EntityPagingQueryProxy getModuleQueryProxy(String module, List<Criteria> cs, ExportDataPageInfo ePageInfo);


	


}
