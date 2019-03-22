package cn.sowell.dataserver.model.modules.service.view;

import java.util.List;

import cn.sowell.dataserver.model.dict.pojo.OptionItem;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListCriteria;

public class ViewListCriteria<CRI extends AbstractListCriteria> {

	private CRI tCriteria;
	private String requestValue;
	private String value;
	private List<OptionItem> selectOptions;
	private List<String> selectLabels;
	
	ViewListCriteria(CRI tCriteria2) {
		this.tCriteria = tCriteria2;
	}
	
	public CRI getTemplateCriteria() {
		return tCriteria;
	}

	public String getRequestValue() {
		return requestValue;
	}

	void setRequestValue(String requestValue) {
		this.requestValue = requestValue;
	}

	public String getValue() {
		return value;
	}

	void setValue(String value) {
		this.value = value;
	}
	
	public boolean getIsShown() {
		return tCriteria.getQueryShow() != null;
	}

	public List<OptionItem> getSelectOptions() {
		return selectOptions;
	}

	void setSelectOptions(List<OptionItem> selectOptions) {
		this.selectOptions = selectOptions;
	}

	public List<String> getSelectLabels() {
		return this.selectLabels;
	}

	public void setSelectLabels(List<String> selectLabels) {
		this.selectLabels = selectLabels;
	}
}
