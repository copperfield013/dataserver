package cn.sowell.dataserver.model.abc.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.abc.rrc.query.queryrecord.criteria.Criteria;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.ABCNodeProxy;
import cn.sowell.datacenter.entityResolver.impl.RelationEntityPropertyParser;
import cn.sowell.dataserver.model.abc.service.ABCExecuteService;
import cn.sowell.dataserver.model.modules.bean.EntityPagingQueryProxy;
import cn.sowell.dataserver.model.modules.bean.EntityQueryAdapter;
import cn.sowell.dataserver.model.modules.bean.ExportDataPageInfo;
import cn.sowell.dataserver.model.modules.pojo.EntityHistoryItem;
import cn.sowell.dataserver.model.tmpl.bean.QueryEntityParameter;


@Service
public class ABCExecuteServiceImpl implements ABCExecuteService{

	Logger logger = Logger.getLogger(ABCExecuteService.class);
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	
	public EntityPagingQueryProxy getModuleQueryProxy(String moduleName, List<Criteria> cs, ExportDataPageInfo ePageInfo, UserIdentifier user) {
		FusionContextConfig config = fFactory.getModuleConfig(moduleName);
		BizFusionContext context = config.getCurrentContext(user);
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		EntitySortedPagedQuery sortedPagedQuery = discoverer.discover(cs, "编辑时间");
		
		PageInfo pageInfo = ePageInfo.getPageInfo();
		if("all".equals(ePageInfo.getScope())){
			return new EntityQueryAdapter(sortedPagedQuery, config.getConfigResolver(), ePageInfo.getQueryCacheCount());
		}else{
			return new EntityQueryAdapter(sortedPagedQuery, config.getConfigResolver(), pageInfo.getPageSize());
		}
	}
	
	
	private List<Entity> queryEntityList(String moduleName, String relationName, List<Criteria> criterias, PageInfo pageInfo, UserIdentifier user){
		BizFusionContext context;
		if(TextUtils.hasText(relationName)) {
			context = fFactory.getModuleConfig(moduleName).createRelationContext(relationName, user);
		}else {
			context = fFactory.getModuleConfig(moduleName).getCurrentContext(user);
		}
		
		Discoverer discoverer = PanelFactory.getDiscoverer(context);
		
		EntitySortedPagedQuery sortedPagedQuery = discoverer.discoverQuick(criterias, "编辑时间");
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
	
	public List<Entity> queryModuleEntities(QueryEntityParameter param) {
		return queryEntityList(
				param.getModule(), 
				param.getRelationName(),
				param.getCriterias(), 
				param.getPageInfo(),
				param.getUser());
	}
	
	
	public ModuleEntityPropertyParser getHistoryEntityParser(QueryEntityParameter param, UserIdentifier user) {
		BizFusionContext context = fFactory.getModuleConfig(param.getModule()).getCurrentContext(user);
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		HistoryTracker tracker = null;
		if(param.getHistoryId() != null) {
			tracker = discoverer.track(BigInteger.valueOf(param.getHistoryId()));
		}else if(param.getCode() != null) {
			if(param.getHistoryTime() == null) {
				param.setHistoryTime(new Date());
			}
			tracker = discoverer.track(param.getCode(), param.getHistoryTime());
		}else {
			logger.error("historyId一级entityCode不能都为null！！！");
		}
		if(tracker != null) {
			Entity entity = tracker.getEntity();
			List<ErrorInfomation> errors = tracker.getErrorInfomations();
			ModuleEntityPropertyParser parser = fFactory.getModuleResolver(param.getModule()).createParser(entity, user, discoverer);
			parser.setErrors(errors);
			return parser;
		}else {
			return null;
		}
	}
	
	public List<EntityHistoryItem> queryHistory(String moduleName, String code,
			Integer pageNo, Integer pageSize, UserIdentifier user) {
		BizFusionContext context = fFactory.getModuleConfig(moduleName).getCurrentContext(user);
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		
		List<RecordHistory> historyList = discoverer.trackHistory(code, pageNo, pageSize);
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

	private String toUserName(String usergroupId) {
		UserInfo user = ServiceFactory.getUserInfoService().getUserInfo(usergroupId);
		if(user != null) {
			return user.getUserName();
		}else {
			return "未知用户";
		}
	}
	
	public Entity getModuleEntity(String moduleName, String code, UserIdentifier user) {
		BizFusionContext context = fFactory.getModuleConfig(moduleName).getCurrentContext(user);
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		Entity result=discoverer.discover(code);
		return result;
	}
	
	public Entity getModuleRelationEntity(String moduleName, String relationName, String code, UserIdentifier user) {
		BizFusionContext context = fFactory.getModuleConfig(moduleName).createRelationContext(relationName, user);
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		Entity result=discoverer.discover(code);
		return result;
	}
	

	public void delete(String moduleName, String code, UserIdentifier user) {
		FusionContextConfig config = fFactory.getModuleConfig(moduleName);
		config.removeEntity(code, user);
	}
	
	public void remove(String moduleName, Set<String> codes, UserIdentifier user) {
		FusionContextConfig config = fFactory.getModuleConfig(moduleName);
		for (String code : codes) {
			config.removeEntity(code, user);
		}
	}
	
	public String mergeEntity(String module, Map<String, Object> propMap, UserIdentifier user) {
		return fFactory.getModuleResolver(module).saveEntity(propMap, null, user);
	}	
	
	public String fuseEntity(String module, Map<String, Object> map, UserIdentifier user) {
		FusionContextConfig config = fFactory.getModuleConfig(module);
		String code = (String) map.remove(config.getCodeAttributeName());
		map.remove(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL);
		if(TextUtils.hasText(code)) {
			delete(module, code, user);
		}
		return mergeEntity(module, map, user);
	}
	
	public ModuleEntityPropertyParser getModuleEntityParser(String module, String code, UserIdentifier user) {
		return getModuleEntityParser(module, code, user, null);
	}
	
	public ModuleEntityPropertyParser getModuleEntityParser(String module, String code, UserIdentifier user, Object propertyGetterArgument) {
		return getModuleEntityParser(module, getModuleEntity(module, code, user), user, propertyGetterArgument);
	}
	
	public ModuleEntityPropertyParser getModuleEntityParser(String module, Entity entity, UserIdentifier user) {
		return getModuleEntityParser(module, entity, user, null);
	}
	
	public ModuleEntityPropertyParser getModuleEntityParser(String module, Entity entity, UserIdentifier user, Object propertyGetterArgument) {
		return fFactory.getModuleResolver(module).createParser(entity, user, propertyGetterArgument);
	}
	
	public RelationEntityPropertyParser getRelationEntityParser(String moduleName, String relationName, Entity entity,
			UserIdentifier user) {
		return fFactory.getModuleResolver(moduleName).createRelationParser(entity, relationName, user);
	}
	
	public RelationEntityPropertyParser getRelationEntityParser(String moduleName, String relationName, String code, UserIdentifier user) {
		return getRelationEntityParser(moduleName, relationName, getModuleRelationEntity(moduleName, relationName, code, user), user);
	}


}
