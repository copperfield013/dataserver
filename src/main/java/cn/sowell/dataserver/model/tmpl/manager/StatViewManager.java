package cn.sowell.dataserver.model.tmpl.manager;

import java.util.function.Consumer;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatView;

public interface StatViewManager extends ModuleCachableManager<TemplateStatView>{

	void bindStatViewReloadEvent(Consumer<TemplateStatView> consumer);

}
