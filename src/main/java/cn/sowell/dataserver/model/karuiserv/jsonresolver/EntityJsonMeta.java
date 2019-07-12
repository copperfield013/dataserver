package cn.sowell.dataserver.model.karuiserv.jsonresolver;

import java.util.ArrayList;
import java.util.List;

public class EntityJsonMeta {
	List<JsonMetaField> fields = new ArrayList<JsonMetaField>();

	public List<JsonMetaField> getFields() {
		return fields;
	}

	public void setFields(List<JsonMetaField> fields) {
		this.fields = fields;
	}
}
