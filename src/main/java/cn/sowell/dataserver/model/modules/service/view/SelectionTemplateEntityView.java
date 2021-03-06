package cn.sowell.dataserver.model.modules.service.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import cho.carbon.meta.enun.AttributeValueType;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.modules.exception.UnknowFieldException;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;

public class SelectionTemplateEntityView extends EntityView<TemplateSelectionTemplate, SelectionTemplateEntityViewCriteria>{

	private Map<Integer, DictionaryField> fieldMap;
	
	public SelectionTemplateEntityView(TemplateSelectionTemplate selectionTemplate, Map<Integer, DictionaryField> fieldMap) {
		super();
		Assert.notNull(selectionTemplate);
		Assert.notNull(fieldMap);
		this.setListTemplate(selectionTemplate);
		this.fieldMap = fieldMap;
	}

	
	@Override
	public List<EntityColumn> getColumns() {
		List<EntityColumn> columns = new ArrayList<EntityColumn>();
		TemplateSelectionTemplate selectionTemplate = getListTemplate();
		if(selectionTemplate.getColumns() != null) {
			int i = 0;
			for (TemplateSelectionColumn tColumn : selectionTemplate.getColumns()) {
				if(!TextUtils.hasText(tColumn.getSpecialField())) {
					
					final int index = i;
					EntityColumn column = new EntityColumn() {
						
						private DictionaryField getFieldDependency() {
							if(!getEffective()) {
								throw new UnknowFieldException(getFieldId());
							}
							return fieldMap.get(getFieldId());
						}
						
						@Override
						public String getTitle() {
							return tColumn.getTitle();
						}
						
						@Override
						public int getIndex() {
							return index;
						}
						
						@Override
						public String getFieldName() {
							return getFieldDependency().getFullKey();
						}
						
						@Override
						public Integer getFieldId() {
							return tColumn.getFieldId();
						}

						@Override
						public String getFieldInputType() {
							return getFieldDependency().getType();
						}
						
						@Override
						public AttributeValueType getFieldType() {
							return AttributeValueType.getType(getFieldDependency().getAbcType());
						}

						@Override
						public String getFieldFormat() {
							return null;
						}

						@Override
						public boolean getEffective() {
							return fieldMap.containsKey(tColumn.getFieldId());
						}
						
						@Override
						public EntityCell getCell(int rowIndex) {
							EntityRow row = getRows().get(rowIndex);
							if(row != null) {
								return row.getCell(getIndex());
							}
							return null;
						}

						@Override
						public Long getColumnId() {
							return tColumn.getId();
						}
					};
					columns.add(i++, column);
				}
			}
			
		}
		return columns;
	}
}
