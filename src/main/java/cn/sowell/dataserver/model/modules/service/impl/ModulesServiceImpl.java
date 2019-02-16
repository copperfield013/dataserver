package cn.sowell.dataserver.model.modules.service.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.abc.application.BizFusionContext;
import com.abc.mapping.entity.Entity;
import com.abc.rrc.query.criteria.EntityCriteriaFactory;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.datacenter.entityResolver.CEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.FieldConfigure;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.RelationFieldConfigure;
import cn.sowell.datacenter.entityResolver.config.abst.Module;
import cn.sowell.datacenter.entityResolver.impl.RelationEntityPropertyParser;
import cn.sowell.dataserver.model.abc.service.ABCExecuteService;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.modules.bean.EntityPagingIterator;
import cn.sowell.dataserver.model.modules.bean.EntityPagingQueryProxy;
import cn.sowell.dataserver.model.modules.bean.ExportDataPageInfo;
import cn.sowell.dataserver.model.modules.pojo.EntityHistoryItem;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.tmpl.bean.QueryEntityParameter;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;
import cn.sowell.dataserver.model.tmpl.service.ListCriteriaFactory;

@Service
public class ModulesServiceImpl implements ModulesService{
	
	@Resource
	ABCExecuteService abcService;
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	@Resource
	ListCriteriaFactory lcriteriaFactory;
	
	@Resource
	DictionaryService dictService;
	
	
	@Override
	public List<ModuleEntityPropertyParser> queryEntities(QueryEntityParameter param) {
		List<Entity> list = abcService.queryModuleEntities(param);
		return CollectionUtils.toList(list, entity->abcService.getModuleEntityParser(param.getModule(), entity, param.getUser()));
	}

	
	@Override
	public ModuleMeta getModule(String moduleKey) {
		Module module = fFactory.getModule(moduleKey);
		return new ModuleMeta() {
			
			@Override
			public String getTitle() {
				return module.getTitle();
			}
			
			@Override
			public String getName() {
				return module.getName();
			}
		};
	}
	
	@Override
	public ModuleMeta getStatModule(String moduleName) {
		FusionContextConfig config = fFactory.getModuleConfig(moduleName);
		if(config != null && config.isStatistic()) {
			return getModule(moduleName);
		}
		return null;
	}
	
	
	@Override
	public ModuleEntityPropertyParser getEntity(String module, String code, Date date, UserIdentifier user) {
		if(date == null) {
			Entity entity = abcService.getModuleEntity(module, code, user);
			return entity == null? null: abcService.getModuleEntityParser(module, entity, user, null);
		}else {
			QueryEntityParameter param = new QueryEntityParameter();
			param.setModule(module);
			param.setCode(code);
			param.setHistoryTime(date);
			return abcService.getHistoryEntityParser(param, user);
		}
	}
	
	@Override
	public ModuleEntityPropertyParser getHistoryEntityParser(String moduleName, String code, Long historyId,
			UserIdentifier user) {
		QueryEntityParameter param = new QueryEntityParameter();
		param.setModule(moduleName);
		param.setCode(code);
		param.setHistoryId(historyId);
		return abcService.getHistoryEntityParser(param, user);
	}
	
	@Override
	public List<EntityHistoryItem> queryHistory(String module, String code, Integer pageNo, Integer pageSize, UserIdentifier user) {
		return abcService.queryHistory(module, code, pageNo, pageSize, user);
	}


	@Override
	public void deleteEntity(String moduleName, String code, UserIdentifier user) {
		abcService.delete(moduleName, code, user);
	}
	
	@Override
	public void removeEntities(String moduleName, Set<String> codes, UserIdentifier user) {
		abcService.remove(moduleName, codes, user);
	}
	
	@Override
	public String mergeEntity(String module, Map<String, Object> map, UserIdentifier user) {
		return abcService.mergeEntity(module, map, user);
	}
	
	@Override
	public String fuseEntity(String module, Map<String, Object> map, UserIdentifier user) {
		return abcService.fuseEntity(module, map, user);
	}
	
	@Override
	public EntityPagingIterator queryIterator(TemplateListTemplate ltmpl, Set<NormalCriteria> nCriterias,
			ExportDataPageInfo ePageInfo, UserIdentifier user) {
		PageInfo pageInfo = ePageInfo.getPageInfo();
		BizFusionContext context = fFactory.getModuleConfig(ltmpl.getModule()).getCurrentContext(user);
		EntityCriteriaFactory cf = lcriteriaFactory.appendCriterias(nCriterias, ltmpl.getModule(), context);
		EntityPagingQueryProxy proxy = abcService.getModuleQueryProxy(ltmpl.getModule(), cf.getCriterias(), ePageInfo, user);
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
	}


	@Override
	public Map<String, CEntityPropertyParser> getEntityParsers(String moduleName, String relationName, Set<String> codes, UserIdentifier user) {
		Assert.hasText(moduleName);
		Map<String, CEntityPropertyParser> map = new LinkedHashMap<>();
		if(codes != null && !codes.isEmpty()) {
			if(relationName != null) {
				for (String code : codes) {
					RelationEntityPropertyParser parser = abcService.getRelationEntityParser(moduleName, relationName, code, user);
					if(parser != null) {
						map.put(code, parser);
					}
				}
			}else {
				for (String code : codes) {
					ModuleEntityPropertyParser parser = abcService.getModuleEntityParser(moduleName, code, user);
					if(parser != null) {
						map.put(code, parser);
					}
				}
			}
		}
		return map;
		
	}
	
	@Override
	public EntityHistoryItem getLastHistoryItem(String moduleName, String code, UserIdentifier user) {
		List<EntityHistoryItem> histories = abcService.queryHistory(moduleName, code, 1, 1, user);
		if(histories != null && !histories.isEmpty()) {
			return histories.get(0);
		}
		return null;
	}
	
	@Override
	public boolean getModuleEntityWritable(String moduleName) {
		FusionContextConfigResolver resolver = fFactory.getModuleResolver(moduleName);
		if(resolver != null) {
			return resolver.isEntityWritable();
		}
		return false;
	}
	
	@Override
	public ModuleMeta getCompositeRelatedModule(String moduleName, Long compositeId) {
		FusionContextConfigResolver resolver = fFactory.getModuleResolver(moduleName);
		DictionaryComposite composite = dictService.getComposite(moduleName, compositeId);
		String compositeName = composite.getName();
		FieldConfigure relationConfigure = resolver.getFieldConfigure(compositeName);
		if(relationConfigure instanceof RelationFieldConfigure) {
			Long mappingId = ((RelationFieldConfigure) relationConfigure).getRabcMappingId();
			if(mappingId != null) {
				FusionContextConfig config = fFactory.getAllConfigs().stream().filter((c)->mappingId.equals(c.getMappingId())).findFirst().orElse(null);
				if(config != null) {
					return getModule(config.getModule());
				}
			}
		}
		return null;
	} 
	
}
