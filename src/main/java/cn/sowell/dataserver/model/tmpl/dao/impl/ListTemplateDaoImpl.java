package cn.sowell.dataserver.model.tmpl.dao.impl;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.tmpl.dao.ListTemplateDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;
import cn.sowell.dataserver.model.tmpl.utils.QueryUtils;

@Repository
public class ListTemplateDaoImpl implements ListTemplateDao{

	@Resource
	SessionFactory sFactory;
	
	@Override
	public List<TemplateListTemplate> queryLtmplList(String module, Serializable userId,
			PageInfo pageInfo) {
		return QueryUtils.pagingQuery("from TemplateListTemplate t where t.module = :module order by t.updateTime desc", sFactory.getCurrentSession(), pageInfo, dQuery->{
			dQuery.setParam("module", module);
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateListColumn> getColumnsByTmplId(Long ltmplId) {
		String hql = "from TemplateListColumn c where c.templateId = :ltmplId order by c.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		query.setLong("ltmplId", ltmplId);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<TemplateListCriteria> getCriteriaByTmplId(Long ltmplId) {
		String hql = "from TemplateListCriteria c where c.templateId = :ltmplId order by c.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		query.setLong("ltmplId", ltmplId);
		return new LinkedHashSet<TemplateListCriteria>(query.list());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateListTemplate> queryTemplates() {
		String hql = "from TemplateListTemplate l order by l.updateTime desc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		return query.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<Long, List<TemplateListColumn>> queryColumnsMap() {
		String hql = "from TemplateListColumn c order by c.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		List<TemplateListColumn> list = query.list();
		return CollectionUtils.toListMap(list, column->column.getTemplateId());
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, Set<TemplateListCriteria>> queryCriteriasMap() {
		String hql = "from TemplateListCriteria c order by c.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		List<TemplateListCriteria> list = query.list();
		return CollectionUtils.toCollectionMap(list, criteria->criteria.getTemplateId(), ()->new LinkedHashSet<>());
	}
	

}
