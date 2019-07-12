package cn.sowell.dataserver.model.karuiserv.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.dataserver.model.cachable.dao.impl.AbsctractCachableDao;
import cn.sowell.dataserver.model.karuiserv.dao.KaruiServJsonMetaDao;
import cn.sowell.dataserver.model.karuiserv.pojo.KaruiServJsonMeta;

@Repository
public class KaruiServJsonMetaDaoImpl extends AbsctractCachableDao<KaruiServJsonMeta> implements KaruiServJsonMetaDao {

	@Autowired
	public KaruiServJsonMetaDaoImpl(NormalOperateDao nDao, SessionFactory sFactory) {
		super(nDao, sFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<KaruiServJsonMeta> queryAll() {
		return getSessionFactory().getCurrentSession().createCriteria(KaruiServJsonMeta.class).list();
	}

	@Override
	public KaruiServJsonMeta get(long cachableId) {
		return (KaruiServJsonMeta) getSessionFactory().getCurrentSession().createCriteria(KaruiServJsonMeta.class).add(Restrictions.idEq(cachableId)).uniqueResult();
	}

}
