package cn.sowell.dataserver.model.cachable.dao.impl;

import org.hibernate.SessionFactory;
import org.springframework.util.Assert;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.dataserver.model.cachable.dao.CachableDao;

public abstract class AbsctractCachableDao<T> implements CachableDao<T>{

	private final NormalOperateDao nDao;
	
	private final SessionFactory sFactory;
	
	protected AbsctractCachableDao(NormalOperateDao nDao, SessionFactory sFactory) {
		Assert.notNull(nDao);
		Assert.notNull(sFactory);
		this.nDao = nDao;
		this.sFactory = sFactory;
	}
	
	protected SessionFactory getSessionFactory() {
		return sFactory;
	}
	
	public NormalOperateDao getNormalOperateDao() {
		return nDao;
	};

}
