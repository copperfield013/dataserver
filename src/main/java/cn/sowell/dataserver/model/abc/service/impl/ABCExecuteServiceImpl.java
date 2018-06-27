package cn.sowell.dataserver.model.abc.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.abc.application.BizFusionContext;
import com.abc.application.RemovedFusionContext;
import com.abc.dto.ErrorInfomation;
import com.abc.extface.dto.RecordHistory;
import com.abc.mapping.entity.Entity;
import com.abc.panel.Discoverer;
import com.abc.panel.PanelFactory;
import com.abc.query.criteria.Criteria;
import com.abc.query.entity.impl.EntitySortedPagedQuery;
import com.abc.record.HistoryTracker;

import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.ABCNodeProxy;
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
	
	
	@Override
	public EntityPagingQueryProxy getModuleQueryProxy(String moduleName, List<Criteria> cs, ExportDataPageInfo ePageInfo) {
		FusionContextConfig config = fFactory.getModuleConfig(moduleName);
		BizFusionContext context = config.createContext();
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		EntitySortedPagedQuery sortedPagedQuery = discoverer.discover(cs, "编辑时间");
		
		PageInfo pageInfo = ePageInfo.getPageInfo();
		if("all".equals(ePageInfo.getScope())){
			return new EntityQueryAdapter(sortedPagedQuery, config.getConfigResolver(), ePageInfo.getQueryCacheCount());
		}else{
			return new EntityQueryAdapter(sortedPagedQuery, config.getConfigResolver(), pageInfo.getPageSize());
		}
	}
	
	
	private List<Entity> queryEntityList(String moduleName, List<Criteria> criterias, PageInfo pageInfo){
		BizFusionContext context = fFactory.getModuleConfig(moduleName).createContext();
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		
		EntitySortedPagedQuery sortedPagedQuery = discoverer.discover(criterias, "编辑时间");
		sortedPagedQuery.setPageSize(pageInfo.getPageSize());
		pageInfo.setCount(sortedPagedQuery.getAllCount());
		List<Entity> peoples = sortedPagedQuery.visit(pageInfo.getPageNo());
		return peoples;
	}
	
	@Override
	public List<Entity> queryModuleEntities(QueryEntityParameter param) {
		return queryEntityList(
				param.getModule(), 
				param.getCriterias(), 
				param.getPageInfo());
	}
	
	
	@Override
	public Entity getHistoryEntity(QueryEntityParameter param, List<ErrorInfomation> errors) {
		BizFusionContext context = fFactory.getModuleConfig(param.getModule()).createContext();
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		
		HistoryTracker tracker = discoverer.track(param.getCode(), param.getHistoryTime());
		List<ErrorInfomation> errorInfomations = tracker.getErrorInfomations();
		if(errors != null && errorInfomations != null && !errorInfomations.isEmpty()){
			errors.addAll(errorInfomations);
		}
		return tracker.getEntity();
		
	}
	
	@Override
	public List<EntityHistoryItem> queryHistory(String moduleName, String code,
			Integer pageNo, Integer pageSize) {
		BizFusionContext context = fFactory.getModuleConfig(moduleName).createContext();
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		
		List<RecordHistory> historyList = discoverer.trackHistory(code, pageNo, pageSize);
		
		List<EntityHistoryItem> list = new ArrayList<EntityHistoryItem>();
		historyList.forEach(history->{
			EntityHistoryItem item = new EntityHistoryItem();
			item.setId(FormatUtils.toLong(history.getId()));
			item.setTime(history.getCreationTime());
			item.setUserName(toUserName(history.getUsergroupId()));
			item.setDesc(history.getContent());
			list.add(item);
		});
		return list;
	}

	private String toUserName(String usergroupId) {
		return "户户户";
	}
	
	@Override
	public Entity getModuleEntity(String moduleName, String code) {
		BizFusionContext context = fFactory.getModuleConfig(moduleName).createContext();
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		Entity result=discoverer.discover(code);
		return result;
	}
	

	@Override
	public void delete(String code) {
		RemovedFusionContext appInfo=new RemovedFusionContext(code, null, "list-delete" );
		if(!PanelFactory.getIntegration().remove(appInfo)){
			throw new RuntimeException("删除失败");
		}
	}
	
	@Override
	public String mergeEntity(String module, Map<String, Object> propMap) {
		return fFactory.getModuleResolver(module).saveEntity(propMap, null);
	}	
	
	@Override
	public String fuseEntity(String module, Map<String, Object> map) {
		FusionContextConfig config = fFactory.getModuleConfig(module);
		String code = (String) map.remove(config.getCodeAttributeName());
		map.remove(ABCNodeProxy.CODE_PROPERTY_NAME);
		if(TextUtils.hasText(code)) {
			delete(code);
		}
		return mergeEntity(module, map);
	}
	
	
	@Override
	public ModuleEntityPropertyParser getModuleEntityParser(String module, String code) {
		return getModuleEntityParser(module, getModuleEntity(module, code));
	}
	
	@Override
	public ModuleEntityPropertyParser getModuleEntityParser(String module, Entity entity) {
		return fFactory.getModuleResolver(module).createParser(entity);
	}


}
