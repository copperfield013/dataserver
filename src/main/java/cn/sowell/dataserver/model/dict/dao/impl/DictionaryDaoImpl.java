package cn.sowell.dataserver.model.dict.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import com.beust.jcommander.internal.Lists;

import cn.sowell.copframe.dao.deferedQuery.ColumnMapResultTransformer;
import cn.sowell.copframe.dao.deferedQuery.DeferedParamQuery;
import cn.sowell.copframe.dao.deferedQuery.SimpleMapWrapper;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.dict.dao.DictionaryDao;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.pojo.DictionaryOption;
import cn.sowell.dataserver.model.dict.pojo.DictionaryRelationLabels;
import cn.sowell.dataserver.model.dict.pojo.OptionItem;


@Repository
public class DictionaryDaoImpl implements DictionaryDao{

	@Resource
	SessionFactory sFactory;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DictionaryComposite> getAllComposites(String module) {
		String hql = "from DictionaryComposite c where c.module = :module";
		DeferedParamQuery dQuery = new DeferedParamQuery(hql);
		dQuery.setParam("module", module);
		Query query = dQuery.createQuery(sFactory.getCurrentSession(), false, null);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DictionaryComposite> getAllComposites(Set<String> moduleNames) {
		if(moduleNames != null && !moduleNames.isEmpty()) {
			Criteria criteria = sFactory.getCurrentSession().createCriteria(DictionaryComposite.class);
			criteria.add(Restrictions.in("module", moduleNames));
			criteria.addOrder(Order.asc("id"));
			return criteria.list();
		}else {
			return Lists.newArrayList();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DictionaryComposite> getAllComposites() {
		Criteria criteria = sFactory.getCurrentSession().createCriteria(DictionaryComposite.class);
		criteria.add(Restrictions.isNotNull("module"));
		return criteria.list();
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, List<DictionaryField>> getAllFields(Set<Long> compositeIds) {
		if(compositeIds != null && compositeIds.size() > 0){
			String hql = "from DictionaryField f where f.compositeId in (:compositeIds)";
			Query query = sFactory.getCurrentSession().createQuery(hql);
			query.setParameterList("compositeIds", compositeIds);
			List<DictionaryField> list = query.list();
			return CollectionUtils.toListMap(list, field->field.getCompositeId());
		}else{
			return new HashMap<Long, List<DictionaryField>>();
		}
	}
	
	/*@SuppressWarnings("unchecked")
	@Override
	public List<DictionaryField> getAllFields(String module) {
		String hql = "from DictionaryField f where f.composite.module = :module";
		return sFactory.getCurrentSession().createQuery(hql).setString("module", module).list();
	}*/
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, DictionaryField> getFieldMap(Set<Long> fieldIds) {
		if(fieldIds != null && !fieldIds.isEmpty()){
			String hql = "from DictionaryField f where f.id in (:fieldIds)";
			Query query = sFactory.getCurrentSession().createQuery(hql);
			query.setParameterList("fieldIds", fieldIds, StandardBasicTypes.LONG);
			List<DictionaryField> list = query.list();
			return CollectionUtils.toMap(list, item->item.getId());
		}else{
			return new HashMap<Long, DictionaryField>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DictionaryOption> getAllOptions() {
		String hql = "from DictionaryOption o order by o.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		return query.list();
	}

	
	@Override
	public Map<Long, List<OptionItem>> getFieldOptionsMap(Set<Long> fieldIds) {
		Map<Long, List<OptionItem>> map = new HashMap<Long, List<OptionItem>>();
		if(fieldIds != null && !fieldIds.isEmpty()) {
			String sql = 
					"	SELECT" +
							"		f.id field_id, o.c_title" +
							"	FROM" +
							"		v_sa_dictionary_field f" +
							"	LEFT JOIN v_sa_dictionary_optiongroup og ON f.optgroup_id = og.id" +
							"	LEFT JOIN v_sa_dictionary_option o ON og.id = o.group_id" +
							"	where f.id in (:fieldIds)" +
//							"	and o.c_disabled is null" +
//							"	and o.c_deleted is NULL" +
//							"	and og.c_deleted is null" +
//							"	and og.c_disabled is null" +
							"	order by o.c_order asc";
			SQLQuery query = sFactory.getCurrentSession().createSQLQuery(sql);
			query.setParameterList("fieldIds", fieldIds);
			query.setResultTransformer(new ColumnMapResultTransformer<byte[]>() {
				private static final long serialVersionUID = -392302880551548725L;
				
				@Override
				protected byte[] build(SimpleMapWrapper mapWrapper) {
					OptionItem item = new OptionItem();
					item.setTitle(mapWrapper.getString("c_title"));
					item.setValue(mapWrapper.getString("c_title"));
					Long fieldId = mapWrapper.getLong("field_id");
					if(!map.containsKey(fieldId)){
						map.put(fieldId, new ArrayList<OptionItem>());
					}
					map.get(fieldId).add(item);
					return null;
				}
			});
			query.list();
		}
		return map;
	}
	
	@SuppressWarnings("serial")
	@Override
	public Map<Long, DictionaryRelationLabels> getRelationSubdomainMap(Set<Long> compositeIds) {
		Map<Long, DictionaryRelationLabels> map = new HashMap<>();
		if(compositeIds != null && !compositeIds.isEmpty()) {
			String sql = "select * from v_sa_dictionary_relation_label l where l.relation_id in (:compositeIds)";
			SQLQuery query = sFactory.getCurrentSession().createSQLQuery(sql);
			query.setParameterList("compositeIds", compositeIds);
			query.setResultTransformer(new ColumnMapResultTransformer<byte[]>() {

				@Override
				protected byte[] build(SimpleMapWrapper mapWrapper) {
					Long relationId = mapWrapper.getLong("relation_id");
					String toSplit = mapWrapper.getString("label");
					if(toSplit != null && relationId != null) {
						DictionaryRelationLabels labels = new DictionaryRelationLabels();
						labels.setRelationId(relationId);
						labels.setLabelId(mapWrapper.getLong("label_id"));
						labels.setAccess(mapWrapper.getString("opt"));
						labels.setLabels(TextUtils.split(toSplit, ",", LinkedHashSet::new, c->c));
						map.put(relationId, labels);
					}
					return null;
				}
			});
			query.list();
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DictionaryOption> queryOptions(Long optGroupId) {
		String hql = "from DictionaryOption o where o.groupId = :groupId order by o.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		query.setLong("groupId", optGroupId);
		return query.list();
	}
	
}
