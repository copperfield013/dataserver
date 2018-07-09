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
			String hql = "from TemplateDetailField f where f.groupId in (:groupIds) order by f.order asc";
			Query query = sFactory.getCurrentSession().createQuery(hql);
			
			query.setParameterList("groupIds", groupIdSet);
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
	
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateDetailTemplate> queryTemplates() {
		String hql = "from TemplateDetailTemplate t order by t.updateTime desc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateDetailFieldGroup> queryFieldGroups() {
		String hql = "from TemplateDetailFieldGroup g order by g.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TemplateDetailField> queryTemplateFields() {
		String hql = "from TemplateDetailField f order by f.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		return query.list();
	}
	
	
	/*@SuppressWarnings("unchecked")
	@Override
	public Map<Long, List<TemplateDetailFieldValidator>> getTemplateFieldValidators(Long dtmplId) {
		String sql = "SELECT v.*  FROM" +
				"	t_tmpl_detail_field_validator v" +
				"	LEFT JOIN t_tmpl_detail_field f ON v.dtmplfield_id = f.id" +
				"	LEFT JOIN t_tmpl_detail_fieldgroup g ON f.group_id = g.id " +
				" WHERE g.tmpl_id = :dtmplId";
		
		SQLQuery query = sFactory.getCurrentSession().createSQLQuery(sql);
		query.setLong("dtmplId", dtmplId);
		query.setResultTransformer(HibernateRefrectResultTransformer.getInstance(TemplateDetailFieldValidator.class));
		List<TemplateDetailFieldValidator> list = query.list();
		return CollectionUtils.toListMap(list, v->v.getDetailTemplateFieldId());
	}
	
	@SuppressWarnings({ "serial", "unchecked" })
	@Override
	public Map<Long, Map<Long, List<TemplateDetailFieldValidator>>> queryAllFieldValidatorsMap() {
		String sql = "SELECT v.*, g.tmpl_id dtmpl_id  FROM" +
				"	t_tmpl_detail_field_validator v" +
				"	LEFT JOIN t_tmpl_detail_field f ON v.dtmplfield_id = f.id" +
				"	LEFT JOIN t_tmpl_detail_fieldgroup g ON f.group_id = g.id ";
		SQLQuery query = sFactory.getCurrentSession().createSQLQuery(sql);
		Map<Long, Long> dtmplIdMap = new HashMap<>(); 
		query.setResultTransformer(new ColumnMapResultTransformer<TemplateDetailFieldValidator>() {

			@Override
			protected TemplateDetailFieldValidator build(SimpleMapWrapper mapWrapper) {
				TemplateDetailFieldValidator validator = HibernateRefrectResultTransformer.getInstance(TemplateDetailFieldValidator.class)
							.build(mapWrapper);
				dtmplIdMap.put(validator.getDetailTemplateFieldId(), mapWrapper.getLong("dtmpl_id"));
				return validator;
			}
		});
		
		List<TemplateDetailFieldValidator> list = query.list();
		Map<Long, List<TemplateDetailFieldValidator>> fieldValidatorsMap = CollectionUtils.toListMap(list, v->v.getDetailTemplateFieldId());
		
		Map<Long, Map<Long, List<TemplateDetailFieldValidator>>> map = new HashMap<>();
		dtmplIdMap.forEach((tmplFieldId, dtmplId)->{
			Map<Long, List<TemplateDetailFieldValidator>> tmplFieldValidatorMap = map.get(dtmplId);
			if(tmplFieldValidatorMap == null) {
				tmplFieldValidatorMap = new HashMap<Long, List<TemplateDetailFieldValidator>>();
				map.put(dtmplId, tmplFieldValidatorMap);
			}
			tmplFieldValidatorMap.put(tmplFieldId, fieldValidatorsMap.get(tmplFieldId));
		});
		return map;
	}*/

}
