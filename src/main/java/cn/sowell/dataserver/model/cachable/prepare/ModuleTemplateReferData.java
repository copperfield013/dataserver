package cn.sowell.dataserver.model.cachable.prepare;

import java.util.Map;
import java.util.Set;

import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;

public class ModuleTemplateReferData {
	private Map<Integer, DictionaryComposite> compositeMap;
	private Map<Integer, DictionaryField> fieldMap;
	private FusionContextConfig fusionContextConfig;
	private Map<String, Set<String>> fieldInputTypeMap;
	public Map<Integer, DictionaryComposite> getCompositeMap() {
		return this.compositeMap;
	}
	public Map<String, Set<String>> getFieldInputTypeMap() {
		return this.fieldInputTypeMap;
	}
	public Map<Integer, DictionaryField> getFieldMap() {
		return this.fieldMap;
	}
	public boolean getEntityWriatble() {
		return this.fusionContextConfig.getConfigResolver().isEntityWritable();
	}
	public FusionContextConfig getFusionContextConfig() {
		return this.fusionContextConfig;
	}
	public void setCompositeMap(Map<Integer, DictionaryComposite> compositeMap) {
		this.compositeMap = compositeMap;
	}
	public void setFieldMap(Map<Integer, DictionaryField> fieldMap) {
		this.fieldMap = fieldMap;
	}
	public void setFusionContextConfig(FusionContextConfig fusionContextConfig) {
		this.fusionContextConfig = fusionContextConfig;
	}
	public void setFieldInputTypeMap(Map<String, Set<String>> fieldInputTypeMap) {
		this.fieldInputTypeMap = fieldInputTypeMap;
	}
	public String getModuleName() {
		return fusionContextConfig.getModule();
	}
}
