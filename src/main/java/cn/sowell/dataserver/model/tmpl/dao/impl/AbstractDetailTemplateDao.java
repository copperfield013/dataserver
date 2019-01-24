package cn.sowell.dataserver.model.tmpl.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.cachable.dao.impl.AbsctractCachableDao;
import cn.sowell.dataserver.model.tmpl.dao.OpenDetailTemplateDao;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractDetailTemplate;

public abstract class AbstractDetailTemplateDao<DT extends AbstractDetailTemplate<GT,FT>, GT extends AbstractDetailFieldGroup<FT>, FT extends AbstractDetailField>
	extends AbsctractCachableDao<DT>
	implements OpenDetailTemplateDao<DT, GT, FT>{

	protected AbstractDetailTemplateDao(NormalOperateDao nDao, SessionFactory sFactory) {
		super(nDao, sFactory);
	}

	public abstract Class<DT> getTemplateClass();
	public abstract Class<GT> getFieldGroupClass();
	public abstract Class<FT> getFieldClass();
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DT> queryAll() {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(getTemplateClass());
		criteria.addOrder(Order.desc("updateTime"));
		return criteria.list();
	}

	@Override
	public DT get(long cachableId) {
		return getNormalOperateDao().get(getTemplateClass(), cachableId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GT> queryFieldGroups() {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(getFieldGroupClass());
		criteria.addOrder(Order.asc("order"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FT> queryTemplateFields() {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(getFieldClass());
		criteria.addOrder(Order.asc("order"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GT> getTemplateGroups(Long tmplId) {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(getFieldGroupClass());
		criteria.add(Restrictions.eq("tmplId", tmplId));
		criteria.addOrder(Order.asc("order"));
		return criteria.list();
	}

	@Override
	public Map<Long, List<FT>> getTemplateFieldsMap(Set<Long> groupIds) {
		if(groupIds != null && !groupIds.isEmpty()){
			Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(getFieldClass());
			criteria.add(Restrictions.in("groupId", groupIds));
			criteria.addOrder(Order.asc("order"));
			@SuppressWarnings("unchecked")
			List<FT> fieldList = criteria.list();
			return CollectionUtils.toListMap(fieldList, field->field.getGroupId());
		}else{
			return new HashMap<Long, List<FT>>();
		}
	}


}
