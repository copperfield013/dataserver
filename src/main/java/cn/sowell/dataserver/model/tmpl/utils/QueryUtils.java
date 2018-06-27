package cn.sowell.dataserver.model.tmpl.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import cn.sowell.copframe.dao.deferedQuery.ColumnMapResultTransformer;
import cn.sowell.copframe.dao.deferedQuery.DeferedParamQuery;
import cn.sowell.copframe.dao.deferedQuery.HibernateRefrectResultTransformer;
import cn.sowell.copframe.dao.deferedQuery.SimpleMapWrapper;
import cn.sowell.copframe.dao.deferedQuery.sqlFunc.WrapForCountFunction;
import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.copframe.utils.FormatUtils;

public class QueryUtils {
	/**
	 * 根据BaseModel的参数设置Query对象的分页参数
	 * @param query
	 * @param criteria
	 */
	public static void setPagingParamWithCriteria(Query query,
			PageInfo pageInfo) {
		if(query != null && pageInfo != null){
			if(pageInfo.getCount() != null && pageInfo.getPageSize() != null){
				if(pageInfo.getPageNo() > (pageInfo.getCount() / pageInfo.getPageSize()) + 1){
					pageInfo.setPageNo((pageInfo.getCount() / pageInfo.getPageSize()) + 1);
				}
			}
			query.setFirstResult(pageInfo.getFirstIndex());
			query.setMaxResults(pageInfo.getPageSize());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> queryList(String sql, Class<T> itemClass, Session session, Consumer<DeferedParamQuery> consumer){
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		if(consumer != null){
			consumer.accept(dQuery);
		}
		SQLQuery query = dQuery.createSQLQuery(session, false, null);
		query.setResultTransformer(HibernateRefrectResultTransformer.getInstance(itemClass));
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> pagingSQLQuery(String sql, Class<T> itemClass, Session session, PageInfo pageInfo, Consumer<DeferedParamQuery> consumer){
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		if(consumer != null){
			consumer.accept(dQuery);
		}
		SQLQuery countQuery = dQuery.createSQLQuery(session, false, new WrapForCountFunction());
		if(pageInfo != null){
			pageInfo.setCount(FormatUtils.toInteger(countQuery.uniqueResult()));
		}
		if(pageInfo == null || pageInfo.getCount() > 0){
			SQLQuery query = dQuery.createSQLQuery(session, false, null);
			query.setResultTransformer(HibernateRefrectResultTransformer.getInstance(itemClass));
			setPagingParamWithCriteria(query, pageInfo);
			return query.list();
		}else{
			return new ArrayList<T>();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> pagingQuery(String hql, Session session, PageInfo pageInfo, Consumer<DeferedParamQuery> consumer){
		DeferedParamQuery dQuery = new DeferedParamQuery(hql);
		if(consumer != null){
			consumer.accept(dQuery);
		}
		Query countQuery = dQuery.createQuery(session, false, new WrapForCountFunction());
		if(pageInfo != null){
			pageInfo.setCount(FormatUtils.toInteger(countQuery.uniqueResult()));
		}
		if(pageInfo == null || pageInfo.getCount() > 0){
			Query query = dQuery.createQuery(session, false, null);
			setPagingParamWithCriteria(query, pageInfo);
			return query.list();
		}else{
			return new ArrayList<T>();
		}
	}
	
	
	@SuppressWarnings("serial")
	public static <K, V> Map<K, V> queryMap(String sql, Session session, Function<SimpleMapWrapper, K> keyGetter, Function<SimpleMapWrapper, V> valueGetter, Consumer<DeferedParamQuery> consumer){
		Map<K, V> groupMap = new HashMap<K, V>();
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		if(consumer != null){
			consumer.accept(dQuery);
		}
		SQLQuery query = dQuery.createSQLQuery(session, false, null);
		query.setResultTransformer(new ColumnMapResultTransformer<Void>() {
			
			@Override
			protected Void build(SimpleMapWrapper mapWrapper) {
				groupMap.put(keyGetter.apply(mapWrapper), valueGetter.apply(mapWrapper));
				return null;
			}
		});
		query.list();
		return groupMap;
	}
	
	public static <K, V> Map<K, V> queryMap(String sql, Session session, Function<SimpleMapWrapper, K> keyGetter, Class<V> valueClass, Consumer<DeferedParamQuery> consumer){
		return queryMap(sql, session, keyGetter,
				mapWrapper->HibernateRefrectResultTransformer.getInstance(valueClass).build(mapWrapper),
				consumer);
	}
	
}
