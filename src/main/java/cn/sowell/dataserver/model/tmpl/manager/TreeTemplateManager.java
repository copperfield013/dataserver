package cn.sowell.dataserver.model.tmpl.manager;

import java.util.List;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeTemplate;

public interface TreeTemplateManager extends ModuleCachableManager<TemplateTreeTemplate>{

	List<TemplateTreeTemplate> queryByNodeModule(String nodeModule);

}
