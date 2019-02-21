package cn.sowell.dataserver.model.dict.pojo;

import java.util.Set;

public class DictionaryCompositeExpand {
	private Set<String> dataClasses;
	private String rabcModule;
	public Set<String> getDataClasses() {
		return dataClasses;
	}
	public void setDataClasses(Set<String> dataClasses) {
		this.dataClasses = dataClasses;
	}
	public String getRabcModule() {
		return rabcModule;
	}
	public void setRabcModule(String rabcModule) {
		this.rabcModule = rabcModule;
	}
	
}
