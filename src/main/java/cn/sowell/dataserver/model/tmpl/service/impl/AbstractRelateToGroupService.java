package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cn.sowell.dataserver.model.tmpl.manager.ModuleCachableManager;
import cn.sowell.dataserver.model.tmpl.pojo.Cachable;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.service.RelateToTemplateGroup;
import cn.sowell.dataserver.model.tmpl.service.TemplateGroupService;

public abstract class AbstractRelateToGroupService<T extends Cachable, M extends ModuleCachableManager<T>> 
			extends AbstractTemplateService<T, M> 
			implements RelateToTemplateGroup{
	
	private final TemplateGroupService tmplGroupService;
	
	protected AbstractRelateToGroupService(M manager, TemplateGroupService tmplGroupService) {
		super(manager);
		Assert.notNull(tmplGroupService);
		this.tmplGroupService = tmplGroupService;
	}

	/**
	 * 查询所有引用当前模板的模板组合
	 * @param ltmplIds
	 * @return
	 */
	public Map<Long, List<TemplateGroup>> getRelatedGroupsMap(Set<Long> tmplIds) {
		Map<Long, List<TemplateGroup>> map = new HashMap<>();
		if(tmplIds != null) {
			for (Long dtmplId : tmplIds) {
				map.put(dtmplId, getRelatedGroups(dtmplId));
			}
		}
		return map;
	}

	/**
	 * 获得某个模板关联的所有模板组合
	 * @param ltmplId
	 * @return
	 */
	public List<TemplateGroup> getRelatedGroups(Long tmplId) {
		return tmplGroupService.allStream()
				.filter(tmplGroup->isRelatedGroup(tmplId, tmplGroup))
				.collect(Collectors.toList());
	}

	protected abstract boolean isRelatedGroup(Long tmplId, TemplateGroup tmplGroup);

	@Transactional(propagation=Propagation.NEVER)
	protected TemplateGroupService getTemplateGroupService() {
		return this.tmplGroupService;
	}
	
	
}
