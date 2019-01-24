package cn.sowell.dataserver.model.tmpl.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.dao.deferedQuery.HibernateRefrectResultTransformer;
import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.tmpl.dao.ActionTemplateDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionArrayEntity;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionArrayEntityField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionTemplate;

@Repository
public class ActionTemplateDaoImpl extends AbstractDetailTemplateDao<TemplateActionTemplate, TemplateActionFieldGroup, TemplateActionField> implements ActionTemplateDao{

	@Autowired
	public ActionTemplateDaoImpl(@Autowired NormalOperateDao nDao, @Autowired SessionFactory sFactory) {
		super(nDao, sFactory);
	}

	@Override
	public Class<TemplateActionTemplate> getTemplateClass() {
		return TemplateActionTemplate.class;
	}

	@Override
	public Class<TemplateActionFieldGroup> getFieldGroupClass() {
		return TemplateActionFieldGroup.class;
	}

	@Override
	public Class<TemplateActionField> getFieldClass() {
		return TemplateActionField.class;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateActionArrayEntity> queryArrayEntities() {
		String hql = "from TemplateActionArrayEntity e order by e.index asc";
		return getSessionFactory().getCurrentSession().createQuery(hql).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateActionArrayEntity> queryArrayEntities(Long atmplId) {
		String sql = "select e.* from t_sa_tmpl_action_arrayentity e "
				+ "left join t_sa_tmpl_action_fieldgroup g on g.id = e.tmpl_field_group_id "
				+ "where g.tmpl_id = :atmplId";
		SQLQuery query = getSessionFactory().getCurrentSession().createSQLQuery(sql);
		query.setLong("atmplId", atmplId);
		query.setResultTransformer(HibernateRefrectResultTransformer.getInstance(TemplateActionArrayEntity.class));
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateActionArrayEntityField> queryArrayEntityFields() {
		String hql = "from TemplateActionArrayEntityField f";
		return getSessionFactory().getCurrentSession().createQuery(hql).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, List<TemplateActionArrayEntityField>> queryArrayEntityFields(Set<Long> entityIds) {
		if(entityIds != null && !entityIds.isEmpty()) {
			String hql = "from TemplateActionArrayEntityField f where f.actionArrayEntityId in (:entityIds)";
			Query query = getSessionFactory().getCurrentSession().createQuery(hql);
			query.setParameterList("entityIds", entityIds);
			List<TemplateActionArrayEntityField> list = query.list();
			return CollectionUtils.toListMap(list, TemplateActionArrayEntityField::getActionArrayEntityId);
		}else {
			return new HashMap<>();
		}
	}

	
	

}
