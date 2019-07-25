package cn.sowell.dataserver.model.dict.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.beust.jcommander.internal.Lists;

import cho.carbon.meta.vo.DictionaryCompositeVO;
import cho.carbon.meta.vo.DictionaryFieldVO;
import cho.carbon.meta.vo.DictionaryOptionVO;
import cho.carbon.panel.HydCarbonMetadata;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.dict.dao.DictionaryDao;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.pojo.DictionaryOption;
import cn.sowell.dataserver.model.dict.pojo.DictionaryRelationLabels;
import cn.sowell.dataserver.model.dict.pojo.OptionItem;


@Repository
public class DictionaryDaoImpl implements DictionaryDao{

	@Override
	public List<DictionaryComposite> getAllComposites(String module) {
		Collection<DictionaryCompositeVO> composites = HydCarbonMetadata.getInstance().getAllDicFieldComposite();
		return composites.stream().filter(c->module.equals(c.getModuleName()))
			.map(DictionaryDaoImpl::toComposite)
			.collect(Collectors.toList());
		
		/*
		 * String hql = "from DictionaryComposite c where c.module = :module";
		 * DeferedParamQuery dQuery = new DeferedParamQuery(hql);
		 * dQuery.setParam("module", module); Query query =
		 * dQuery.createQuery(sFactory.getCurrentSession(), false, null); return
		 * query.list();
		 */
	}
	
	private static DictionaryComposite toComposite(DictionaryCompositeVO source) {
		if(source == null) return null;
		DictionaryComposite composite = new DictionaryComposite();
		composite.setId(source.getId());
		composite.setId(source.getId());
		composite.setName(source.getTitle());
		composite.setTitle(source.getTitle());
		composite.setModule(source.getModuleName());
		composite.setIsArray(source.getIsArray());
		composite.setAddType(source.getAddType());
		composite.setRelModuleName(source.getPointModuleName());
		composite.setAccess(source.getOpt().getName());
		return composite;
	}
	
	@Override
	public List<DictionaryComposite> getAllComposites(Set<String> moduleNames) {
		if(moduleNames != null && !moduleNames.isEmpty()) {
			return HydCarbonMetadata.getInstance().getAllDicFieldComposite().stream()
				.filter(composite->moduleNames.contains(composite.getModuleName()))
				.map(DictionaryDaoImpl::toComposite)
				.collect(Collectors.toList());
//			Criteria criteria = sFactory.getCurrentSession().createCriteria(DictionaryComposite.class);
//			criteria.add(Restrictions.in("module", moduleNames));
//			criteria.addOrder(Order.asc("id"));
//			return criteria.list();
		}else {
			return Lists.newArrayList();
		}
	}
	
	@Override
	public List<DictionaryComposite> getAllComposites() {
		return HydCarbonMetadata.getInstance().getAllDicFieldComposite().stream()
				.map(DictionaryDaoImpl::toComposite)
				.collect(Collectors.toList());
		
		//Criteria criteria = sFactory.getCurrentSession().createCriteria(DictionaryComposite.class);
		//criteria.add(Restrictions.isNotNull("module"));
		//return criteria.list();
	}
	

	@Override
	public Map<Integer, List<DictionaryField>> getAllFields(Set<Integer> compositeIds) {
		if(compositeIds != null && compositeIds.size() > 0){
			return CollectionUtils.toListMap(HydCarbonMetadata.getInstance().getAllDicField()
					.stream()
					.filter(f->compositeIds.contains(f.getCompositeId()))
					.map(DictionaryDaoImpl::toField)
					.collect(Collectors.toSet()), DictionaryField::getCompositeId)
					;
			
//			
//			String hql = "from DictionaryField f where f.compositeId in (:compositeIds)";
//			Query query = sFactory.getCurrentSession().createQuery(hql);
//			query.setParameterList("compositeIds", compositeIds);
//			List<DictionaryField> list = query.list();
//			return CollectionUtils.toListMap(list, field->field.getCompositeId());
		}else{
			return new HashMap<Integer, List<DictionaryField>>();
		}
	}
	
	/*@SuppressWarnings("unchecked")
	@Override
	public List<DictionaryField> getAllFields(String module) {
		String hql = "from DictionaryField f where f.composite.module = :module";
		return sFactory.getCurrentSession().createQuery(hql).setString("module", module).list();
	}*/
	
	@Override
	public Map<Integer, DictionaryField> getFieldMap(Set<Integer> fieldIds) {
		if(fieldIds != null && !fieldIds.isEmpty()) {
			return HydCarbonMetadata.getInstance().getAllDicField()
				.stream()
				.filter(f->fieldIds.contains(f.getId()))
				.collect(Collectors.toMap(DictionaryFieldVO::getId, DictionaryDaoImpl::toField))
				;
		}else {
			return new HashMap<Integer, DictionaryField>();
		}
		
//		
//		if(fieldIds != null && !fieldIds.isEmpty()){
//			String hql = "from DictionaryField f where f.id in (:fieldIds)";
//			Query query = sFactory.getCurrentSession().createQuery(hql);
//			query.setParameterList("fieldIds", fieldIds, StandardBasicTypes.LONG);
//			List<DictionaryField> list = query.list();
//			return CollectionUtils.toMap(list, item->item.getId());
//		}else{
//			return new HashMap<Long, DictionaryField>();
//		}
	}
	
	private static DictionaryField toField(DictionaryFieldVO source) {
		if(source == null) return null;
		DictionaryField field = new DictionaryField();
		field.setId(source.getId());
		field.setTitle(source.getTitle());
		field.setCompositeId(source.getCompositeId());
		field.setFullKey(source.getFullKey());
		field.setFieldAccess(source.getOpt().getName());
		field.setType(source.getType());
		field.setAbcType(source.getAbcType().getName());
		field.setOptionGroupId(source.getOptGroupId());
		field.setCasLevel(source.getCasLevel());
		field.setAbcAttrCode(source.getItemCode());
		field.setModuleName(source.getModuleName());
		return field;
	}
	
	@Override
	public List<DictionaryOption> getAllOptions() {
		return HydCarbonMetadata.getInstance().getAllDicOption()
			.stream().map(DictionaryDaoImpl::toOption)
			.collect(Collectors.toList());
		/*String hql = "from DictionaryOption o order by o.order asc";
		Query query = sFactory.getCurrentSession().createQuery(hql);
		return query.list();*/
	}

	private static DictionaryOption toOption(DictionaryOptionVO source) {
		if(source == null) return null;
		DictionaryOption option = new DictionaryOption();
		option.setId(source.getId());
		option.setTitle(source.getTitle());
		option.setGroupId(source.getGroupId());
		option.setOrder(source.getOrder());
		return option;
	}
	
	private static OptionItem toOptionItem(DictionaryOptionVO source) {
		if(source == null) return null;
		OptionItem item = new OptionItem();
		item.setTitle(source.getTitle());
		item.setValue(source.getTitle());
		return item;
	}
	
	
	@Override
	public Map<Integer, List<OptionItem>> getFieldOptionsMap(Set<Integer> fieldIds) {
		Map<Integer, List<OptionItem>> map = new HashMap<Integer, List<OptionItem>>();
		if(fieldIds != null && !fieldIds.isEmpty()) {
			Set<DictionaryFieldVO> fields = HydCarbonMetadata.getInstance().getAllDicField().stream().filter(field->fieldIds.contains(field.getId())).collect(Collectors.toSet());
			
			for (DictionaryFieldVO field : fields) {
				if(field.getOptGroupId() != null) {
					HydCarbonMetadata.getInstance().getAllDicOption().forEach(option->{
						if(option.getGroupId().equals(field.getOptGroupId())) {
							if(!map.containsKey(field.getId())) {
								map.put(field.getId(), new ArrayList<OptionItem>());
							}
							map.get(field.getId()).add(toOptionItem(option));
						}
					});
				}
			}
		}
		return map;
	}
	
	@Override
	public Map<Integer, DictionaryRelationLabels> getRelationSubdomainMap(Set<Integer> compositeIds) {
		Map<Integer, DictionaryRelationLabels> map = new HashMap<>();
		if(compositeIds != null && !compositeIds.isEmpty()) {
			List<DictionaryCompositeVO> composites = HydCarbonMetadata.getInstance().getAllDicFieldComposite().stream().filter(c->compositeIds.contains(c.getId())).collect(Collectors.toList());
			for (DictionaryCompositeVO composite : composites) {
				if(composite.getRelationTypeNames() != null) {
					map.put(composite.getId(), toLabel(composite));
				}
			}
			
//			String sql = "select * from v_sa_dictionary_relation_label l where l.relation_id in (:compositeIds)";
//			SQLQuery query = sFactory.getCurrentSession().createSQLQuery(sql);
//			query.setParameterList("compositeIds", compositeIds);
//			query.setResultTransformer(new ColumnMapResultTransformer<byte[]>() {
//
//				@Override
//				protected byte[] build(SimpleMapWrapper mapWrapper) {
//					Long relationId = mapWrapper.getLong("relation_id");
//					String toSplit = mapWrapper.getString("label");
//					if(toSplit != null && relationId != null) {
//						DictionaryRelationLabels labels = new DictionaryRelationLabels();
//						labels.setRelationId(relationId);
//						labels.setLabelId(mapWrapper.getLong("label_id"));
//						labels.setAccess(mapWrapper.getString("opt"));
//						labels.setLabels(TextUtils.split(toSplit, ",", LinkedHashSet::new, c->c));
//						map.put(relationId, labels);
//					}
//					return null;
//				}
//			});
//			query.list();
		}
		return map;
	}
	
	private DictionaryRelationLabels toLabel(DictionaryCompositeVO composite) {
		DictionaryRelationLabels labels = new DictionaryRelationLabels();
		labels.setAccess(composite.getOpt().getName());
		labels.setRelationId(composite.getId());
		labels.setLabels(new LinkedHashSet<String>(composite.getRelationTypeNames()));
		return labels;
	}

	@Override
	public List<DictionaryOption> queryOptions(Integer optGroupId) {
		return HydCarbonMetadata.getInstance().getAllDicOption().stream()
			.filter(option->optGroupId.equals(option.getGroupId()))
			.map(DictionaryDaoImpl::toOption)
			.collect(Collectors.toList());
		
		
		
//		String hql = "from DictionaryOption o where o.groupId = :groupId order by o.order asc";
//		Query query = sFactory.getCurrentSession().createQuery(hql);
//		query.setLong("groupId", optGroupId);
//		return query.list();
	}
	
}
