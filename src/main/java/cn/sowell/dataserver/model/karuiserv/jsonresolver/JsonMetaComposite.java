package cn.sowell.dataserver.model.karuiserv.jsonresolver;

import java.util.ArrayList;
import java.util.List;

public class JsonMetaComposite extends JsonMetaField{
	public static final String TYPE_NORMAL = "normal";
	public static final String TYPE_ARRAY = "array";
	private String type;
	private Long dtmplCompositeId;
	private Long compositeId;
	private List<JsonMetaField> fields = new ArrayList<JsonMetaField>();
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getDtmplCompositeId() {
		return dtmplCompositeId;
	}
	public void setDtmplCompositeId(Long dtmplCompositeId) {
		this.dtmplCompositeId = dtmplCompositeId;
	}
	public Long getCompositeId() {
		return compositeId;
	}
	public void setCompositeId(Long compositeId) {
		this.compositeId = compositeId;
	}
	public List<JsonMetaField> getFields() {
		return fields;
	}
	public void setFields(List<JsonMetaField> fields) {
		this.fields = fields;
	}
}
