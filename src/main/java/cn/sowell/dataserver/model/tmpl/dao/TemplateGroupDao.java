package cn.sowell.dataserver.model.tmpl.dao;

import java.util.List;
import java.util.Set;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupPremise;

public interface TemplateGroupDao {

	List<TemplateGroup> queryGroups(String module);

	TemplateGroup getGroup(Long groupId);

	TemplateGroup getTemplateGroup(String module, String templateGroupKey);

	List<TemplateGroup> getTemplateGroups(Set<String> moduleNames);

	List<TemplateGroup> queryGroups();

	List<TemplateGroupPremise> queryPremises();

	List<TemplateGroupPremise> queryPremises(Long groupId);

	void updateAllGroupsDetailTemplate(Long dtmplId, Long targetDtmplId);

	void updateAllGroupsListTemplate(Long ltmplId, Long targetLtmplId);

}
