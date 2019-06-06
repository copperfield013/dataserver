package cn.sowell.dataserver.model.modules.service.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.abc.mapping.entity.RecordEntity;
import com.abc.model.enun.AttributeValueType;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.sowell.datacenter.entityResolver.CEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.Label;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.dataserver.model.dict.pojo.OptionItem;
import cn.sowell.dataserver.model.modules.exception.UnknowFieldException;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListTemplate;

public class EntityView<LT extends AbstractListTemplate<?, ?>, EC extends EntityViewCriteria> {
	private List<? extends RecordEntity> entities;
	private List<? extends CEntityPropertyParser> parsers;
	
	private LT listTemplate;
	@SuppressWarnings("unchecked")
	private EC criteria = (EC) new EntityViewCriteria();
	
	private ModuleMeta module;
	
	public EntityView() {
	}
	
	private List<EntityColumn> columns;
	public List<EntityColumn> getColumns() {
		return columns;
	}
	
	public void setEntities(List<? extends RecordEntity> entities) {
		this.entities = entities;
	}
	
	
	public void setCriteria(EC criteria) {
		this.criteria = criteria;
	}
	
	public void setModule(ModuleMeta module) {
		this.module = module;
	}
	
	public List<? extends RecordEntity> getEntities() {
		return entities;
	}
	
	public EC getCriteria() {
		return criteria;
	}

	public ModuleMeta getModule() {
		return module;
	}
	
	
	private Map<Long, List<OptionItem>> criteriaOptionMap = new HashMap<>();
	private Map<String, Label> criteriaLabelMap = new HashMap<>();
	
	public Map<Long, List<OptionItem>> getCriteriaOptionMap() {
		return criteriaOptionMap;
	}

	public void setCriteriaOptionMap(Map<Long, List<OptionItem>> criteriaOptionMap) {
		this.criteriaOptionMap = criteriaOptionMap;
	}

	public Map<String, Label> getCriteriaLabelMap() {
		return criteriaLabelMap;
	}

	public void setCriteriaLabelMap(Map<String, Label> criteriaLabelMap) {
		this.criteriaLabelMap = criteriaLabelMap;
	}
	
	public List<EntityRow> getRows(){
		List<EntityRow> list = new ArrayList<EntityRow>();
		List<? extends CEntityPropertyParser> parsers = getParsers();
		List<EntityColumn> columns = getColumns();
		for (int i = 0; i < parsers.size(); i++) {
			CEntityPropertyParser parser = parsers.get(i);
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
					if(parser instanceof ModuleEntityPropertyParser) {
						return ((ModuleEntityPropertyParser) parser).getTitle();
					}else {
						return null;
					}
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
		AttributeValueType getFieldType();
		String getFieldFormat();
		String getFieldInputType();
		String getTitle();
		EntityCell getCell(int rowIndex);
		Long getColumnId();
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

	public List<? extends CEntityPropertyParser> getParsers() {
		return parsers;
	}

	public void setParsers(List<? extends CEntityPropertyParser> parsers) {
		this.parsers = parsers;
	}

	public LT getListTemplate() {
		return listTemplate;
	}

	public void setListTemplate(LT listTemplate) {
		this.listTemplate = listTemplate;
	}

}

