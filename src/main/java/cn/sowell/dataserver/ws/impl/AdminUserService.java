package cn.sowell.dataserver.ws.impl;

import javax.annotation.Resource;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.dataserver.model.tmpl.service.AdminIdGetter;

public class AdminUserService implements AdminIdGetter{

	@Resource
	SessionFactory sFactroy;
	
	
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public Long getSystemAdminIdByUserId(Long userId) {
		String sql = "select a.id from t_sys_admin a where a.user_id = :userId";
		SQLQuery query = sFactroy.getCurrentSession().createSQLQuery(sql);
		query.setLong("userId", userId);
		return FormatUtils.toLong(query.uniqueResult());
	}

}
