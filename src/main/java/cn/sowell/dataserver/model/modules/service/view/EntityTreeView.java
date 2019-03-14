package cn.sowell.dataserver.model.modules.service.view;

import java.util.List;

import com.abc.mapping.entity.Entity;
import com.alibaba.fastjson.JSONObject;

import cn.sowell.datacenter.entityResolver.CEntityPropertyParser;

public class EntityTreeView{
	private List<Entity> entities;
	private List<? extends CEntityPropertyParser> parsers;
	
	private EntityTreeViewCriteria criteria;
	
	public JSONObject toJson() {
		JSONObject jo = new JSONObject();
		return jo;
	}
	
	public List<? extends CEntityPropertyParser> getParsers(){
		return parsers;
	}
	
	
}
	
	
	
