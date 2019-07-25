package cn.sowell.dataserver.model.dict.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.pojo.DictionaryOption;
import cn.sowell.dataserver.model.dict.pojo.DictionaryRelationLabels;
import cn.sowell.dataserver.model.dict.pojo.OptionItem;

public interface DictionaryDao {

	/**
	 * 根据模块获得所有可用字段组
	 * @param m
	 * @return
	 */
	List<DictionaryComposite> getAllComposites(String m);

	/**
	 * 根据字段组获得其对应的所有可用字段
	 * @param compositeIds
	 * @return
	 */
	Map<Integer, List<DictionaryField>> getAllFields(Set<Integer> compositeIds);

	public List<DictionaryOption> getAllOptions();

	Map<Integer, List<OptionItem>> getFieldOptionsMap(Set<Integer> fieldIds);

	Map<Integer, DictionaryField> getFieldMap(Set<Integer> fieldIds);

	Map<Integer, DictionaryRelationLabels> getRelationSubdomainMap(Set<Integer> compositeIds);

	List<DictionaryOption> queryOptions(Integer optGroupId);

	List<DictionaryComposite> getAllComposites();

	List<DictionaryComposite> getAllComposites(Set<String> moduleNames);
}
