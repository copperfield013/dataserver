package cn.sowell.dataserver.model.tmpl.dao.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.dao.deferedQuery.HibernateRefrectResultTransformer;
import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.dataserver.model.tmpl.dao.DetailTemplateDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroupTreeNode;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;

@Repository
public class DetailTemplateDaoImpl extends AbstractDetailTemplateDao<TemplateDetailTemplate, TemplateDetailFieldGroup, TemplateDetailField> 
	implements DetailTemplateDao{

	@Autowired
	public DetailTemplateDaoImpl(@Autowired NormalOperateDao nDao, @Autowired SessionFactory sFactory) {
		super(nDao, sFactory);
	}
	
	@Override
	public Class<TemplateDetailTemplate> getTemplateClass() {
		return TemplateDetailTemplate.class;
	}

	@Override
	public Class<TemplateDetailFieldGroup> getFieldGroupClass() {
		return TemplateDetailFieldGroup.class;
	}

	@Override
	public Class<TemplateDetailField> getFieldClass() {
		return TemplateDetailField.class;
	}
	
	@Override
	public TemplateDetailTemplate getDetailTemplateByGroupId(Long templateGroupId) {
		String sql = "select d.* from t_sa_tmpl_detail_template d left join t_sa_tmpl_group g on d.id = g.detail_tmpl_id where g.id = :groupId";
		SQLQuery query = getSessionFactory().getCurrentSession().createSQLQuery(sql);
		query.setLong("groupId", templateGroupId);
		query.setResultTransformer(HibernateRefrectResultTransformer.getInstance(TemplateDetailTemplate.class));
		return (TemplateDetailTemplate) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateDetailFieldGroupTreeNode> queryAllFieldGroupTreeNodes() {
		return getSessionFactory().getCurrentSession()
				.createCriteria(TemplateDetailFieldGroupTreeNode.class).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateDetailFieldGroupTreeNode> queryFieldGroupTreeNodes(Long dtmplId) {
		String sql = "select n.* from t_sa_tmpl_detail_fieldgroup_treenode n "
				+ " left join t_sa_tmpl_detail_fieldgroup g on n.fieldgroup_id = g.id"
				+ " where g.tmpl_id = :dtmplId";
		SQLQuery query = getSessionFactory().getCurrentSession().createSQLQuery(sql);
		query.setLong("dtmplId", dtmplId);
		query.setResultTransformer(HibernateRefrectResultTransformer.getInstance(TemplateDetailFieldGroupTreeNode.class));
		return query.list();
	}
}
