package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;

public class FilteredDictionary {
	private static final SerializeConfig config;
	static {
		config = new SerializeConfig();
		SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
		filter.getExcludes().add("fields");
		config.addFilter(DictionaryComposite.class, filter);
	}
	
	private final Map<DictionaryComposite, List<DictionaryField>> fieldMap = new LinkedHashMap<>();
	private Map<DictionaryComposite, String> compositeTitleCover = new HashMap<>();
	private Map<DictionaryField, String> fieldTitleCoverMap = new HashMap<>();
	
	public FilteredDictionary(Map<DictionaryComposite, List<DictionaryField>> matched) {
		this.fieldMap.putAll(matched);
	}

	public JSONArray toJson() {
		JSONArray result = new JSONArray();
		fieldMap.forEach((composite, fields)->{
			JSONObject jComposite = parseJson(composite);
			JSONArray jFields = new JSONArray();
			fields.forEach(field->{
				jFields.add(parseJson(field));
			});
			jComposite.put("fields", jFields);
			result.add(jComposite);
		});
		return result;
	}

	private Object parseJson(DictionaryField field) {
		JSONObject jField = (JSONObject) JSON.toJSON(field);
		String fieldTitle = this.fieldTitleCoverMap.get(field);
		if(TextUtils.hasText(fieldTitle)) {
			jField.put("title", fieldTitle);
		}
		return jField;
	}

	private JSONObject parseJson(DictionaryComposite composite) {
		JSONObject jComposite = (JSONObject) JSON.toJSON(composite, config);
		String fieldTitle = this.compositeTitleCover.get(composite);
		if(TextUtils.hasText(fieldTitle)) {
			jComposite.put("title", fieldTitle);
		}
		return jComposite;
	}

	public Map<DictionaryComposite, String> getCompositeTitleCover() {
		return compositeTitleCover;
	}

	public Map<DictionaryField, String> getFieldTitleCoverMap() {
		return fieldTitleCoverMap;
	}
	
	
	
}
