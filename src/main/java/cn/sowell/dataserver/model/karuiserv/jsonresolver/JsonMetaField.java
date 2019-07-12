package cn.sowell.dataserver.model.karuiserv.jsonresolver;

public abstract class JsonMetaField {
	private String name;
	private JsonMetaField parent;
	private String desc;
	private boolean disabled = false;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public JsonMetaField getParent() {
		return parent;
	}
	public void setParent(JsonMetaField parent) {
		this.parent = parent;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public boolean getDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}
