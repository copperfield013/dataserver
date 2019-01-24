package cn.sowell.dataserver.model.tmpl.dao;

import java.util.List;
import java.util.Set;

import cn.sowell.dataserver.model.cachable.dao.CachableDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupAction;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupDictionaryFilter;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupPremise;

public interface TemplateGroupDao extends CachableDao<TemplateGroup>{

	List<TemplateGroup> queryGroups(String module);

	List<TemplateGroup> getTemplateGroups(Set<String> moduleNames);

	List<TemplateGroup> queryGroups();

	List<TemplateGroupPremise> queryPremises();

	List<TemplateGroupPremise> queryPremises(Long groupId);

	void updateAllGroupsDetailTemplate(Long dtmplId, Long targetDtmplId);

	void updateAllGroupsListTemplate(Long ltmplId, Long targetLtmplId);

	List<TemplateGroupAction> queryActions();

	List<TemplateGroupAction> queryActions(Long groupId);

	List<TemplateGroupDictionaryFilter> queryGroupDictionaryFilters();

}
