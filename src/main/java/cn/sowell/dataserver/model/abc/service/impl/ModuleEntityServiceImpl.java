package cn.sowell.dataserver.model.abc.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.abc.application.BizFusionContext;
import com.abc.auth.pojo.UserInfo;
import com.abc.auth.service.ServiceFactory;
import com.abc.dto.ErrorInfomation;
import com.abc.extface.dto.RecordHistory;
import com.abc.mapping.entity.Entity;
import com.abc.panel.Discoverer;
import com.abc.panel.PanelFactory;
import com.abc.record.HistoryTracker;
import com.abc.rrc.query.entity.EntitySortedPagedQuery;

import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.ABCNodeProxy;
import cn.sowell.datacenter.entityResolver.impl.RelationEntityPropertyParser;
import cn.sowell.dataserver.model.abc.service.EntitiesQueryParameter;
import cn.sowell.dataserver.model.abc.service.EntityParserParameter;
import cn.sowell.dataserver.model.abc.service.EntityQueryParameter;
import cn.sowell.dataserver.model.abc.service.ModuleEntityService;
import cn.sowell.dataserver.model.modules.bean.EntityPagingIterator;
import cn.sowell.dataserver.model.modules.bean.EntityPagingQueryProxy;
import cn.sowell.dataserver.model.modules.bean.EntityQueryAdapter;
import cn.sowell.dataserver.model.modules.bean.ExportDataPageInfo;
import cn.sowell.dataserver.model.modules.pojo.EntityHistoryItem;
import cn.sowell.dataserver.model.tmpl.service.ListCriteriaFactory;

@Service
public class ModuleEntityServiceImpl implements ModuleEntityService {
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	static Logger logger = Logger.getLogger(ModuleEntityServiceImpl.class);
	
	@Resource
	ListCriteriaFactory lcriteriaFactory;
	
	@Override
	public Entity getEntity(EntityQueryParameter queryParam){
		BizFusionContext context = fFactory.getModuleConfig(queryParam.getModuleName()).getCurrentContext(queryParam.getUser());
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		Entity result=discoverer.discover(queryParam.getEntityCode(), queryParam.getCriteriasMap());
		return result;
	}
	
	@Override
	public ModuleEntityPropertyParser getEntityParser(EntityQueryParameter queryParam) {
		Entity entity = getEntity(queryParam);
		if(entity != null) {
			return toEntityParser(entity, new EntityParserParameter(queryParam.getModuleName(), queryParam.getUser()));
		}
		return null;
	}
	
	
	
	@Override
	public ModuleEntityPropertyParser toEntityParser(Entity entity, EntityParserParameter parameter) {
		return fFactory.getModuleResolver(parameter.getModuleName()).createParser(entity, parameter.getUser(), parameter.getPropertyGetterArgument());
	}
	
	@Override
	public RelationEntityPropertyParser toRelationParser(Entity entity, EntityParserParameter parameter) {
		return fFactory.getModuleResolver(parameter.getModuleName()).createRelationParser(entity, parameter.getRelationName(), parameter.getUser());
	}

	@Override
	public ModuleEntityPropertyParser getHistoryEntityParser(EntityQueryParameter queryParam, Long historyId, Date historyTime) {
		BizFusionContext context = fFactory.getModuleConfig(queryParam.getModuleName()).getCurrentContext(queryParam.getUser());
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		HistoryTracker tracker = null;
		if(historyId != null) {
			tracker = discoverer.track(BigInteger.valueOf(historyId));
		}else if(queryParam.getEntityCode() != null) {
			tracker = discoverer.track(queryParam.getEntityCode(), historyTime);
		}else {
			logger.error("historyId以及entityCode不能都为null！！！");
		}
		return trackEntityParser(discoverer, tracker, queryParam);
		
	}

	private ModuleEntityPropertyParser trackEntityParser(Discoverer discoverer, HistoryTracker tracker, EntityQueryParameter queryParam) {
		if(tracker != null) {
			Entity entity = tracker.getEntity();
			List<ErrorInfomation> errors = tracker.getErrorInfomations();
			ModuleEntityPropertyParser parser = toEntityParser(entity, new EntityParserParameter(queryParam.getModuleName(), queryParam.getUser(), discoverer));
			parser.setErrors(errors);
			return parser;
		}else {
			return null;
		}
	}
	
	@Override
	public EntityPagingIterator queryExportIterator(EntitiesQueryParameter param, ExportDataPageInfo ePageInfo) {
		//String moduleName = param.getModuleName();
		PageInfo pageInfo = ePageInfo.getPageInfo();
		//BizFusionContext context = fFactory.getModuleConfig(moduleName).getCurrentContext(param.getUser());
		//EntityCriteriaFactory cf = lcriteriaFactory.appendCriterias(param.getMainCriterias(), moduleName, context);
		EntityPagingQueryProxy proxy = getEntityExportQueryProxy(param, ePageInfo);
		int dataCount = pageInfo.getPageSize();
		int startPageNo = pageInfo.getPageNo();
		int totalCount = proxy.getTotalCount();
		int ignoreCount = 0;
		if(totalCount < pageInfo.getPageSize()) {
			dataCount = totalCount;
			startPageNo = 1;
		}
		if("all".equals(ePageInfo.getScope())){
			dataCount = proxy.getTotalCount();
			startPageNo = 1;
			if(ePageInfo.getRangeStart() != null){
				ignoreCount = ePageInfo.getRangeStart() - 1;
				if(ePageInfo.getRangeEnd() != null && ePageInfo.getRangeEnd() < dataCount){
					dataCount = ePageInfo.getRangeEnd() - ePageInfo.getRangeStart() + 1;
				}else{
					dataCount -= ePageInfo.getRangeStart() - 1;
				}
			}else if(ePageInfo.getRangeEnd() != null && ePageInfo.getRangeEnd() < dataCount){
				dataCount = ePageInfo.getRangeEnd();
			}
		}
		return new EntityPagingIterator(totalCount, dataCount, ignoreCount, startPageNo, param.getUser(), proxy);
	}
	
	/*@Override
	public EntityPagingIterator queryExportIterator(TemplateListTemplate ltmpl, Set<NormalCriteria> nCriterias,
			ExportDataPageInfo ePageInfo, UserIdentifier user) {
		String moduleName = ltmpl.getModule();
		PageInfo pageInfo = ePageInfo.getPageInfo();
		BizFusionContext context = fFactory.getModuleConfig(moduleName).getCurrentContext(user);
		EntityCriteriaFactory cf = lcriteriaFactory.appendCriterias(nCriterias, ltmpl.getModule(), context);
		EntitiesQueryParameter param = new EntitiesQueryParameter(moduleName, user, cf.getCriterias());
		EntityPagingQueryProxy proxy = getEntityExportQueryProxy(param, ePageInfo);
		int dataCount = pageInfo.getPageSize();
		int startPageNo = pageInfo.getPageNo();
		int totalCount = proxy.getTotalCount();
		int ignoreCount = 0;
		if(totalCount < pageInfo.getPageSize()) {
			dataCount = totalCount;
			startPageNo = 1;
		}
		if("all".equals(ePageInfo.getScope())){
			dataCount = proxy.getTotalCount();
			startPageNo = 1;
			if(ePageInfo.getRangeStart() != null){
				ignoreCount = ePageInfo.getRangeStart() - 1;
				if(ePageInfo.getRangeEnd() != null && ePageInfo.getRangeEnd() < dataCount){
					dataCount = ePageInfo.getRangeEnd() - ePageInfo.getRangeStart() + 1;
				}else{
					dataCount -= ePageInfo.getRangeStart() - 1;
				}
			}else if(ePageInfo.getRangeEnd() != null && ePageInfo.getRangeEnd() < dataCount){
				dataCount = ePageInfo.getRangeEnd();
			}
		}
		return new EntityPagingIterator(totalCount, dataCount, ignoreCount, startPageNo, user, proxy);
	}*/
	
	
	@Override
	public EntityPagingQueryProxy getEntityExportQueryProxy(EntitiesQueryParameter param, ExportDataPageInfo ePageInfo) {
		FusionContextConfig config = fFactory.getModuleConfig(param.getModuleName());
		BizFusionContext context = config.getCurrentContext(param.getUser());
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		EntitySortedPagedQuery sortedPagedQuery = discoverer.discoverQuick(param.getMainCriterias(), "编辑时间", param.getCriteriasMap());
		PageInfo pageInfo = ePageInfo.getPageInfo();
		if("all".equals(ePageInfo.getScope())){
			return new EntityQueryAdapter(sortedPagedQuery, config.getConfigResolver(), ePageInfo.getQueryCacheCount());
		}else{
			return new EntityQueryAdapter(sortedPagedQuery, config.getConfigResolver(), pageInfo.getPageSize());
		}
	}
	
	@Override
	public Map<String, RelationEntityPropertyParser> queryRelationEntityParsers(EntitiesQueryParameter param,
			String relationName) {
		Map<String, RelationEntityPropertyParser> map = new LinkedHashMap<>();
		if(param.getEntityCodes() != null && !param.getEntityCodes().isEmpty() && relationName != null) {
			for (String code : param.getEntityCodes()) {
				EntityQueryParameter entityQueryParam = new EntityQueryParameter(param.getModuleName(), code, param.getRelationName(), param.getUser());
				RelationEntityPropertyParser parser = getRelationEntityParser(entityQueryParam);
				if(parser != null) {
					map.put(code, parser);
				}
			}
		}
		return map;
	}
	
	@Override
	public RelationEntityPropertyParser getRelationEntityParser(EntityQueryParameter param) {
		return fFactory.getModuleResolver(param.getModuleName()).
				createRelationParser(
						getModuleRelationEntity(param), 
						param.getRelationName(), 
						param.getUser());
	}
	
	@Override
	public Entity getModuleRelationEntity(EntityQueryParameter entityQueryParam) {
		BizFusionContext context = fFactory.getModuleConfig(entityQueryParam.getModuleName()).createRelationContext(entityQueryParam.getRelationName(), entityQueryParam.getUser());
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		Entity result=discoverer.discover(entityQueryParam.getEntityCode());
		return result;
	}
	
	@Override
	public List<EntityHistoryItem> queryHistory(EntityQueryParameter param, Integer pageNo, Integer pageSize) {
		BizFusionContext context = fFactory.getModuleConfig(param.getModuleName()).getCurrentContext(param.getUser());
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		
		List<RecordHistory> historyList = discoverer.trackHistory(param.getEntityCode(), pageNo, pageSize);
		List<EntityHistoryItem> list = new ArrayList<EntityHistoryItem>();
		historyList.forEach(history->{
			EntityHistoryItem item = new EntityHistoryItem();
			item.setId(FormatUtils.toLong(history.getId()));
			item.setTime(history.getCreationTime());
			item.setUserName(toUserName(history.getUsergroupId()));
			item.setDesc(history.getContentWithDecompress());
			list.add(item);
		});
		return list;
	}
	
	@Override
	public EntityHistoryItem getLastHistoryItem(EntityQueryParameter param) {
		List<EntityHistoryItem> histories = queryHistory(param, 1, 1);
		if(histories != null && !histories.isEmpty()) {
			return histories.get(0);
		}
		return null;
	}
	
	private String toUserName(String usergroupId) {
		UserInfo user = ServiceFactory.getUserInfoService().getUserInfo(usergroupId);
		if(user != null) {
			return user.getUserName();
		}else {
			return "未知用户";
		}
	}

	@Override
	public String fuseEntity(EntityQueryParameter param, Map<String, Object> entityMap) {
		FusionContextConfig config = fFactory.getModuleConfig(param.getModuleName());
		String code = (String) entityMap.remove(config.getCodeAttributeName());
		entityMap.remove(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL);
		if(TextUtils.hasText(code)) {
			param.setEntityCode(code);
			delete(param);
		}
		return mergeEntity(param, entityMap);
	}

	@Override
	public String mergeEntity(EntityQueryParameter param, Map<String, Object> entityMap) {
		return fFactory.getModuleResolver(param.getModuleName()).saveEntity(entityMap, null, param.getUser(), param.getCriteriasMap());
	}
	
	@Override
	public void delete(EntityQueryParameter param) {
		if(param.getEntityCode() != null) {
			FusionContextConfig config = fFactory.getModuleConfig(param.getModuleName());
			config.removeEntity(param.getEntityCode(), param.getUser());
		}
	}
	
	@Override
	public void remove(EntitiesQueryParameter param) {
		if(!param.getEntityCodes().isEmpty()) {
			FusionContextConfig config = fFactory.getModuleConfig(param.getModuleName());
			for (String entityCode : param.getEntityCodes()) {
				config.removeEntity(entityCode, param.getUser());
			}
		}
	}
	
	
	
	@Override
	public List<Entity> queryModuleEntities(EntitiesQueryParameter param) {
		BizFusionContext context;
		String moduleName = param.getModuleName();
		if(TextUtils.hasText(param.getRelationName())) {
			context = fFactory.getModuleConfig(moduleName).createRelationContext(param.getRelationName(), param.getUser());
		}else {
			context = fFactory.getModuleConfig(moduleName).getCurrentContext(param.getUser());
		}
		
		Discoverer discoverer = PanelFactory.getDiscoverer(context);
		
		EntitySortedPagedQuery sortedPagedQuery = discoverer.discoverQuick(param.getMainCriterias(), "编辑时间", param.getCriteriasMap());
		//EntitySortedPagedQuery sortedPagedQuery = discoverer.discoverQuick(param.getMainCriterias(), "编辑时间");
		PageInfo pageInfo = param.getPageInfo();
		sortedPagedQuery.setPageSize(pageInfo.getPageSize());
		pageInfo.setCount(sortedPagedQuery.getAllCount());
		if(Integer.valueOf(0).equals(pageInfo.getCount())) {
			pageInfo.setPageNo(1);
		}else if(pageInfo.getCount() < pageInfo.getPageSize() * pageInfo.getPageNo() ) {
			pageInfo.setPageNo((int) Math.ceil(Double.valueOf(pageInfo.getCount()) / pageInfo.getPageSize()));
		}
		List<Entity> entities = sortedPagedQuery.visit(pageInfo.getPageNo());
		return entities;
	}

	
}
