package cn.sowell.dataserver.model.dict.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sowell.datacenter.entityResolver.Label;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryCompositeExpand;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.pojo.DictionaryOption;
import cn.sowell.dataserver.model.dict.pojo.OptionItem;

public interface DictionaryService {

	List<DictionaryComposite> getAllComposites(String module);
	
	List<DictionaryField> getAllFields(String module);
	
	List<DictionaryOption> getAllOptions();

	public Map<Long, List<OptionItem>> getOptionsMap(Set<Long> fieldIds);

	/**
	 * 从所有配置文件中获得所有label字段的map
	 * @return
	 */
	Map<String, Set<Label>> getAllLabelsMap();

	DictionaryComposite getCurrencyCacheCompositeByFieldId(String module, Long fieldId);

	Map<String, Label> getModuleLabelMap(String module);
	
	Map<String, Label> getModuleLabelMap(String module, Set<String> criteriaFieldNames);

	DictionaryField getField(String module, Long fieldId);

	Map<Long, DictionaryField> getFieldMap(String module, Set<Long> fieldIds);

	Map<String, Set<String>> getFieldInputTypeMap();

	/**
	 * 根据id获得composite对象
	 * @param module
	 * @param compositeId
	 * @return
	 */
	DictionaryComposite getComposite(String module, Long compositeId);

	/**
	 * 
	 * @param optGroupId
	 * @return
	 */
	List<DictionaryOption> queryOptions(Long optGroupId);

	Set<String> getCompositeClasses(String moduleName, Long compositeId);

	/**
	 * 获得字段在目标模块中的字段对象
	 * @param targetModuleName
	 * @param originField
	 * @return
	 */
	DictionaryField mapModuleField(String targetModuleName, DictionaryField originField);
	
	DictionaryComposite mapModuleComposite(String targetModuleName, DictionaryComposite composite);

	Map<String, List<DictionaryComposite>> getAllCompositesMap(Set<String> moduleNames);

	Map<String, List<DictionaryField>> getAllFields(Set<String> moduleNames);

	Map<Long, DictionaryCompositeExpand> getCompositeExpandMap(String moduleName, Set<Long> compositeIds);


}
