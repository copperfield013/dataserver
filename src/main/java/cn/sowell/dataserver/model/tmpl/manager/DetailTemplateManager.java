package cn.sowell.dataserver.model.tmpl.manager;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;

public interface DetailTemplateManager extends ModuleCachableManager<TemplateDetailTemplate>{

	TemplateDetailFieldGroup getFieldGroup(Long groupId);

}
