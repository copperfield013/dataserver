package cn.sowell.dataserver.model.tmpl.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.cachable.dao.impl.AbsctractCachableDao;
import cn.sowell.dataserver.model.tmpl.dao.TreeTemplateDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeNode;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeNodeCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeRelation;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeRelationCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateTreeTemplate;

@Repository
public class TreeTemplateDaoImpl
	extends AbsctractCachableDao<TemplateTreeTemplate>
	implements TreeTemplateDao{

	@Autowired
	protected TreeTemplateDaoImpl(@Autowired NormalOperateDao nDao, @Autowired SessionFactory sFactory) {
		super(nDao, sFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateTreeTemplate> queryAll() {
		Criteria criteria = getSessionFactory().getCurrentSession()
					.createCriteria(TemplateTreeTemplate.class);
		criteria.addOrder(Order.desc("updateTime"));
		return criteria.list();
	}

	@Override
	public TemplateTreeTemplate get(long cachableId) {
		return getSessionFactory().getCurrentSession().get(TemplateTreeTemplate.class, cachableId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateTreeNode> queryAllNodes() {
		return getSessionFactory().getCurrentSession()
				.createCriteria(TemplateTreeNode.class)
				.addOrder(Order.asc("order"))
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateTreeNodeCriteria> queryAllNodeCriterias() {
		return getSessionFactory().getCurrentSession()
				.createCriteria(TemplateTreeNodeCriteria.class)
				.addOrder(Order.asc("order"))
				.list();
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateTreeRelation> queryAllRelation() {
		return getSessionFactory().getCurrentSession()
				.createCriteria(TemplateTreeRelation.class)
				.addOrder(Order.asc("order"))
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateTreeRelationCriteria> queryAllCriterias() {
		return getSessionFactory().getCurrentSession()
					.createCriteria(TemplateTreeRelationCriteria.class)
					.addOrder(Order.asc("order"))
					.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateTreeNode> queryNodeList(Long ttmplId) {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(TemplateTreeNode.class);
		criteria.add(Restrictions.eq("treeTemplateId", ttmplId));
		criteria.addOrder(Order.asc("order"));
		return criteria.list();
	}

	@Override
	public Map<Long, List<TemplateTreeNodeCriteria>> queryNodeCriterias(Set<Long> nodeIds) {
		if(nodeIds != null && !nodeIds.isEmpty()) {
			Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(TemplateTreeNodeCriteria.class);
			criteria.add(Restrictions.in("templateId", nodeIds));
			criteria.addOrder(Order.asc("order"));
			@SuppressWarnings("unchecked")
			List<TemplateTreeNodeCriteria> list = criteria.list();
			return CollectionUtils.toListMap(list, TemplateTreeNodeCriteria::getTemplateId);
		}
		return new HashMap<>();
	}

	
	@Override
	public Map<Long, List<TemplateTreeRelation>> queryRelationsMapByNodes(Set<Long> nodeIds) {
		if(nodeIds != null && !nodeIds.isEmpty()) {
			Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(TemplateTreeRelation.class);
			criteria.add(Restrictions.in("nodeId", nodeIds));
			criteria.addOrder(Order.asc("order"));
			@SuppressWarnings("unchecked")
			List<TemplateTreeRelation> list = criteria.list();
			return CollectionUtils.toListMap(list, TemplateTreeRelation::getNodeId);
		}
		return new HashMap<>();
	}

	@Override
	public Map<Long, List<TemplateTreeRelationCriteria>> queryCriteriasMapByRelations(Set<Long> nodeRelationIds) {
		if(nodeRelationIds != null && !nodeRelationIds.isEmpty()) {
			Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(TemplateTreeRelationCriteria.class);
			criteria.add(Restrictions.in("templateId", nodeRelationIds));
			criteria.addOrder(Order.asc("order"));
			@SuppressWarnings("unchecked")
			List<TemplateTreeRelationCriteria> list = criteria.list();
			return CollectionUtils.toListMap(list, TemplateTreeRelationCriteria::getTemplateId);
		}
		return new HashMap<>();
	}

	

}
