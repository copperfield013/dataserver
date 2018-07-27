package cn.sowell.dataserver.model.modules.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.abc.mapping.entity.Entity;
import com.abc.util.ValueType;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.sowell.datacenter.entityResolver.Label;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.dataserver.model.dict.pojo.OptionItem;
import cn.sowell.dataserver.model.modules.exception.UnknowFieldException;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;

public class EntityView {
	private List<Entity> entities;
	private List<ModuleEntityPropertyParser> parsers;
	
	private EntityViewCriteria criteria = new EntityViewCriteria();
	
	private ModuleMeta module;
	
	EntityView() {
	}
	
	private List<EntityColumn> columns;
	protected List<EntityColumn> getColumns() {
		return columns;
	}
	
	void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
	void setParsers(List<ModuleEntityPropertyParser> parsers) {
		this.parsers = parsers;
	}
	
	void setCriteria(EntityViewCriteria criteria) {
		this.criteria = criteria;
	}
	
	void setModule(ModuleMeta module) {
		this.module = module;
	}
	
	public List<Entity> getEntities() {
		return entities;
	}
	
	public EntityViewCriteria getCriteria() {
		return criteria;
	}

	public ModuleMeta getModule() {
		return module;
	}
	
	public List<ModuleEntityPropertyParser> getParsers(){
		return parsers;
	}
	
	private Map<Long, List<OptionItem>> criteriaOptionMap = new HashMap<>();
	private Map<String, Label> criteriaLabelMap = new HashMap<>();
	
	public Map<Long, List<OptionItem>> getCriteriaOptionMap() {
		return criteriaOptionMap;
	}

	void setCriteriaOptionMap(Map<Long, List<OptionItem>> criteriaOptionMap) {
		this.criteriaOptionMap = criteriaOptionMap;
	}

	public Map<String, Label> getCriteriaLabelMap() {
		return criteriaLabelMap;
	}

	void setCriteriaLabelMap(Map<String, Label> criteriaLabelMap) {
		this.criteriaLabelMap = criteriaLabelMap;
	}
	
	public List<EntityRow> getRows(){
		List<EntityRow> list = new ArrayList<EntityRow>();
		List<ModuleEntityPropertyParser> parsers = getParsers();
		List<EntityColumn> columns = getColumns();
		for (int i = 0; i < parsers.size(); i++) {
			ModuleEntityPropertyParser parser = parsers.get(i);
			final int index = i;
			EntityRow row = new EntityRow() {

				@Override
				public int getIndex() {
					return index;
				}

				@Override
				public EntityCell getCell(int index) {
					EntityColumn column = columns.get(index);
					String fieldKey = column.getFieldName();
					if(fieldKey != null) {
						return getCell(fieldKey);
					}else {
						throw new UnknowFieldException(column.getFieldId());
					}
				}

				@Override
				public EntityCell getCell(String propertyName) {
					return new EntityCell() {
						
						@Override
						public String getText() {
							return parser.getFormatedProperty(propertyName);
						}
					};
				}

				@Override
				public int getCellCount() {
					return columns.size();
				}

				@Override
				public String getCode() {
					return parser.getCode();
				}

				@Override
				public String getTitle() {
					return parser.getTitle();
				}
				
			};
			list.add(row);
		}
		
		return list;
	}
	
	
	
	
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		JSONArray jRows = new JSONArray();
		List<EntityColumn> cols = getColumns();
		getParsers().forEach(parser->{
			JSONArray jRow = new JSONArray();
			jRows.add(jRow);
			cols.forEach(col->{
				jRow.add(parser.getFormatedProperty(col.getFieldName(), col.getFieldType(), col.getFieldFormat()));
			});
		});
		json.put("rows", jRows);
		return json;
	}
	
	public static interface EntityColumn{
		int getIndex();
		boolean getEffective();
		Long getFieldId();
		String getFieldName();
		ValueType getFieldType();
		String getFieldFormat();
		String getFieldInputType();
		String getTitle();
		EntityCell getCell(int rowIndex);
	}
	
	public static interface EntityRow{
		int getIndex();
		int getCellCount();
		EntityCell getCell(int index);
		EntityCell getCell(String propertyName);
		String getCode();
		String getTitle();
	}
	
	public static interface EntityCell{
		String getText();
	}


}

