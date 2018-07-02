package cn.sowell.dataserver.model.tmpl.dao.impl;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.dto.page.PageInfo;
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
	
	

}
