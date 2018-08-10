package cn.sowell.dataserver.model.tmpl.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.dao.deferedQuery.DeferedParamSnippet;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.tmpl.dao.TemplateGroupDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupPremise;
import cn.sowell.dataserver.model.tmpl.utils.QueryUtils;

@Repository
public class TemplateGroupDaoImpl implements TemplateGroupDao{

	@Resource
	SessionFactory sFactory;
	
	private List<TemplateGroup> queryGroups(GroupQueryCriteria criteria) {
		return QueryUtils.queryList(
				"	SELECT g.*, l.c_title list_tmpl_title, d.c_title detail_tmpl_title" +
				"	FROM t_sa_tmpl_group g" +
				"		LEFT JOIN t_sa_tmpl_list_template l ON g.list_tmpl_id = l.id" +
				"		LEFT JOIN t_sa_tmpl_detail_template d ON g.detail_tmpl_id = d.id " +
				"	WHERE" +
				"		g.id is not null @moduleSnippet @keySnippet @groupIdsSnippet @modulesSnippet",
				TemplateGroup.class, sFactory.getCurrentSession(), dQuery->{
			DeferedParamSnippet moduleSnippet = dQuery.createSnippet("moduleSnippet", null),
					groupIdsSnippet = dQuery.createSnippet("groupIdsSnippet", null),
					keySnippet = dQuery.createSnippet("keySnippet", null),
					modulesSnippet = dQuery.createSnippet("modulesSnippet", null);
			if(criteria.getModule() != null) {
				moduleSnippet.append("and g.c_module = :module");
				dQuery.setParam("module", criteria.getModule());
			}
			if(criteria.getGroupIds() != null && !criteria.getGroupIds().isEmpty()) {
				groupIdsSnippet.append("and g.id in (:groupIds)");
				dQuery.setParam("groupIds", criteria.getGroupIds());
			}
			if(criteria.getModules() != null && !criteria.getModules().isEmpty()) {
				modulesSnippet.append("and g.c_module in (:moduleNames)");
				dQuery.setParam("moduleNames", criteria.getModules(), StandardBasicTypes.STRING);
			}
			if(TextUtils.hasText(criteria.getGroupKey())) {
				keySnippet.append("and g.c_key = :groupKey");
				dQuery.setParam("groupKey", criteria.getGroupKey());
			}
		});
	}

	@Override
	public List<TemplateGroup> queryGroups(String module) {
		GroupQueryCriteria criteria = new GroupQueryCriteria();
		criteria.setModule(module);
		return queryGroups(criteria);
	}
	

	@Override
	public TemplateGroup getGroup(Long groupId) {
		GroupQueryCriteria criteria = new GroupQueryCriteria();
		criteria.setGroupIds(new HashSet<Long>(Arrays.asList(groupId)));
		List<TemplateGroup> list = queryGroups(criteria);
		if(list != null) {
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public TemplateGroup getTemplateGroup(String module, String templateGroupKey) {
		GroupQueryCriteria criteria = new GroupQueryCriteria();
		criteria.setGroupKey(templateGroupKey);
		List<TemplateGroup> list = queryGroups(criteria);
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	@Override
	public List<TemplateGroup> getTemplateGroups(Set<String> moduleNames) {
		if(moduleNames != null && !moduleNames.isEmpty()) {
			GroupQueryCriteria criteria = new GroupQueryCriteria();
			criteria.setModules(moduleNames);
			return queryGroups(criteria);
		}else {
			return new ArrayList<TemplateGroup>();
		}
	}
	
	static class GroupQueryCriteria{
		private Set<String> modules;
		private String module;
		private String groupKey;
		private Set<Long> groupIds;
		public String getModule() {
			return module;
		}
		public void setModule(String module) {
			this.module = module;
		}
		public String getGroupKey() {
			return groupKey;
		}
		public void setGroupKey(String groupKey) {
			this.groupKey = groupKey;
		}
		public Set<Long> getGroupIds() {
			return groupIds;
		}
		public void setGroupIds(Set<Long> groupIds) {
			this.groupIds = groupIds;
		}
		public Set<String> getModules() {
			return modules;
		}
		public void setModules(Set<String> modules) {
			this.modules = modules;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateGroup> queryGroups() {
		String hql = "from TemplateGroup g order by g.updateTime desc ";
		Query query =  sFactory.getCurrentSession().createQuery(hql);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateGroupPremise> queryPremises() {
		String hql = "from TemplateGroupPremise p order by p.order asc";
		return sFactory.getCurrentSession().createQuery(hql).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateGroupPremise> queryPremises(Long groupId) {
		String hql = "from TemplateGroupPremise p where p.groupId = :groupId order by p.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		query.setLong("groupId", groupId);
		return query.list();
	}
	
	@Override
	public void updateAllGroupsDetailTemplate(Long dtmplId, Long targetDtmplId) {
		String sql = "update t_sa_tmpl_group set detail_tmpl_id = :targetDtmplId where detail_tmpl_id = :dtmplId";
		SQLQuery query = sFactory.getCurrentSession().createSQLQuery(sql);
		query.setLong("dtmplId", dtmplId);
		query.setLong("targetDtmplId", targetDtmplId);
		query.executeUpdate();
	}

	@Override
	public void updateAllGroupsListTemplate(Long ltmplId, Long targetLtmplId) {
		String sql = "update t_sa_tmpl_group set list_tmpl_id = :targetLtmplId where list_tmpl_id = :ltmplId";
		SQLQuery query = sFactory.getCurrentSession().createSQLQuery(sql);
		query.setLong("ltmplId", ltmplId);
		query.setLong("targetLtmplId", targetLtmplId);
		query.executeUpdate();
	}
	
}
