package cn.sowell.dataserver.model.tmpl.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.tmpl.dao.ActionTemplateDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionTemplate;

@Repository
public class ActionTemplateDaoImpl implements ActionTemplateDao{

	@Resource
	SessionFactory sFactory;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateActionTemplate> queryTemplates() {
		String hql = "from TemplateActionTemplate t order by t.updateTime desc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateActionFieldGroup> queryFieldGroups() {
		String hql = "from TemplateActionFieldGroup g order by g.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateActionField> queryTemplateFields() {
		String hql = "from TemplateActionField f order by f.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateActionFieldGroup> getTemplateGroups(Long atmplId) {
		String hql = "from TemplateActionFieldGroup g where g.tmplId = :tmplId order by g.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		query.setLong("tmplId", atmplId);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, List<TemplateActionField>> getTemplateFieldsMap(Set<Long> groupIdSet) {
		if(groupIdSet != null && !groupIdSet.isEmpty()){
			String hql = "from TemplateActionField f where f.groupId in (:groupIds) order by f.order asc";
			Query query = sFactory.getCurrentSession().createQuery(hql);
			
			query.setParameterList("groupIds", groupIdSet);
			List<TemplateActionField> fieldList = query.list();
			return CollectionUtils.toListMap(fieldList, field->field.getGroupId());
		}else{
			return new HashMap<Long, List<TemplateActionField>>();
		}
	}
	
	

}
