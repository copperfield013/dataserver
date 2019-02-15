package cn.sowell.dataserver.model.modules.service.view;

import java.util.HashMap;
import java.util.Map;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;

public class SelectionTemplateEntityViewCriteria extends EntityViewCriteria{
	private Map<Long, String> selectionTemplateCriteria = new HashMap<>();
	private TemplateSelectionTemplate selectionTemplate;
	
	
	
	
	public SelectionTemplateEntityViewCriteria(TemplateSelectionTemplate selectionTemplate,
			Map<Long, String> listTemplateCriteria) {
		super();
		this.selectionTemplateCriteria = listTemplateCriteria;
		this.selectionTemplate = selectionTemplate;
		super.setModule(selectionTemplate.getModule());
		super.setRelationName(selectionTemplate.getRelationName());
		
	}

	public Map<Long, String> getSelectionTemplateCriteria() {
		return selectionTemplateCriteria;
	}


	public TemplateSelectionTemplate getSelectionTemplate() {
		return selectionTemplate;
	}

}
