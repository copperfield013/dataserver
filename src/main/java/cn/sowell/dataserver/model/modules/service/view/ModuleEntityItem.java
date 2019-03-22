package cn.sowell.dataserver.model.modules.service.view;

import com.alibaba.fastjson.annotation.JSONField;

import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;

public class ModuleEntityItem extends EntityItem{
	public ModuleEntityItem(ModuleEntityPropertyParser parser) {
		super(parser);
	}
	@Override
	public ModuleEntityPropertyParser getParser() {
		return (ModuleEntityPropertyParser) super.getParser();
	}
	
	@JSONField(name="entityTitle")
	public String getEntityTitle() {
		return getParser().getTitle();
	}
	
	@JSONField(name="entityCode")
	public String getEntityCode() {
		return getParser().getCode();
	}
}
