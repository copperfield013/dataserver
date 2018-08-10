package cn.sowell.dataserver.model.tmpl.dao.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.tmpl.dao.SelectionTemplateDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;

@Repository
public class SelectionTemplateDaoImpl implements SelectionTemplateDao{

	@Resource
	SessionFactory sFactory;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateSelectionTemplate> queryTemplates() {
		String hql = "from TemplateSelectionTemplate s order by s.updateTime desc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, List<TemplateSelectionColumn>> queryColumnsMap() {
		String hql = "from TemplateSelectionColumn c order by c.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		List<TemplateSelectionColumn> list = query.list();
		return CollectionUtils.toListMap(list, column->column.getTemplateId());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, Set<TemplateSelectionCriteria>> queryCriteriasMap() {
		String hql = "from TemplateSelectionCriteria c order by c.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		List<TemplateSelectionCriteria> list = query.list();
		return CollectionUtils.toCollectionMap(list, criteria->criteria.getTemplateId(), ()->new LinkedHashSet<>());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateSelectionColumn> getColumnsByTmplId(Long stmplId) {
		String hql = "from TemplateSelectionColumn c where c.templateId = :stmplId order by c.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		query.setLong("stmplId", stmplId);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<TemplateSelectionCriteria> getCriteriaByTmplId(Long stmplId) {
		String hql = "from TemplateSelectionCriteria c where c.templateId = :stmplId order by c.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		query.setLong("stmplId", stmplId);
		return new LinkedHashSet<TemplateSelectionCriteria>(query.list());
	}
	

}
