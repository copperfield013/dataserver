package cn.sowell.dataserver.model.modules.service.view;

import java.util.Map;

import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

public class ListTemplateEntityView extends 
	AbstractListTemplateEntityView<TemplateListTemplate, TemplateListColumn, TemplateListCriteria, ListTemplateEntityViewCriteria>{

	public ListTemplateEntityView(TemplateListTemplate listTemplate, Map<Long, DictionaryField> fieldMap) {
		super(listTemplate, fieldMap);
	}

}
