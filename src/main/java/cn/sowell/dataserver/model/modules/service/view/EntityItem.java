package cn.sowell.dataserver.model.modules.service.view;

import com.alibaba.fastjson.annotation.JSONField;

import cn.sowell.datacenter.entityResolver.CEntityPropertyParser;

public class EntityItem {

	@JSONField(serialize = false)
	protected CEntityPropertyParser parser;
	
	private boolean uncheckable = false;

	public EntityItem(CEntityPropertyParser parser) {
		this.parser = parser;
	}

	public CEntityPropertyParser getParser() {
		return parser;
	}

	@JSONField(name="code")
	public String getCode() {
		return parser.getCode();
	}

	public boolean getUncheckable() {
		return uncheckable;
	}

	public void setUncheckable(boolean uncheckable) {
		this.uncheckable = uncheckable;
	}

}