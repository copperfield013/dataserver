package cn.sowell.dataserver.model.dict.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Maps;

import cho.carbon.auth.constant.AuthConstant;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.copframe.utils.TimelinenessWrapper;
import cn.sowell.copframe.utils.TimelinessMap;
import cn.sowell.datacenter.entityResolver.Composite;
import cn.sowell.datacenter.entityResolver.FieldConfigure;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FieldService;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.ImportCompositeField;
import cn.sowell.datacenter.entityResolver.Label;
import cn.sowell.datacenter.entityResolver.RelationFieldConfigure;
import cn.sowell.dataserver.Constants;
import cn.sowell.dataserver.model.dict.dao.DictionaryDao;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryCompositeExpand;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.pojo.DictionaryOption;
import cn.sowell.dataserver.model.dict.pojo.DictionaryRelationLabels;
import cn.sowell.dataserver.model.dict.pojo.OptionItem;
import cn.sowell.dataserver.model.dict.service.DictionaryService;

@Service("dictionaryServiceImpl")
public class DictionaryServiceImpl implements DictionaryService, FieldService{

	private static final long GLOBAL_TIMEOUT = Long.MAX_VALUE;

	@Resource
	DictionaryDao dictDao;
	
	Logger logger = Logger.getLogger(DictionaryServiceImpl.class);
	
	
	
	private final TimelinessMap<String, List<DictionaryComposite>> moduleCompositesMap = new TimelinessMap<>(GLOBAL_TIMEOUT);
	private final TimelinessMap<String, List<DictionaryField>> moduleFieldsMap = new TimelinessMap<>(GLOBAL_TIMEOUT);
	private final TimelinenessWrapper<List<DictionaryOption>> optionsCache = new TimelinenessWrapper<>(GLOBAL_TIMEOUT);
	private final TimelinessMap<Integer, List<OptionItem>> optionItemsMap = new TimelinessMap<>(GLOBAL_TIMEOUT);
	private final Map<String, Set<FieldParserDescription>> fieldDescsMap = new HashMap<>();
	
	
	
	@Override
	public synchronized DictionaryComposite getCurrencyCacheCompositeByFieldId(String module, Integer fieldId) {
		DictionaryField field = getAllFields(module).stream().filter(f->fieldId.equals(f.getId())).findFirst().orElse(null);
		return field != null? field.getComposite(): null;
	}
	
	@Override
	public synchronized List<DictionaryComposite> getAllComposites(String moduleName) {
		Set<String> set = new HashSet<>();
		set.add(moduleName);
		Map<String, List<DictionaryComposite>> map = getAllCompositesMap(set);
		List<DictionaryComposite> ret = null;
		if(map != null) {
			ret = map.get(moduleName);
		}
		return FormatUtils.coalesce(ret, new ArrayList<>());
	}
	
	@Override
	public Map<String, List<DictionaryComposite>> getAllCompositesMap(Set<String> moduleNames) {
		return moduleCompositesMap.getAll(()->{
			try {
				Set<String> uncontainedModuleNames = moduleNames.stream().filter(moduleName->!moduleCompositesMap.contains(moduleName)).collect(Collectors.toSet());
				if(!uncontainedModuleNames.isEmpty()) {
					List<DictionaryComposite> composites = dictDao.getAllComposites(uncontainedModuleNames);
					Map<Integer, List<DictionaryField>> allCompositeFieldMap = dictDao.getAllFields(CollectionUtils.toSet(composites, DictionaryComposite::getId));
					Map<Integer, DictionaryRelationLabels> allRelationSubdomainMaps = 
							dictDao.getRelationSubdomainMap(
									CollectionUtils.toSet(composites.stream().filter(
											c->Composite.RELATION_ADD_TYPE.equals(c.getAddType())
											).collect(Collectors.toSet()), c->c.getId()));;
					handlerComposite(composites, allCompositeFieldMap, allRelationSubdomainMaps);
					return CollectionUtils.toListMap(composites, DictionaryComposite::getModule);
				}else {
					return Maps.newHashMap();
				}
			} catch (Exception e) {
				logger.error("初始化模块[" + moduleNames + "]的字段数据时发生错误", e);
				return null;
			}
		});
	}
	
	private void handlerComposite(
			List<DictionaryComposite> composites, 
			Map<Integer, List<DictionaryField>> allCompositeFieldsMap,
			Map<Integer, DictionaryRelationLabels> allRelationSubdomainMaps){
		handerWithConfig(composites);
		Map<Integer, DictionaryComposite> compositeMap = CollectionUtils.toMap(composites, DictionaryComposite::getId);
		//Map<Long, List<DictionaryField>> compositeFieldMap = dictDao.getAllFields(compositeMap.keySet());
	
		allCompositeFieldsMap.forEach((cId, fields)->fields.forEach(field->{
			if(compositeMap.containsKey(cId)) {
				field.setComposite(compositeMap.get(cId));
				setFieldPattern(field);
			}
		}));
		/*Map<Long, DictionaryRelationLabels> relationSubdomainMap = 
				dictDao.getRelationSubdomainMap(
						CollectionUtils.toSet(composites.stream().filter(
								c->Composite.RELATION_ADD_TYPE.equals(c.getAddType())
								).collect(Collectors.toSet()), c->c.getId()));*/
		composites.forEach(composite->{
			composite.setFields(FormatUtils.coalesce(allCompositeFieldsMap.get(composite.getId()), new ArrayList<DictionaryField>()));
			if(allRelationSubdomainMaps.containsKey(composite.getId())) {
				DictionaryRelationLabels labels = allRelationSubdomainMaps.get(composite.getId());
				composite.setRelationSubdomain(labels.getLabels());
				composite.setRelationLabelAccess(labels.getAccess());
			}
		});
	}
	
	
	@Override
	public Map<Integer, DictionaryCompositeExpand> getCompositeExpandMap(String moduleName, Set<Integer> compositeIds) {
		if(compositeIds != null) {
			Map<Integer, DictionaryCompositeExpand> expandMap = new HashMap<>();
			Set<DictionaryComposite> composites = getAllComposites(moduleName).stream().filter(composite->compositeIds.contains(composite.getId())).collect(Collectors.toSet());
			for (DictionaryComposite composite : composites) {
				DictionaryCompositeExpand expand = new DictionaryCompositeExpand();
				Set<String> classes = new LinkedHashSet<>();
				FusionContextConfigResolver resolver = fFactory.getModuleResolver(moduleName);
				FieldConfigure compositeConfig = resolver.getFieldConfigure(composite.getName());
				if(compositeConfig != null) {
					if(compositeConfig instanceof RelationFieldConfigure) {
						RelationFieldConfigure relationFieldConfig = (RelationFieldConfigure) compositeConfig;
						if(AuthConstant.AUTH_RECORDTYPE_USER.equals(relationFieldConfig.getAbcNodeAbcAttr())) {
							classes.add("user");
						}
						expand.setDataClasses(classes);
						Long mappingId = relationFieldConfig.getRabcMappingId();
						if(mappingId != null) {
							FusionContextConfig config = fFactory.getAllConfigs().stream().filter((c)->mappingId.equals(c.getMappingId())).findFirst().orElse(null);
							if(config != null) {
								expand.setRabcModule(config.getModule());
							}
						}
					}
				}
				expandMap.put(composite.getId(), expand);
			}
			return expandMap;
		}
		return null;
	}
	
	@Override
	public Set<String> getCompositeClasses(String moduleName, Integer compositeId){
		DictionaryComposite composite = getComposite(moduleName, compositeId);
		Set<String> classes = new HashSet<>();
		if(composite != null) {
			FusionContextConfigResolver resolver = fFactory.getModuleResolver(moduleName);
			FieldConfigure compositeConfig = resolver.getFieldConfigure(composite.getName());
			if(compositeConfig != null) {
				if(compositeConfig instanceof RelationFieldConfigure) {
					if(AuthConstant.AUTH_RECORDTYPE_USER.equals(((RelationFieldConfigure) compositeConfig).getAbcNodeAbcAttr())) {
						classes.add("user");
					}
				}
			}
		}
		return classes;
	}
	
	private void setFieldPattern(DictionaryField field) {
		if(Constants.TRUE.equals(field.getComposite().getIsArray())) {
			if(field.getFullKey().startsWith(field.getComposite().getName() + ".")) {
				field.setFieldPattern(
						field.getComposite().getName() 
						+ "[" + ImportCompositeField.REPLACE_INDEX + "]"
						+ field.getFullKey().substring(field.getComposite().getName().length()));
			}else {
				StringBuffer buffer = new StringBuffer(field.getFullKey());
				buffer.insert(buffer.lastIndexOf("."), "[" + ImportCompositeField.REPLACE_INDEX + "]");
				field.setFieldPattern(buffer.toString());
			}
		}else {
			field.setFieldPattern(field.getFullKey());
		}
	}

	@Override
	public DictionaryComposite getComposite(String module, Integer compositeId) {
		return getAllComposites(module).stream().filter(composite->compositeId.equals(composite.getId())).findFirst().orElse(null);
	}
	
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	
	private void handerWithConfig(List<DictionaryComposite> composites) {
		for (DictionaryComposite composite : composites) {
			if(Composite.RELATION_ADD_TYPE.equals(composite.getAddType())) {
				if(composite.getName() != null) {
					composite.setRelationKey(composite.getName().replaceAll("\\[\\d\\]", ""));
				}
			}
		}
		/*FusionContextConfig config = fFactory.getModuleConfigDependended(module);
		if(config.getConfigResolver() == null) {
			config.loadResolver(null);
		}*/
	}

	@Override
	public synchronized List<DictionaryField> getAllFields(String module) {
		return moduleFieldsMap.get(module, m->{
			List<DictionaryComposite> composites = getAllComposites(module);
			Assert.notNull(composites, "模块");
			List<DictionaryField> result = new ArrayList<>();
			composites.forEach(composite->composite.getFields().forEach(field->result.add(field)));
			return result;
		});
	}
	
	@Override
	public synchronized Map<String, List<DictionaryField>> getAllFields(Set<String> moduleNames) {
		Map<String, List<DictionaryField>> map = moduleFieldsMap.getAll(()->{
			Set<String> uncontainedModuleNames = moduleNames.stream().filter(moduleName->!moduleFieldsMap.contains(moduleName)).collect(Collectors.toSet());
			Map<String, List<DictionaryField>> fieldsMap = new HashMap<>();
			if(!uncontainedModuleNames.isEmpty()) {
				Map<String, List<DictionaryComposite>> compositesMap = getAllCompositesMap(moduleNames);
				compositesMap.forEach((moduleName, composites)->{
					List<DictionaryField> fields= new ArrayList<>();
					composites.forEach(composite->{
						fields.addAll(composite.getFields());
					});
					fieldsMap.put(moduleName, fields);
				});
			}
			return fieldsMap;
		});
		return map;
	}
	
	

	

	@Override
	public synchronized List<DictionaryOption> getAllOptions() {
		return optionsCache.getObject(()->dictDao.getAllOptions());
	}
	
	@Override
	public synchronized Map<Integer, List<OptionItem>> getOptionsMap(Set<Integer> fieldIds) {
		return optionItemsMap.getMap(fieldIds, (fs, optionMap)->{
			optionMap.putAll(dictDao.getFieldOptionsMap(fs));
		});
	}
	
	
	@Override
	public synchronized Set<FieldParserDescription> getFieldDescriptions(String module) {
		if(!fieldDescsMap.containsKey(module)) {
			Set<FieldParserDescription> fieldDescs = getLastFieldDescs(module);
			fieldDescsMap.put(module, fieldDescs);
			return fieldDescs;
		}
		return new LinkedHashSet<FieldParserDescription>(fieldDescsMap.get(module));
	}
	
	@Override
	public synchronized Map<String, Set<FieldParserDescription>> getFieldDescriptions(Set<String> moduleNames) {
		Set<String> unloadedModuleNames = new HashSet<>();
		moduleNames.forEach(moduleName->{
			if(!fieldDescsMap.containsKey(moduleName)) {
				unloadedModuleNames.add(moduleName);
			}
		});
		if(!unloadedModuleNames.isEmpty()) {
			Map<String, Set<FieldParserDescription>> fieldDescs = getLastFieldDescs(unloadedModuleNames);
			fieldDescsMap.putAll(fieldDescs);
		}
		Map<String, Set<FieldParserDescription>> map = new HashMap<>();
		moduleNames.forEach(moduleName->{
			map.put(moduleName, fieldDescsMap.get(moduleName));
		});
		return map;
	}

	private Map<String, Set<FieldParserDescription>> getLastFieldDescs(Set<String> unloadedModuleNames) {
		Map<String, Set<FieldParserDescription>> map = new HashMap<>();
		Map<String, List<DictionaryField>> fieldsMap = getAllFields(unloadedModuleNames);
		fieldsMap.forEach(
			(moduleName, fields)->
				map.put(moduleName, CollectionUtils.toSet(fields, FieldParserDescription::new)));
		return Collections.synchronizedMap(map);
	}

	

	private Set<FieldParserDescription> getLastFieldDescs(String module){
		Set<FieldParserDescription> fieldDescs = CollectionUtils.toSet(getAllFields(module), field->new FieldParserDescription(field));
		return Collections.synchronizedSet(fieldDescs);
	}
	
	public synchronized void updateDynamicFiedDescriptionSet(String module) {
		if(fieldDescsMap.containsKey(module)) {
			Set<FieldParserDescription> lastFieldDescs = getLastFieldDescs(module);
			Set<FieldParserDescription> fieldDescs = fieldDescsMap.get(module);
			fieldDescs.clear();
			fieldDescs.addAll(lastFieldDescs);
		}else {
			getFieldDescriptions(module);
		}
		
	}
	
	@Override
	public synchronized Map<String, Set<Label>> getAllLabelsMap() {
		Map<String, Set<Label>> labelsMap = new HashMap<>();
		Set<FusionContextConfig> configs = fFactory.getAllConfigs();
		configs.forEach(config->{
			labelsMap.put(config.getModule(), config.getAllLabels());
		});
		return labelsMap;
	}
	
	@Override
	public synchronized Map<String, Label> getModuleLabelMap(String module) {
		FusionContextConfig config = fFactory.getModuleConfig(module);
		return CollectionUtils.toMap(config.getAllLabels(), label->label.getFieldName());
	}
	
	@Override
	public Map<String, Label> getModuleLabelMap(String module, Set<String> criteriaFieldNames) {
		Map<String, Label> result = new HashMap<>();
		if(criteriaFieldNames != null && !criteriaFieldNames.isEmpty()) {
			Map<String, Label> labelMap = getModuleLabelMap(module);
			criteriaFieldNames.forEach(fieldName->result.put(fieldName, labelMap.get(fieldName)));
		}
		return result;
	}
	
	@Override
	public DictionaryField getField(String module, Integer fieldId) {
		return getAllFields(module).stream().filter(field->field.getId().equals(fieldId)).findFirst().orElse(null);
	}

	
	@Override
	public Map<Integer, DictionaryField> getFieldMap(String module, Set<Integer> fieldIds) {
		return getAllFields(module)
				.stream().filter(field->fieldIds.contains(field.getId()))
				.collect(Collectors.toMap(field->field.getId(), field->field));
	}
	
	
	
	Map<String, Set<String>> fieldInputTypeMap;
	
	@Override
	public Map<String, Set<String>> getFieldInputTypeMap() {
		synchronized (this) {
			if(fieldInputTypeMap == null) {
				try {
					ClassPathResource resource = new ClassPathResource("field-input-typemap.json");
					if(resource.exists()) {
						JSONObject jo = (JSONObject) JSON.parse(TextUtils.readAsString(resource.getInputStream()));
						Map<String, Set<String>> map = new HashMap<String, Set<String>>();
						jo.getJSONObject("selectableType").forEach((type, v)->{
							JSONArray jInputTypeArray = (JSONArray) v;
							Set<String> inputTypeSet = new LinkedHashSet<String>();
							map.put(type, inputTypeSet);
							inputTypeSet.add("text");
							jInputTypeArray.forEach(t->inputTypeSet.add((String) t));
						});
						fieldInputTypeMap = map;
					}else {
						throw new RuntimeException("field-input-typemap.json文件不存在");
					}
				} catch (Exception e) {
					throw new RuntimeException("初始化field-input-typemap.json时发生错误", e);
				}
			}
			return fieldInputTypeMap;
		}
	}
	
	
	@Override
	public synchronized void refreshFields() {
		fieldDescsMap.clear();
		optionsCache.refresh();
		optionItemsMap.refresh();
		moduleFieldsMap.refresh();
		moduleCompositesMap.refresh();
	}
	
	
	Map<Integer, List<DictionaryOption>> casOptionMap;
	@Override
	public List<DictionaryOption> queryOptions(Integer optGroupId) {
		synchronized (this) {
			if(casOptionMap == null) {
				casOptionMap = new HashMap<>();
			}
		}
		synchronized (casOptionMap) {
			if(!casOptionMap.containsKey(optGroupId)) {
				List<DictionaryOption> options = dictDao.queryOptions(optGroupId);
				casOptionMap.put(optGroupId, options);
			}
		}
		return casOptionMap.get(optGroupId);
	}
	
	
	@Override
	public DictionaryField mapModuleField(String targetModuleName, DictionaryField originField) {
		String abcAttrCode = originField.getAbcAttrCode();
		List<DictionaryField> fields = getAllFields(targetModuleName);
		if(fields != null) {
			//获得目标模块中所有abcattrCode相同的字段
			Set<DictionaryField> eligibilities = 
					fields.stream()
					.filter(field->field.getAbcAttrCode()!= null && field.getAbcAttrCode().equals(abcAttrCode))
					.collect(Collectors.toSet());
			if(!eligibilities.isEmpty()) {
				//如果有多个字段匹配
				for (DictionaryField field : eligibilities) {
					//如果字段的全路径完全相同的话，那么直接判定相同
					if(field.getFullKey().equals(originField.getFullKey())) {
						return field;
					}
				}
				//筛选出片段长度相同的字段
				int fieldNameSplitCount = originField.getFullKey().split("\\.").length;
				//返回筛选后的第一个字段
				return eligibilities.stream()
								.filter(field->field.getFullKey().split("\\.").length == fieldNameSplitCount)
								.findFirst().orElse(null);
			}
		}
		return null;
	}
	
	@Override
	public DictionaryComposite mapModuleComposite(String targetModuleName, DictionaryComposite composite) {
		Assert.hasText(targetModuleName);
		Assert.notNull(composite);
		List<DictionaryComposite> composites = getAllComposites(targetModuleName);
		return composites.stream()
				.filter(c->c.getName().equals(composite.getName())).filter(c->c.getAddType() != null && c.getAddType().equals(composite.getAddType()) 
							&& (composite.getIsArray() != null && composite.getIsArray().equals(c.getIsArray()) 
								|| composite.getIsArray() == null && c.getIsArray() == null))
				.findFirst().orElse(null);
		
	}
	
}
