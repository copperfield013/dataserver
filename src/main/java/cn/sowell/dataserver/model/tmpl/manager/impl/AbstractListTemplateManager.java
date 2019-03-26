package cn.sowell.dataserver.model.tmpl.manager.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.sowell.dataserver.model.cachable.manager.AbstractModuleCacheManager;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.tmpl.dao.OpenListTemplateDao;
import cn.sowell.dataserver.model.tmpl.manager.ModuleCachableManager;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToListTemplate;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToListTemplate.PreparedToListTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListColumn;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListTemplate;
import cn.sowell.dataserver.model.tmpl.service.ListCriteriaFactory;
import cn.sowell.dataserver.model.tmpl.strategy.NormalDaoSetUpdateStrategy;

public abstract class AbstractListTemplateManager<LT extends AbstractListTemplate<COL, CRI>, COL extends AbstractListColumn, CRI extends AbstractListCriteria> 
		extends AbstractModuleCacheManager<LT, OpenListTemplateDao<LT, COL, CRI>, GlobalPreparedToListTemplate<COL, CRI>, PreparedToListTemplate<COL, CRI>> 
		implements ModuleCachableManager<LT>{

	@Resource
	ListCriteriaFactory lcFactory;
	
	protected AbstractListTemplateManager(OpenListTemplateDao<LT, COL, CRI> dao,
			ModuleCachableMetaSupportor metaSupportor) {
		super(dao, metaSupportor);
	}

	@Override
	protected GlobalPreparedToListTemplate<COL, CRI> getGlobalPreparedToCache() {
		Map<Long, List<COL>> columnsMap = getDao().queryColumnsMap();
		Map<Long, List<CRI>> criteriasMap = getDao().queryCriteriasMap();
		GlobalPreparedToListTemplate<COL, CRI> gp = new GlobalPreparedToListTemplate<COL, CRI>();
		gp.setColumnsMap(columnsMap);
		gp.setCriteriasMap(criteriasMap);
		return gp;
	}

	@Override
	protected PreparedToListTemplate<COL, CRI> extractPrepare(
			GlobalPreparedToListTemplate<COL, CRI> globalPreparedToCache, LT cachable) {
		List<COL> columns = globalPreparedToCache.getColumnsMap().get(cachable.getId());
		List<CRI> criterias = globalPreparedToCache.getCriteriasMap().get(cachable.getId());
		PreparedToListTemplate<COL, CRI> prepared = new PreparedToListTemplate<>();
		prepared.setCriterias(criterias);
		prepared.setColumns(columns);
		return prepared ; 
	}

	@Override
	protected PreparedToListTemplate<COL, CRI> getPreparedToCache(LT cachable) {
		PreparedToListTemplate<COL, CRI> prepared = new PreparedToListTemplate<>();
		List<COL> columns = getDao().getColumnsByTmplId(cachable.getId());
		List<CRI> criterias = getDao().getCriteriaByTmplId(cachable.getId());
		prepared.setColumns(columns);
		prepared.setCriterias(criterias);
		return prepared;
	}

	@Override
	protected void handlerCache(LT latest, PreparedToListTemplate<COL, CRI> prepareToCache) {
		beforeHandleCache(latest, prepareToCache);
		Map<Long, DictionaryField> fieldMap = prepareToCache.getReferData().getFieldMap();
		if(prepareToCache.getColumns() != null) {
			prepareToCache.getColumns().forEach(column->{
				if(column.getSpecialField() == null) {
					DictionaryField field = fieldMap.get(column.getFieldId());
					if(field != null) {
						column.setFieldKey(field.getFullKey());
					}else {
						column.setFieldUnavailable();
					}
				}
			});
			latest.setColumns(prepareToCache.getColumns());
		}
		if(prepareToCache.getCriterias() != null) {
			prepareToCache.getCriterias().forEach(criteria->{
				if(criteria.getFieldId() != null) {
					DictionaryField field = fieldMap.get(criteria.getFieldId());
					if(field != null) {
						if(getMetaSupportor().supportFieldInputType(criteria.getInputType(), field.getType(), prepareToCache.getReferData().getFieldInputTypeMap())) {
							criteria.setFieldKey(field.getFullKey());
							//只有字段存在并且字段当前类型支持当前条件的表单类型，该条件字段才可用
							//(因为条件的表单类型是创建模板时选择的，与字段类型不同，防止字段修改了类型但与条件表单类型不匹配)
							return;
						}
					}
					criteria.setFieldUnavailable();
				}
				whenCriteriaFieldNull(criteria, prepareToCache);
			});
			latest.setCriterias(prepareToCache.getCriterias());
		}
	}

	protected void beforeHandleCache(LT latest, PreparedToListTemplate<COL, CRI> prepareToCache) {
	}

	protected void whenCriteriaFieldNull(CRI criteria, PreparedToListTemplate<COL, CRI> prepareToCache) {
	}
	
	@Override
	protected Long doCreate(LT template) {
		if(template.getId() == null){
			Date now = new Date();
			//创建
			template.setCreateTime(now);
			template.setUpdateTime(now);
			Long tmplId = getDao().getNormalOperateDao().save(template);
			List<COL> columns = template.getColumns();
			for (COL column : columns) {
				column.setTemplateId(tmplId);
				column.setCreateTime(now);
				column.setUpdateTime(now);
				getDao().getNormalOperateDao().save(column);
			}
			List<CRI> criterias = template.getCriterias();
			for (CRI criteria : criterias) {
				criteria.setTemplateId(tmplId);
				criteria.setCreateTime(now);
				criteria.setUpdateTime(now);
				getDao().getNormalOperateDao().save(criteria);
			}
			return tmplId;
		}
		return null;
	}

	@Override
	protected void doUpdate(LT template) {
		LT origin = get(template.getId());
		if(origin != null){
			origin.setTitle(template.getTitle());
			origin.setDefaultPageSize(template.getDefaultPageSize());
			origin.setDefaultOrderFieldId(template.getDefaultOrderFieldId());
			origin.setDefaultOrderDirection(template.getDefaultOrderDirection());
			updateTemplate(origin, template);
			getDao().getNormalOperateDao().update(origin);
			Date now = new Date();
			
			NormalDaoSetUpdateStrategy.build(
					getDao().getListColumnClass(), getDao().getNormalOperateDao(),
					column->column.getId(),
					(oColumn, column)->{
						oColumn.setTitle(column.getTitle());
						oColumn.setFieldKey(column.getFieldKey());
						oColumn.setFieldId(column.getFieldId());
						oColumn.setOrder(column.getOrder());
						oColumn.setOrderable(column.getOrderable());
						oColumn.setSpecialField(column.getSpecialField());
						oColumn.setUpdateTime(now);
						oColumn.setViewOption(column.getViewOption());
						updateColumn(oColumn, column);
					},column->{
						column.setCreateTime(now);
						column.setUpdateTime(now);
						column.setTemplateId(origin.getId());
					})
				.doUpdate(new HashSet<>(origin.getColumns()), new HashSet<>(template.getColumns()));
			
			NormalDaoSetUpdateStrategy.build(
				getDao().getListCriteriaClass(), getDao().getNormalOperateDao(), 
				criteria->criteria.getId(), 
				(originCriteria, criteria)->{
					lcFactory.coverAbsCriteriaForUpdate(originCriteria, criteria);
					updateCriteria(originCriteria, criteria);
				}, criteria->{
					criteria.setCreateTime(now);
					criteria.setUpdateTime(now);
					criteria.setTemplateId(origin.getId());
				})
			.doUpdate(new LinkedHashSet<>(origin.getCriterias()), new LinkedHashSet<>(template.getCriterias()));
		}else{
			throw new RuntimeException(template.getClass().getSimpleName() + "模板[id=" + template.getId() + "]不存在");
		}
	}

	protected void updateTemplate(LT origin, LT template) {
	}
	protected void updateColumn(COL originColumn, COL column) {
	}
	protected void updateCriteria(CRI originCriteria, CRI criteria) {
	}
	
}
