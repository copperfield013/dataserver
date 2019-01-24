package cn.sowell.dataserver.model.cachable.prepare;

import java.util.HashMap;
import java.util.Map;

public class PreparedToCache {
	private Map<String, Object> map = new HashMap<>();
	private ModuleTemplateReferData referData;
	
	public Object get(String key) {
		return map.get(key);
	}
	
	public <T> void set(String key, T value) {
		map.put(key, value);
	}

	public ModuleTemplateReferData getReferData() {
		return referData;
	}

	public void setReferData(ModuleTemplateReferData referData) {
		this.referData = referData;
	}
	
}
