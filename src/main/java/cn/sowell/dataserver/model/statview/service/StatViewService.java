package cn.sowell.dataserver.model.statview.service;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.dataserver.model.modules.service.view.StatListTemplateEntityView;
import cn.sowell.dataserver.model.statview.pojo.StatCriteria;
import cn.sowell.dataserver.model.tmpl.param.StatModuleDetail;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatView;
import cn.sowell.dataserver.model.tmpl.service.OpenTemplateService;

public interface StatViewService extends OpenTemplateService<TemplateStatView>{

	Map<String, StatModuleDetail> getStatModuleDetail(Set<String> modules);

	void recalc(String moduleName, UserIdentifier user);

	StatListTemplateEntityView stat(StatCriteria criteria);
	
	void bindStatViewReloadEvent(Consumer<TemplateStatView> consumer);

}
