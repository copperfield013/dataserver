package cn.sowell.dataserver.model.tmpl.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.dataserver.model.cachable.dao.impl.AbsctractCachableDao;
import cn.sowell.dataserver.model.tmpl.dao.TemplateDictionaryFilterDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDictionaryFilter;

@Repository
public class TemplateDictionaryFilterDaoImpl 
			extends AbsctractCachableDao<TemplateDictionaryFilter> 
			implements TemplateDictionaryFilterDao {
	
	@Autowired
	public TemplateDictionaryFilterDaoImpl(@Autowired NormalOperateDao nDao, @Autowired SessionFactory sFactory) {
		super(nDao, sFactory);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateDictionaryFilter> queryAll() {
		String hql = "from TemplateDictionaryFilter f ";
		Query query = getSessionFactory().getCurrentSession().createQuery(hql);
		return query.list();
	}

	@Override
	public TemplateDictionaryFilter get(long cachableId) {
		return getSessionFactory().getCurrentSession().get(TemplateDictionaryFilter.class, cachableId);
	}
	
}
