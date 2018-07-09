package cn.sowell.dataserver.model.tmpl.strategy;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;
import cn.sowell.dataserver.model.tmpl.service.TemplateService;

public class TemplateListUpdateStrategy implements TemplateUpdateStrategy<TemplateListTemplate> {

	@Resource
	TemplateService tService;
	
	@Resource
	NormalOperateDao nDao;
	
	@Override
	public void update(TemplateListTemplate template) {
		TemplateListTemplate origin = tService.getListTemplate(template.getId());
		if(origin != null){
			origin.setTitle(template.getTitle());
			origin.setUnmodifiable(template.getUnmodifiable());
			origin.setDefaultPageSize(template.getDefaultPageSize());
			origin.setDefaultOrderFieldId(template.getDefaultOrderFieldId());
			origin.setDefaultOrderDirection(template.getDefaultOrderDirection());
			
			Date now = new Date();
			
			NormalDaoSetUpdateStrategy.build(
					TemplateListColumn.class, nDao,
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
				TemplateListCriteria.class, nDao, 
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
			.doUpdate(origin.getCriterias(), template.getCriterias());
		}else{
			throw new RuntimeException("列表模板[id=" + template.getId() + "]不存在");
		}
	}

	@Override
	public Long create(TemplateListTemplate template) {
		if(template.getId() == null){
			Date now = new Date();
			//创建
			template.setCreateTime(now );
			template.setUpdateTime(now);
			Long tmplId = nDao.save(template);
			List<TemplateListColumn> columns = template.getColumns();
			for (TemplateListColumn column : columns) {
				column.setTemplateId(tmplId);
				column.setCreateTime(now);
				column.setUpdateTime(now);
				nDao.save(column);
			}
			Set<TemplateListCriteria> criterias = template.getCriterias();
			for (TemplateListCriteria criteria : criterias) {
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
