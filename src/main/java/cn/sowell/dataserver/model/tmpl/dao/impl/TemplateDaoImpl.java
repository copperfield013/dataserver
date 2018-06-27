package cn.sowell.dataserver.model.tmpl.dao.impl;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.copframe.utils.Assert;
import cn.sowell.dataserver.model.tmpl.dao.TemplateDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateAdminDefaultTemplate;

@Repository
public class TemplateDaoImpl implements TemplateDao{

	@Resource
	SessionFactory sFactory;
	
	@Resource
	NormalOperateDao nDao;
	
	@Override
	public TemplateAdminDefaultTemplate getAdminDefaultTempalte(long adminId, String module, String type) {
		String hql = "from TemplateAdminDefaultTemplate dt where dt.adminId = :adminId and dt.module = :module and dt.type = :type";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		query.setLong("adminId", adminId);
		query.setString("module", module);
		query.setString("type", type);
		return (TemplateAdminDefaultTemplate) query.uniqueResult();
	}

	@Override
	public void setTemplateAsDefault(Long adminId, String module, String type, long tmplId) {
		Assert.notNull(adminId);
		Assert.notNull(module);
		Assert.notNull(type);
		TemplateAdminDefaultTemplate t = getAdminDefaultTempalte(adminId, module, type);
		if(t != null) {
			if(!t.getTmplId().equals(tmplId)) {
				t.setTmplId(tmplId);
				nDao.update(t);
			}
		}else {
			t = new TemplateAdminDefaultTemplate();
			t.setAdminId(adminId);
			t.setModule(module);
			t.setType(type);
			t.setTmplId(tmplId);
			nDao.save(t);
		}
		
	}

}
