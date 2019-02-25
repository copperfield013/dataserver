package cn.sowell.dataserver.model.tmpl.dao.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.cachable.dao.impl.AbsctractCachableDao;
import cn.sowell.dataserver.model.tmpl.dao.ArrayItemFilterDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailArrayItemCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailArrayItemFilter;

@Repository
public class ArrayItemFilterDaoImpl extends AbsctractCachableDao<TemplateDetailArrayItemFilter> implements ArrayItemFilterDao{
	
	@Autowired
	public ArrayItemFilterDaoImpl(@Autowired NormalOperateDao nDao, @Autowired SessionFactory sFactory) {
		super(nDao, sFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateDetailArrayItemFilter> queryAll() {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(TemplateDetailArrayItemFilter.class);
		return criteria.list();
	}

	@Override
	public TemplateDetailArrayItemFilter get(long cachableId) {
		return getNormalOperateDao().get(TemplateDetailArrayItemFilter.class, cachableId);
	}

	@Override
	public Map<Long, List<TemplateDetailArrayItemCriteria>> queryAllCriterias() {
		Criteria query = getSessionFactory().getCurrentSession().createCriteria(TemplateDetailArrayItemCriteria.class);
		@SuppressWarnings("unchecked")
		List<TemplateDetailArrayItemCriteria> criterias = query.list();
		return CollectionUtils.toListMap(criterias, TemplateDetailArrayItemCriteria::getTemplateId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateDetailArrayItemCriteria> queryCriterias(Long filterId) {
		Criteria query = getSessionFactory().getCurrentSession().createCriteria(TemplateDetailArrayItemCriteria.class);
		query.add(Restrictions.eq("templateId", filterId));
		return query.list();
	}

}
