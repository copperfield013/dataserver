package cn.sowell.dataserver.model.modules.service.view;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.sowell.datacenter.entityResolver.CEntityPropertyParser;

public class ListEntityItem extends EntityItem{
	private Map<String, String> cellMap = new LinkedHashMap<String, String>();
	public ListEntityItem(CEntityPropertyParser parser) {
		super(parser);
	}
	public ListEntityItem putCell(String columnId, String cellValue) {
		cellMap.put(columnId, cellValue);
		return this;
	}
	public Map<String, String> getCellMap() {
		return cellMap;
	}
	
}
