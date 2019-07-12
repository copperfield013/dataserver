package cn.sowell.dataserver.model.tmpl.duplicator.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.util.Assert;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.Constants;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractDetailFieldGroup;

public abstract class AbstractTemplateDulicator {
	@Resource
	DictionaryService dictService;
	
	/**
	 * 根据目标模块名和
	 * @param targetModuleName
	 * @param fields
	 * @param group
	 * @return
	 */
	protected DictionaryComposite analyzeModuleMapComposite(String targetModuleName, List<? extends AbstractDetailField> tfields,
			AbstractDetailFieldGroup<? extends AbstractDetailField> group) {
		Assert.notEmpty(tfields);
		//获得目标模块下的所有字段
		Map<Long, DictionaryField> fieldMap = CollectionUtils.toMap(dictService.getAllFields(targetModuleName), f->f.getId());
		Set<DictionaryComposite> composites = new LinkedHashSet<>();
		Set<AbstractDetailField> toRemoves = new HashSet<>();
		for (AbstractDetailField tfield : tfields) {
			DictionaryField field = fieldMap.get(tfield.getFieldId());
			if(field != null) {
				composites.add(field.getComposite());
			}else {
				//field不存在，则删除该字段
				toRemoves.add(tfield);
			}
		}
		for (AbstractDetailField toRemove : toRemoves) {
			tfields.remove(toRemove);
		}
		
		if(composites.isEmpty()) {
			//所有的字段都没有composite，那么肯定有问题
			throw new RuntimeException("要复制的字段组[" + group.getTitle() + "]的composite都为空");
		}else if(composites.size() == 1) {
			//字段都是在同一个composite里，直接返回该composite
			return composites.iterator().next();
		}
		//字段属于多个composite，则要判断字段
		Set<DictionaryComposite> arrayComposite = composites.stream().filter(composite->Constants.TRUE.equals(composite.getIsArray())).collect(Collectors.toSet());
		if(arrayComposite.size() == 0) {
			//全部都是非数组composite，返回空
			return null;
		}else {
			//选出field最多的composite，其他的field去除
			Map<DictionaryComposite, Integer> compositeFieldCount = new HashMap<>();
			for (AbstractDetailField tfield : tfields) {
				DictionaryField field = fieldMap.get(tfield.getFieldId());
				if(compositeFieldCount.containsKey(field.getComposite())) {
					compositeFieldCount.put(field.getComposite(), compositeFieldCount.get(field.getComposite()) + 1);
				}else {
					compositeFieldCount.put(field.getComposite(), 1);
				}
			}
			DictionaryComposite maxComposite = null;
			int max = 0;
			for (Entry<DictionaryComposite, Integer> entry : compositeFieldCount.entrySet()) {
				if(entry.getValue() > max) {
					max = entry.getValue();
					maxComposite = entry.getKey();
				}
			}
			Iterator<? extends AbstractDetailField> itr = tfields.iterator();
			//移除弱势群体
			while(itr.hasNext()) {
				AbstractDetailField tfield = itr.next();
				DictionaryField field = fieldMap.get(tfield.getFieldId());
				if(field.getComposite() != maxComposite) {
					itr.remove();
				}
			}
			return maxComposite;
		}
	}
}
