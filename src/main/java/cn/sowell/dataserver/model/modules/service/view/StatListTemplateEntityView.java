package cn.sowell.dataserver.model.modules.service.view;

import java.util.Map;

import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatList;

public class StatListTemplateEntityView 
	extends AbstractListTemplateEntityView<TemplateStatList, TemplateStatColumn, TemplateStatCriteria, EntityViewCriteria>{

	public StatListTemplateEntityView(TemplateStatList listTemplate, Map<Integer, DictionaryField> fieldMap) {
		super(listTemplate, fieldMap);
		// TODO Auto-generated constructor stub
	}
	
	
}
