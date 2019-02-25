package cn.sowell.dataserver.model.tmpl.manager.impl;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.sowell.dataserver.model.cachable.manager.AbstractModuleCacheManager;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.tmpl.dao.ArrayItemFilterDao;
import cn.sowell.dataserver.model.tmpl.manager.ArrayItemFilterManager;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToArrayItemFilter;
import cn.sowell.dataserver.model.tmpl.manager.prepared.GlobalPreparedToArrayItemFilter.PreparedToArrayItemFilter;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailArrayItemCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailArrayItemFilter;
import cn.sowell.dataserver.model.tmpl.strategy.NormalDaoSetUpdateStrategy;

@Component
public class ArrayItemFilterManagerImpl 
	extends AbstractModuleCacheManager<TemplateDetailArrayItemFilter, ArrayItemFilterDao, GlobalPreparedToArrayItemFilter, PreparedToArrayItemFilter>  
	implements ArrayItemFilterManager{

	@Autowired
	protected ArrayItemFilterManagerImpl(@Autowired ArrayItemFilterDao dao, @Autowired ModuleCachableMetaSupportor metaSupportor) {
		super(dao, metaSupportor);
	}

	@Override
	protected GlobalPreparedToArrayItemFilter getGlobalPreparedToCache() {
		GlobalPreparedToArrayItemFilter prepared = new GlobalPreparedToArrayItemFilter();
		prepared.setCriteriasMap(getDao().queryAllCriterias());
		return prepared; 
	}

	@Override
	protected PreparedToArrayItemFilter extractPrepare(GlobalPreparedToArrayItemFilter globalPreparedToCache,
			TemplateDetailArrayItemFilter cachable) {
		PreparedToArrayItemFilter prepared = new PreparedToArrayItemFilter();
		if(globalPreparedToCache != null && globalPreparedToCache.getCriteriasMap() != null) {
			prepared.setCriterias(globalPreparedToCache.getCriteriasMap().get(cachable.getId()));
		}
		return prepared ;
	}

	@Override
	protected PreparedToArrayItemFilter getPreparedToCache(TemplateDetailArrayItemFilter cachable) {
		PreparedToArrayItemFilter prepared = new PreparedToArrayItemFilter();
		prepared.setCriterias(getDao().queryCriterias(cachable.getId()));
		return prepared ;
	}

	@Override
	protected void handlerCache(TemplateDetailArrayItemFilter latest, PreparedToArrayItemFilter prepareToCache) {
		Map<Long, DictionaryField> fieldMap = prepareToCache.getReferData().getFieldMap();
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
			});
			latest.setCriterias(prepareToCache.getCriterias());
		}
	}

	@Override
	protected TemplateDetailArrayItemFilter createCachablePojo() {
		return new TemplateDetailArrayItemFilter();
	}

	@Override
	protected Long doCreate(TemplateDetailArrayItemFilter filter) {
		if(filter != null) {
			Date now = new Date();
			filter.setCreateTime(now);
			filter.setUpdateTime(now);
			Long filterId = getDao().getNormalOperateDao().save(filter);
			List<TemplateDetailArrayItemCriteria> criterias = filter.getCriterias();
			if(criterias != null) {
				for (TemplateDetailArrayItemCriteria criteria : criterias) {
					criteria.setTemplateId(filterId);
					criteria.setCreateTime(now);
					criteria.setUpdateTime(now);
					getDao().getNormalOperateDao().save(criteria);
				}
			}
			return filterId;
		}
		return null;
	}

	@Override
	protected void doUpdate(TemplateDetailArrayItemFilter filter) {
		TemplateDetailArrayItemFilter origin = get(filter.getId());
		if(origin != null) {
			Date now = new Date();
			//TODO: 修改过滤器的其他字段
			origin.setUpdateTime(now);
			
			NormalDaoSetUpdateStrategy.build(TemplateDetailArrayItemCriteria.class, getDao().getNormalOperateDao(), TemplateDetailArrayItemCriteria::getId, (originCriteria, criteria)->{
				originCriteria.setTitle(criteria.getTitle());
				originCriteria.setOrder(criteria.getOrder());
				originCriteria.setUpdateTime(now);
				if(criteria.getFieldAvailable()) {
					originCriteria.setFieldId(criteria.getFieldId());
					originCriteria.setFieldKey(criteria.getFieldKey());
					originCriteria.setRelation(criteria.getRelation());
					originCriteria.setQueryShow(criteria.getQueryShow());
					originCriteria.setComparator(criteria.getComparator());
					originCriteria.setInputType(criteria.getInputType());
					originCriteria.setRelationLabel(criteria.getRelationLabel());
					originCriteria.setViewOption(criteria.getViewOption());
					originCriteria.setDefaultValue(criteria.getDefaultValue());
					originCriteria.setPlaceholder(criteria.getPlaceholder());
				}
			}, criteria->{
				criteria.setCreateTime(now);
				criteria.setUpdateTime(now);
				criteria.setTemplateId(origin.getId());
			}).doUpdate(new LinkedHashSet<>(origin.getCriterias()), new LinkedHashSet<>(filter.getCriterias()));
		}else {
			throw new RuntimeException("ArrayItemFilter不存在[id=" + filter.getId() + "]");
		}
	}

}
