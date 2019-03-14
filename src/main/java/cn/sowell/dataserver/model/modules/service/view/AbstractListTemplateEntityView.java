package cn.sowell.dataserver.model.modules.service.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import com.abc.util.ValueType;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.modules.exception.UnknowFieldException;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListColumn;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListTemplate;

public abstract class AbstractListTemplateEntityView<
		T extends AbstractListTemplate<COL, CRI>, 
		COL extends AbstractListColumn, 
		CRI extends AbstractListCriteria, EC extends EntityViewCriteria> extends EntityView<T, EC>{
	private Map<Long, DictionaryField> fieldMap;
	
	private Set<Long> disabledColumns = new HashSet<>();


	public AbstractListTemplateEntityView(T listTemplate, Map<Long, DictionaryField> fieldMap) {
		super();
		Assert.notNull(listTemplate);
		Assert.notNull(fieldMap);
		this.setListTemplate(listTemplate);
		this.fieldMap = fieldMap;
	}

	
	@Override
	public List<EntityColumn> getColumns() {
		List<EntityColumn> columns = new ArrayList<EntityColumn>();
		T listTemplate = getListTemplate();
		if(listTemplate.getColumns() != null) {
			int i = 0;
			for (COL tColumn : listTemplate.getColumns()) {
				if(!disabledColumns.contains(tColumn.getId()) && !TextUtils.hasText(tColumn.getSpecialField())) {
					
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
						public Long getFieldId() {
							return tColumn.getFieldId();
						}

						@Override
						public String getFieldInputType() {
							return getFieldDependency().getType();
						}
						
						@Override
						public ValueType getFieldType() {
							return ValueType.getValueType(getFieldDependency().getAbcType());
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

	public Set<Long> getDisabledColumns() {
		return disabledColumns;
	}
	
	public List<COL> getEnabledColumns(){
		return getListTemplate().getColumns().stream()
				.filter((col)->!disabledColumns.contains(col.getId()))
				.collect(Collectors.toList());
	}
	

}
