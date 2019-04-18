package cn.sowell.dataserver.model.modules.service.view;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;

public class ListModuleEntityItem extends ModuleEntityItem{

	private Map<String, String> cellMap = new LinkedHashMap<String, String>();
	
	public ListModuleEntityItem(ModuleEntityPropertyParser parser) {
		super(parser);
	}
	
	public ListModuleEntityItem putCell(String columnId, String cellValue) {
		cellMap.put(columnId, cellValue);
		return this;
	}
	public Map<String, String> getCellMap() {
		return cellMap;
	}
	
	

}
