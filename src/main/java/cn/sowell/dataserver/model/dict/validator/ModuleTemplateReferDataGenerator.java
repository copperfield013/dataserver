package cn.sowell.dataserver.model.dict.validator;

import cn.sowell.dataserver.model.cachable.prepare.ModuleTemplateReferData;

public interface ModuleTemplateReferDataGenerator {
	ModuleTemplateReferData generate(String moduleName);
}
