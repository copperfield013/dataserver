package cn.sowell.dataserver.model.dict.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.copframe.utils.TimelinenessWrapper;
import cn.sowell.copframe.utils.TimelinessMap;
import cn.sowell.datacenter.entityResolver.Composite;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FieldService;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.Label;
import cn.sowell.dataserver.model.dict.dao.DictionaryDao;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.pojo.DictionaryOption;
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
	private final TimelinessMap<Long, List<OptionItem>> optionItemsMap = new TimelinessMap<>(GLOBAL_TIMEOUT);
	private final Map<String, Set<FieldParserDescription>> fieldDescsMap = new HashMap<>();
	
	@Override
	public synchronized DictionaryComposite getCurrencyCacheCompositeByFieldId(String module, Long fieldId) {
		DictionaryField field = getAllFields(module).stream().filter(f->fieldId.equals(f.getId())).findFirst().orElse(null);
		return field != null? field.getComposite(): null;
	}
	
	@Override
	public synchronized List<DictionaryComposite> getAllComposites(String module) {
		return moduleCompositesMap.get(module, m->{
			try {
				List<DictionaryComposite> composites = dictDao.getAllComposites(m);
				handerWithConfig(module, composites);
				Map<Long, DictionaryComposite> compositeMap = CollectionUtils.toMap(composites, DictionaryComposite::getId);
				Map<Long, List<DictionaryField>> compositeFieldMap = dictDao.getAllFields(compositeMap.keySet());
				compositeFieldMap.forEach((cId, fields)->fields.forEach(field->field.setComposite(compositeMap.get(cId))));
				Map<Long, Set<String>> relationSubdomainMap = 
						dictDao.getRelationSubdomainMap(
								CollectionUtils.toSet(composites.stream().filter(
										c->Composite.RELATION_ADD_TYPE.equals(c.getAddType())
										).collect(Collectors.toSet()), c->c.getId()));
				composites.forEach(composite->{
					composite.setFields(FormatUtils.coalesce(compositeFieldMap.get(composite.getId()), new ArrayList<DictionaryField>()));
					if(relationSubdomainMap.containsKey(composite.getId())) {
						composite.setRelationSubdomain(relationSubdomainMap.get(composite.getId()));
					}
				});
				return composites;
			} catch (Exception e) {
				logger.error("初始化模块[" + m + "]的字段数据时发生错误", e);
				return null;
			}
		});
	}
	
	@Override
	public DictionaryComposite getComposite(String module, Long compositeId) {
		return getAllComposites(module).stream().filter(composite->compositeId.equals(composite.getId())).findFirst().orElse(null);
	}
	
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	
	private void handerWithConfig(String module, List<DictionaryComposite> composites) {
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
	public synchronized List<DictionaryOption> getAllOptions() {
		return optionsCache.getObject(()->dictDao.getAllOptions());
	}
	
	@Override
	public synchronized Map<Long, List<OptionItem>> getOptionsMap(Set<Long> fieldIds) {
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
	public DictionaryField getField(String module, Long fieldId) {
		return getAllFields(module).stream().filter(field->field.getId().equals(fieldId)).findFirst().orElse(null);
	}

	
	@Override
	public Map<Long, DictionaryField> getFieldMap(String module, Set<Long> fieldIds) {
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
	
}
