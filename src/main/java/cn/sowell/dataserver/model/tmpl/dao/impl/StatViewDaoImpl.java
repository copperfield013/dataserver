package cn.sowell.dataserver.model.tmpl.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.dataserver.model.cachable.dao.impl.AbsctractCachableDao;
import cn.sowell.dataserver.model.tmpl.dao.StatViewDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatView;

@Repository
public class StatViewDaoImpl extends AbsctractCachableDao<TemplateStatView> implements StatViewDao{

	@Autowired
	public StatViewDaoImpl(@Autowired NormalOperateDao nDao, @Autowired SessionFactory sFactory) {
		super(nDao, sFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateStatView> queryAll() {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(TemplateStatView.class);
		return criteria.list();
	}

	@Override
	public TemplateStatView get(long cachableId) {
		return getSessionFactory().getCurrentSession().get(TemplateStatView.class, cachableId);
	}


}
