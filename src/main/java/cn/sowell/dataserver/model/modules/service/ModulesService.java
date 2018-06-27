package cn.sowell.dataserver.model.modules.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.MutablePropertyValues;

import com.abc.query.criteria.Criteria;

import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.dataserver.model.modules.bean.EntityPagingIterator;
import cn.sowell.dataserver.model.modules.bean.ExportDataPageInfo;
import cn.sowell.dataserver.model.modules.pojo.EntityHistoryItem;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;
import cn.sowell.dataserver.model.tmpl.bean.QueryEntityParameter;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

public interface ModulesService {

	List<ModuleEntityPropertyParser> queryEntities(QueryEntityParameter param);
	
	List<Criteria> toCriterias(Collection<NormalCriteria> nCriterias, String module);
	
	Map<Long, NormalCriteria> getCriteriasFromRequest(
			MutablePropertyValues pvs,
			Map<Long, TemplateListCriteria> criteriaMap);

	/**
	 * 根据模块名获得模块数据
	 * @param moduleKey
	 * @return
	 */
	ModuleMeta getModule(String moduleName);

	/**
	 * 根据模块和id以及历史时间获得该时间的实体数据
	 * @param module
	 * @param code
	 * @param date
	 * @return
	 */
	ModuleEntityPropertyParser getEntity(String module, String code, Date date);

	/**
	 * 分页查询实体信息的历史
	 * @param module
	 * @param code
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	List<EntityHistoryItem> queryHistory(String module, String code, Integer pageNo, Integer pageSize);

	/**
	 * 删除实体
	 * @param module
	 * @param code
	 */
	void deleteEntity(String code);

	/**
	 * 保存实体（创建或更新）
	 * @param module
	 * @param map
	 * @return 
	 */
	String mergeEntity(String module, Map<String, Object> map);

	String fuseEntity(String module, Map<String, Object> map);
	
	/**
	 * 根据条件查找列表迭代器
	 * @param ltmpl
	 * @param criteria
	 * @param ePageInfo
	 * @return
	 */
	EntityPagingIterator queryIterator(TemplateListTemplate ltmpl, Set<NormalCriteria> criteria,
			ExportDataPageInfo ePageInfo);

	

	

}
