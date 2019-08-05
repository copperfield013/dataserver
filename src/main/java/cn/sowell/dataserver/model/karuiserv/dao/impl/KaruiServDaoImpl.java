package cn.sowell.dataserver.model.karuiserv.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.dataserver.model.cachable.dao.impl.AbsctractCachableDao;
import cn.sowell.dataserver.model.karuiserv.dao.KaruiServDao;
import cn.sowell.dataserver.model.karuiserv.pojo.KaruiServ;
import cn.sowell.dataserver.model.karuiserv.pojo.KaruiServCriteria;

@Repository
public class KaruiServDaoImpl extends AbsctractCachableDao<KaruiServ> implements KaruiServDao{

	protected KaruiServDaoImpl(NormalOperateDao nDao, SessionFactory sFactory) {
		super(nDao, sFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<KaruiServ> queryAll() {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(KaruiServ.class);
		return criteria.list();
	}

	@Override
	public KaruiServ get(long cachableId) {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(KaruiServ.class);
		criteria.add(Restrictions.idEq(cachableId));
		return (KaruiServ) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<KaruiServCriteria> queryAllCriterias() {
		return getSessionFactory().getCurrentSession().createCriteria(KaruiServCriteria.class)
			.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<KaruiServCriteria> getCriteriasByKsId(Long ksId) {
		return getSessionFactory().getCurrentSession().createCriteria(KaruiServCriteria.class)
					.add(Restrictions.eq("karuiServId", ksId)).list();
	}

	@Override
	public void updateDisabled(Long ksId, boolean disabled) {
		Query query = getSessionFactory().getCurrentSession()
			.createQuery("update " + KaruiServ.class.getSimpleName() + " ks set ks.disabled = :disabled where ks.id = :id");
		query
			.setParameter("disabled", disabled?1:null, StandardBasicTypes.INTEGER)
			.setLong("id", ksId)
			.executeUpdate();
	}
}
