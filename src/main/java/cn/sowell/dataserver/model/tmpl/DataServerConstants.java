package cn.sowell.dataserver.model.tmpl;

import cn.sowell.dataserver.model.tmpl.pojo.AbstractTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTempalte;

public interface DataServerConstants {

	String TEMPLATE_TYPE_DETAIL = "detail";
	String TEMPLATE_TYPE_LIST = "list";
	static String mapTemplateType(Class<? extends AbstractTemplate> templateClass) {
		if(TemplateListTempalte.class.equals(templateClass)) {
			return TEMPLATE_TYPE_LIST;
		}else if(TemplateDetailTemplate.class.equals(templateClass)) {
			return TEMPLATE_TYPE_DETAIL;
		}
		throw new RuntimeException(templateClass + "的类型没有被维护");
	}

}
