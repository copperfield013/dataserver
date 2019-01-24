package cn.sowell.dataserver.model.tmpl.service;

import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.tmpl.duplicator.TemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;

public interface SelectionTemplateService extends OpenTemplateService<TemplateSelectionTemplate>, TemplateDuplicator<DictionaryComposite>{

}
