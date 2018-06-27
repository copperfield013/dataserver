package cn.sowell.dataserver.model.dict.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.copframe.utils.TimelinenessWrapper;
import cn.sowell.copframe.utils.TimelinessMap;
import cn.sowell.datacenter.entityResolver.FieldConfigure;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.FieldService;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.Label;
import cn.sowell.datacenter.entityResolver.RelationFieldConfigure;
import cn.sowell.dataserver.model.dict.dao.DictionaryDao;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.pojo.DictionaryOption;
import cn.sowell.dataserver.model.dict.pojo.OptionItem;
import cn.sowell.dataserver.model.dict.service.DictionaryService;

@Service("dictionaryServiceImpl")
public class DictionaryServiceImpl implements DictionaryService, FieldService{

	private static final long GLOBAL_TIMEOUT = 20000l;

	@Resource
	DictionaryDao dictDao;
	
	private final TimelinessMap<String, List<DictionaryComposite>> moduleCompositesMap = new TimelinessMap<>(GLOBAL_TIMEOUT);
	private final TimelinessMap<String, List<DictionaryField>> moduleFieldsMap = new TimelinessMap<>(GLOBAL_TIMEOUT);
	private final TimelinenessWrapper<List<DictionaryOption>> optionsCache = new TimelinenessWrapper<>(GLOBAL_TIMEOUT);
	private final Map<String, Set<FieldParserDescription>> fieldDescsMap = new HashMap<>();
	
	@Override
	public DictionaryComposite getCurrencyCacheCompositeByFieldId(String module, Long fieldId) {
		DictionaryField field = getAllFields(module).stream().filter(f->fieldId.equals(f.getId())).findFirst().orElse(null);
		return field != null? field.getComposite(): null;
	}
	
	@Override
	public List<DictionaryComposite> getAllComposites(String module) {
		return moduleCompositesMap.get(module, m->{
			List<DictionaryComposite> composites = dictDao.getAllComposites(m);
			handerWithConfig(module, composites);
			Map<Long, DictionaryComposite> compositeMap = CollectionUtils.toMap(composites, DictionaryComposite::getId);
			Map<Long, List<DictionaryField>> compositeFieldMap = dictDao.getAllFields(compositeMap.keySet());
			compositeFieldMap.forEach((cId, fields)->fields.forEach(field->field.setComposite(compositeMap.get(cId))));
			composites.forEach(composite->composite.setFields(FormatUtils.coalesce(compositeFieldMap.get(composite.getId()), new ArrayList<DictionaryField>())));
			return composites;
		});
	}
	
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	
	private void handerWithConfig(String module, List<DictionaryComposite> composites) {
		FusionContextConfig config = fFactory.getModuleConfigDependended(module);
		if(config.getConfigResolver() == null) {
			config.loadResolver(null);
		}
		FusionContextConfigResolver resolver = config.getConfigResolver();
		composites.forEach(composite->{
			if(TextUtils.hasText(composite.getName())) {
				FieldConfigure cpsConfig = resolver.getFieldConfigure(composite.getName());
				if(cpsConfig instanceof RelationFieldConfigure) {
					composite.setRelationSubdomain(((RelationFieldConfigure) cpsConfig).getLabelDomain());
				}else {
					composite.setRelationSubdomain(null);
				}
			}
		});
		
	}

	@Override
	public List<DictionaryField> getAllFields(String module) {
		return moduleFieldsMap.get(module, m->{
			List<DictionaryComposite> composites = getAllComposites(module);
			List<DictionaryField> result = new ArrayList<>();
			composites.forEach(composite->composite.getFields().forEach(field->result.add(field)));
			return result;
		});
	}
	
	

	@Override
	public List<DictionaryOption> getAllOptions() {
		return optionsCache.getObject(()->dictDao.getAllOptions());
	}
	
	@Override
	public Map<Long, List<OptionItem>> getOptionsMap(Set<Long> fieldIds) {
		return dictDao.getFieldOptionsMap(fieldIds);
	}
	
	
	@Override
	public Set<FieldParserDescription> getFieldDescriptions(String module) {
		if(!fieldDescsMap.containsKey(module)) {
			Set<FieldParserDescription> fieldDescs = getLastFieldDescs(module);
			fieldDescsMap.put(module, fieldDescs);
			return fieldDescs;
		}
		return fieldDescsMap.get(module);
	}

	private Set<FieldParserDescription> getLastFieldDescs(String module){
		Set<FieldParserDescription> fieldDescs = CollectionUtils.toSet(getAllFields(module), field->new FieldParserDescription(field));
		return Collections.synchronizedSet(fieldDescs);
	}
	
	public void updateDynamicFiedDescriptionSet(String module) {
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
	public Map<String, Set<Label>> getAllLabelsMap() {
		Map<String, Set<Label>> labelsMap = new HashMap<>();
		Set<FusionContextConfig> configs = fFactory.getAllConfigs();
		configs.forEach(config->{
			labelsMap.put(config.getModule(), config.getAllLabels());
		});
		return labelsMap;
	}
	
	@Override
	public Map<String, Label> getModuleLabelMap(String module) {
		FusionContextConfig config = fFactory.getModuleConfig(module);
		return CollectionUtils.toMap(config.getAllLabels(), label->label.getFieldName());
	}
	
	
}
