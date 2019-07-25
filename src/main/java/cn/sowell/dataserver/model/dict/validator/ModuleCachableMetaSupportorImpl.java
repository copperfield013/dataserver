package cn.sowell.dataserver.model.dict.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import cho.carbon.meta.enun.StrucOptType;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.config.UnconfiuredFusionException;
import cn.sowell.dataserver.model.cachable.prepare.ModuleTemplateReferData;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.modules.service.ModulesService;

public class ModuleCachableMetaSupportorImpl implements ModuleCachableMetaSupportor{

	@Resource
	private DictionaryService dictService;
	@Resource
	private ModulesService mService;
	@Resource
	private FusionContextConfigFactory fFactory;

	static Logger logger = Logger.getLogger(ModuleCachableMetaSupportorImpl.class);
	
	
	
	@Override
	public String getRelationLabelAccess(DictionaryComposite composite, boolean moduleEntityWritable) {
		final String READ = StrucOptType.READ.getName();
		if(!moduleEntityWritable) {
			return READ;
		}
		if(READ.equals(composite.getAccess()) 
				|| StrucOptType.ADD.getName().equals(composite.getAccess())
				|| StrucOptType.SUPPLEMENT.getName().equals(composite.getAccess())) {
			return READ;
		}else {
			return composite.getRelationLabelAccess();
		}
	}

	@Override
	public String getAdditionRelationLabelAccess(DictionaryComposite composite, boolean moduleEntityWritable) {
		final String READ = StrucOptType.READ.getName();
		if(!moduleEntityWritable) {
			return READ;
		}
		if(READ.equals(composite.getAccess()) || StrucOptType.SUPPLEMENT.getName().equals(composite.getAccess())) {
			return READ;
		}else {
			return composite.getRelationLabelAccess();
		}
	}

	@Override
	public String getFieldAccess(DictionaryField field, boolean moduleEntityWritable) {
		Assert.notNull(field);
		String fAccess = field.getFieldAccess();
		DictionaryComposite composite = field.getComposite();
		final StrucOptType READ = StrucOptType.READ;
		if(!moduleEntityWritable) {
			return READ.getName();
		}else {
			if(READ.getName().equals(fAccess) || composite == null) {
				return fAccess;
			}else {
				String cAccess = composite.getAccess();
				if(READ.getName().equals(cAccess)) {
					return cAccess;
				}else if(StrucOptType.ADD.getName().equals(cAccess)) {
					//为增的话，已有记录的字段为只读
					return READ.getName();
				}else if(StrucOptType.SUPPLEMENT.getName().equals(cAccess)) {
					//为补的话，已有记录的字段为只读
					return READ.getName();
				}else if(StrucOptType.MERGE.getName().equals(cAccess)) {
					//为并的话，已有记录的字段根据其配置
					return fAccess;
				}else {
					return fAccess;
				}
			}
		}
	}

	@Override
	public String getFieldAdditionAccess(DictionaryField field, boolean moduleEntityWritable) {
		Assert.notNull(field);
		String fAccess = field.getFieldAccess();
		DictionaryComposite composite = field.getComposite();
		final StrucOptType READ = StrucOptType.READ;
		if(!moduleEntityWritable) {
			return READ.getName();
		}
		if(composite != null && READ.getName().equals(composite.getAccess())) {
			return READ.getName();
		}else {
			return fAccess;
		}
	}

	@Override
	public ModuleTemplateReferData getModuleTemplateReferData(String module) {
		ModuleTemplateReferData referData = new ModuleTemplateReferData();
		referData.setCompositeMap(FormatUtils.coalesce(CollectionUtils.toMap(dictService.getAllComposites(module), DictionaryComposite::getId), new HashMap<>()));
		referData.setFieldMap(FormatUtils.coalesce(CollectionUtils.toMap(dictService.getAllFields(module), DictionaryField::getId), new HashMap<>()));
		referData.setFusionContextConfig(fFactory.getModuleConfig(module));
		referData.setFieldInputTypeMap(dictService.getFieldInputTypeMap());
		return referData;
	}
	
	
	@Override
	public ModuleTemplateReferDataGenerator getTemplateReferDataGenetator() {
		Set<FusionContextConfig> allConfig = fFactory.getAllConfigsLoaded();
		Map<String, FusionContextConfig> moduleConfigMap = CollectionUtils.toMap(allConfig, FusionContextConfig::getModule);
		Set<String> moduleNames = moduleConfigMap.keySet();
		Map<String, List<DictionaryComposite>> allCompositeMap = dictService.getAllCompositesMap(moduleNames);
		Map<String, List<DictionaryField>> allFieldsMap = dictService.getAllFields(moduleNames);
		
		Map<String, Map<Integer, DictionaryComposite>> compositeMap = new HashMap<>();
		Map<String, Map<Integer, DictionaryField>> fieldMap = new HashMap<>();
		
		allCompositeMap.forEach((moduleName, composites)->compositeMap.put(moduleName, CollectionUtils.toMap(composites, DictionaryComposite::getId)));
		allFieldsMap.forEach((moduleName, fields)->fieldMap.put(moduleName, CollectionUtils.toMap(fields, DictionaryField::getId)));
		
		
		Map<String, Set<String>> fieldInputTypeMap = dictService.getFieldInputTypeMap();
		Map<String, ModuleTemplateReferData> referDataMap = new HashMap<>();
		return new ModuleTemplateReferDataGenerator() {
			
			@Override
			public ModuleTemplateReferData generate(String moduleName) {
				synchronized (referDataMap) {
					if(!referDataMap.containsKey(moduleName)) {
						ModuleTemplateReferData referData = new ModuleTemplateReferData();
						referData.setCompositeMap(FormatUtils.coalesce(compositeMap.get(moduleName), new HashMap<>()));
						referData.setFieldMap(FormatUtils.coalesce(fieldMap.get(moduleName), new HashMap<>()));
						referData.setFusionContextConfig(moduleConfigMap.get(moduleName));
						referData.setFieldInputTypeMap(fieldInputTypeMap);
						referDataMap.put(moduleName, referData);
					}
					return referDataMap.get(moduleName);
				}
			}
		};
	}
	
	@Override
	public boolean supportFieldInputType(String criteriaInputType, String fieldType, Map<String, Set<String>> fieldInputTypeMap) {
		Set<String> fieldInputTypeSet = fieldInputTypeMap.get(fieldType);
		if(fieldInputTypeSet != null) {
			return fieldInputTypeSet.contains(criteriaInputType);
		}
		return false;
	}
	
	@Override
	public boolean checkModule(String moduleName) {
		try {
			return fFactory.getModuleConfig(moduleName) != null;
		} catch (UnconfiuredFusionException e) {
			return false;
		}
	}
	
}
