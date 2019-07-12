package cn.sowell.dataserver.model.karuiserv.match;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cn.sowell.dataserver.model.karuiserv.pojo.KaruiServ;

public class KaruiServMatcher {
	private KaruiServ karuiServ;
	private Map<String, String> pathVariableMap;
	private Map<String, String> parameters;
	public KaruiServ getKaruiServ() {
		return karuiServ;
	}
	public void setKaruiServ(KaruiServ karuiServ) {
		this.karuiServ = karuiServ;
	}
	public Map<String, String> getPathVariableMap() {
		return pathVariableMap;
	}
	public void setPathVariableMap(Map<String, String> pathVariableMap) {
		this.pathVariableMap = pathVariableMap;
	}
	public JSONObject getKaruiServJson() {
		JSONObject json = new JSONObject();
		json.put("id", karuiServ.getId());
		json.put("title", this.karuiServ.getTitle());
		json.put("path", this.karuiServ.getPath());
		json.put("description", this.karuiServ.getDescription());
		json.put("module", this.karuiServ.getModule());
		json.put("resMeta", this.karuiServ.getResponseMeta());
		return json;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
}
