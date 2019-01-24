package cn.sowell.dataserver.model.tmpl.strategy;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import javax.annotation.Resource;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;
import cn.sowell.dataserver.model.tmpl.service.TemplateService1;

public class TemplateSelectionUpdateStrategy implements TemplateUpdateStrategy<TemplateSelectionTemplate> {

	@Resource
	TemplateService1 tService;
	
	@Resource
	NormalOperateDao nDao;
	
	@Override
	public void update(TemplateSelectionTemplate template) {
		TemplateSelectionTemplate origin = tService.getSelectionTemplate(template.getId());
		if(origin != null){
			origin.setTitle(template.getTitle());
			origin.setDefaultPageSize(template.getDefaultPageSize());
			origin.setDefaultOrderFieldId(template.getDefaultOrderFieldId());
			origin.setDefaultOrderDirection(template.getDefaultOrderDirection());
			origin.setMultiple(template.getMultiple());
			origin.setNonunique(template.getNonunique());
			nDao.update(origin);
			Date now = new Date();
			
			NormalDaoSetUpdateStrategy.build(
					TemplateSelectionColumn.class, nDao,
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
					},column->{
						column.setCreateTime(now);
						column.setUpdateTime(now);
						column.setTemplateId(origin.getId());
					})
				.doUpdate(new HashSet<>(origin.getColumns()), new HashSet<>(template.getColumns()));
			
			NormalDaoSetUpdateStrategy.build(
				TemplateSelectionCriteria.class, nDao, 
				criteria->criteria.getId(), 
				(originCriteria, criteria)->{
					if(!criteria.getFieldAvailable()) {
						originCriteria.setTitle(criteria.getTitle());
						originCriteria.setOrder(criteria.getOrder());
						originCriteria.setUpdateTime(now);
						return;
					}
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
					
				}, criteria->{
					criteria.setCreateTime(now);
					criteria.setUpdateTime(now);
					criteria.setTemplateId(origin.getId());
				})
			.doUpdate(new LinkedHashSet<>(origin.getCriterias()), new LinkedHashSet<>(template.getCriterias()));
		}else{
			throw new RuntimeException("列表模板[id=" + template.getId() + "]不存在");
		}
	}

	@Override
	public Long create(TemplateSelectionTemplate template) {
		if(template.getId() == null){
			Date now = new Date();
			//创建
			template.setCreateTime(now );
			template.setUpdateTime(now);
			Long tmplId = nDao.save(template);
			List<TemplateSelectionColumn> columns = template.getColumns();
			for (TemplateSelectionColumn column : columns) {
				column.setTemplateId(tmplId);
				column.setCreateTime(now);
				column.setUpdateTime(now);
				nDao.save(column);
			}
			List<TemplateSelectionCriteria> criterias = template.getCriterias();
			for (TemplateSelectionCriteria criteria : criterias) {
				criteria.setTemplateId(tmplId);
				criteria.setCreateTime(now);
				criteria.setUpdateTime(now);
				nDao.save(criteria);
			}
			return tmplId;
		}
		return null;
	}

}
