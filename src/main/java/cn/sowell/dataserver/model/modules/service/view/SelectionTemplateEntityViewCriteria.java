package cn.sowell.dataserver.model.modules.service.view;

import java.util.Map;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;

public class SelectionTemplateEntityViewCriteria extends EntityViewCriteria{
	
	private TemplateSelectionTemplate selectionTemplate;
	
	public SelectionTemplateEntityViewCriteria(TemplateSelectionTemplate selectionTemplate,
			Map<Long, String> templateCriteria) {
		super();
		this.selectionTemplate = selectionTemplate;
		super.setModule(selectionTemplate.getModule());
		super.setRelationName(selectionTemplate.getRelationName());
		super.setTemplateCriteriaMap(templateCriteria);
	}


	public TemplateSelectionTemplate getSelectionTemplate() {
		return selectionTemplate;
	}

}
