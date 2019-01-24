package cn.sowell.dataserver.model.dict.validator;

import java.util.Map;
import java.util.Set;

import cn.sowell.dataserver.model.cachable.prepare.ModuleTemplateReferData;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;

public interface ModuleCachableMetaSupportor {

	String getRelationLabelAccess(DictionaryComposite composite, boolean entityWriatble);

	String getAdditionRelationLabelAccess(DictionaryComposite composite, boolean entityWriatble);

	String getFieldAccess(DictionaryField field, boolean moduleEntityWritable);

	String getFieldAdditionAccess(DictionaryField field, boolean moduleEntityWritable);

	ModuleTemplateReferData getModuleTemplateReferData(String module);

	boolean supportFieldInputType(String inputType, String type, Map<String, Set<String>> fieldInputTypeMap);

	boolean checkModule(String moduleName);

	ModuleTemplateReferDataGenerator getTemplateReferDataGenetator();

}
