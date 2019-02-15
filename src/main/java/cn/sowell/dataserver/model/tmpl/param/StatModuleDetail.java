package cn.sowell.dataserver.model.tmpl.param;

import java.util.List;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatView;

public class StatModuleDetail {

	private List<TemplateStatView> views;

	public void setViews(List<TemplateStatView> views) {
		this.views = views;
	}

	public List<TemplateStatView> getViews() {
		return views;
	}

}
