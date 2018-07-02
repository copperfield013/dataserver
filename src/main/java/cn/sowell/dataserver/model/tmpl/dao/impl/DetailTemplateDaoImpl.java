package cn.sowell.dataserver.model.tmpl.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dao.deferedQuery.HibernateRefrectResultTransformer;
import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.tmpl.dao.DetailTemplateDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.utils.QueryUtils;

@Repository
public class DetailTemplateDaoImpl implements DetailTemplateDao{

	@Resource
	SessionFactory sFactory;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateDetailFieldGroup> getTemplateGroups(Long dtmplId) {
		String hql = "from TemplateDetailFieldGroup g where g.tmplId = :tmplId order by g.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		query.setLong("tmplId", dtmplId);
		return query.list();
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateDetailTemplate> getTemplateList(String module, UserIdentifier user, PageInfo pageInfo) {
		String hql = "from TemplateDetailTemplate t where t.module = :module";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		query.setString("module", module);
		QueryUtils.setPagingParamWithCriteria(query, pageInfo);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, List<TemplateDetailField>> getTemplateFieldsMap(
			Set<Long> groupIdSet) {
		if(groupIdSet != null && !groupIdSet.isEmpty()){
			String sql = "select f.*, df.c_type, df.optgroup_id from t_tmpl_detail_field f "
					+ "left join v_dictionary_field df on f.field_id = df.id where f.group_id in (:groupIds) order by f.c_order asc ";
			SQLQuery query = sFactory.getCurrentSession().createSQLQuery(sql);
			
			query.setParameterList("groupIds", groupIdSet);
			query.setResultTransformer(HibernateRefrectResultTransformer.getInstance(TemplateDetailField.class));
			List<TemplateDetailField> fieldList = query.list();
			return CollectionUtils.toListMap(fieldList, field->field.getGroupId());
		}else{
			return new HashMap<Long, List<TemplateDetailField>>();
		}
	}
	
	@Override
	public TemplateDetailTemplate getDetailTemplateByGroupId(Long templateGroupId) {
		String sql = "select d.* from t_tmpl_detail_template d left join t_tmpl_group g on d.id = g.detail_tmpl_id where g.id = :groupId";
		SQLQuery query = sFactory.getCurrentSession().createSQLQuery(sql);
		query.setLong("groupId", templateGroupId);
		query.setResultTransformer(HibernateRefrectResultTransformer.getInstance(TemplateDetailTemplate.class));
		return (TemplateDetailTemplate) query.uniqueResult();
	}

}
