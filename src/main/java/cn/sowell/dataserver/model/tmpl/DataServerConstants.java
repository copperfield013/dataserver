package cn.sowell.dataserver.model.tmpl;

import cn.sowell.dataserver.model.tmpl.pojo.Cachable;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

public interface DataServerConstants {

	String TEMPLATE_TYPE_DETAIL = "detail";
	String TEMPLATE_TYPE_LIST = "list";
	static String mapTemplateType(Class<? extends Cachable> templateClass) {
		if(TemplateListTemplate.class.equals(templateClass)) {
			return TEMPLATE_TYPE_LIST;
		}else if(TemplateDetailTemplate.class.equals(templateClass)) {
			return TEMPLATE_TYPE_DETAIL;
		}
		throw new RuntimeException(templateClass + "的类型没有被维护");
	}

}
