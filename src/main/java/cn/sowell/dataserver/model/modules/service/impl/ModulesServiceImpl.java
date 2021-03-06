package cn.sowell.dataserver.model.modules.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.sowell.datacenter.entityResolver.FieldConfigure;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.RelationFieldConfigure;
import cn.sowell.datacenter.entityResolver.config.ModuleConfigStructure;
import cn.sowell.datacenter.entityResolver.config.abst.Module;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.modules.service.ModulesService;

@Service
public class ModulesServiceImpl implements ModulesService{
	
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	@Resource
	DictionaryService dictService;
	
	
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
	public boolean getModuleEntityWritable(String moduleName) {
		FusionContextConfigResolver resolver = fFactory.getModuleResolver(moduleName);
		if(resolver != null) {
			return resolver.isEntityWritable();
		}
		return false;
	}
	
	@Override
	public ModuleMeta getCompositeRelatedModule(String moduleName, Integer compositeId) {
		DictionaryComposite composite = dictService.getComposite(moduleName, compositeId);
		String compositeName = composite.getName();
		return getRelationModule(moduleName, compositeName);
	} 
	
	@Override
	public ModuleConfigStructure getModuleConfigStructure(String moduleName) {
		return fFactory.getConfigStructure(moduleName);
	}
	
	@Override
	public ModuleMeta getRelationModule(String moduleName, String relationName) {
		FusionContextConfigResolver resolver = fFactory.getModuleResolver(moduleName);
		FieldConfigure relationConfigure = resolver.getFieldConfigure(relationName);
		if(relationConfigure instanceof RelationFieldConfigure) {
			List<DictionaryComposite> composites = dictService.getAllComposites(moduleName);
			DictionaryComposite relComposite = composites.stream().filter(composite->relationName.equals(composite.getName())).findFirst().orElse(null);
			if(relComposite != null) {
				return getModule(relComposite.getRelModuleName());
			}
		}
		return null;
	}
	
	
	
}
