package cn.sowell.dataserver.model.dict.pojo;

import java.util.Set;

public class DictionaryRelationLabels {
	private Long relationId;
	private Long labelId;
	private Set<String> labels;
	private String access;
	public Long getRelationId() {
		return relationId;
	}
	public void setRelationId(Long relationId) {
		this.relationId = relationId;
	}
	public Long getLabelId() {
		return labelId;
	}
	public void setLabelId(Long labelId) {
		this.labelId = labelId;
	}
	public Set<String> getLabels() {
		return labels;
	}
	public void setLabels(Set<String> labels) {
		this.labels = labels;
	}
	public String getAccess() {
		return access;
	}
	public void setAccess(String access) {
		this.access = access;
	}
}
