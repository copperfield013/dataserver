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

import com.abc.auth.constant.AuthConstant;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
import cn.sowell.dataserver.model.dict.dao.DictionaryDao;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
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
				compositeFieldMap.forEach((cId, fields)->fields.forEach(field->{
					field.setComposite(compositeMap.get(cId));
					setFieldPattern(field);
				}));
				Map<Long, DictionaryRelationLabels> relationSubdomainMap = 
						dictDao.getRelationSubdomainMap(
								CollectionUtils.toSet(composites.stream().filter(
										c->Composite.RELATION_ADD_TYPE.equals(c.getAddType())
										).collect(Collectors.toSet()), c->c.getId()));
				composites.forEach(composite->{
					composite.setFields(FormatUtils.coalesce(compositeFieldMap.get(composite.getId()), new ArrayList<DictionaryField>()));
					if(relationSubdomainMap.containsKey(composite.getId())) {
						DictionaryRelationLabels labels = relationSubdomainMap.get(composite.getId());
						composite.setRelationSubdomain(labels.getLabels());
						composite.setRelationLabelAccess(labels.getAccess());
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
	public Set<String> getCompositeClasses(String moduleName, Long compositeId){
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
		if(Integer.valueOf(1).equals(field.getComposite().getIsArray())) {
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
	public DictionaryComposite getComposite(String module, Long compositeId) {
		return getAllComposites(module).stream().filter(composite->compositeId.equals(composite.getId())).findFirst().orElse(null);
	}
	
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	
	private void handerWithConfig(String module, List<DictionaryComposite> composites) {
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
	
	
	Map<Long, List<DictionaryOption>> casOptionMap;
	@Override
	public List<DictionaryOption> queryOptions(Long optGroupId) {
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
