package cn.sowell.dataserver.model.tmpl.dao.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.cachable.dao.impl.AbsctractCachableDao;
import cn.sowell.dataserver.model.tmpl.dao.OpenListTemplateDao;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListColumn;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListTemplate;

public abstract class AbstractListTemplateDao<LT extends AbstractListTemplate<COL, CRI>, COL extends AbstractListColumn, CRI extends AbstractListCriteria>
	extends AbsctractCachableDao<LT> 
	implements OpenListTemplateDao<LT, COL, CRI>{

	protected AbstractListTemplateDao(NormalOperateDao nDao, SessionFactory sFactory) {
		super(nDao, sFactory);
	}
	
	public abstract Class<LT> getListTemplateClass();

	public abstract  Class<COL> getListColumnClass();
	
	public abstract  Class<CRI> getListCriteriaClass();
	
	@SuppressWarnings("unchecked")
	@Override
	public List<LT> queryAll() {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(getListTemplateClass());
		criteria.addOrder(Order.desc("updateTime"));
		return criteria.list();
	}


	@Override
	public LT get(long cachableId) {
		return getNormalOperateDao().get(getListTemplateClass(), cachableId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<COL> getColumnsByTmplId(Long ltmplId) {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(getListColumnClass());
		criteria.add(Restrictions.eq("templateId", ltmplId));
		criteria.addOrder(Order.asc("order"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CRI> getCriteriaByTmplId(Long ltmplId) {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(getListCriteriaClass());
		criteria.add(Restrictions.eq("templateId", ltmplId));
		criteria.addOrder(Order.asc("order"));
		return criteria.list();
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, List<COL>> queryColumnsMap() {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(getListColumnClass());
		criteria.addOrder(Order.asc("order"));
		List<COL> list = criteria.list();
		return CollectionUtils.toListMap(list, AbstractListColumn::getTemplateId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, List<CRI>> queryCriteriasMap() {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(getListCriteriaClass());
		criteria.addOrder(Order.asc("order"));
		List<CRI> list = criteria.list();
		return CollectionUtils.toListMap(list, AbstractListCriteria::getTemplateId);
	}
}
