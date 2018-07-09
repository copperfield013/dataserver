package cn.sowell.dataserver.model.tmpl.dao;

import java.util.List;
import java.util.Set;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;

public interface TempalteGroupDao {

	List<TemplateGroup> queryGroups(String module);

	TemplateGroup getGroup(Long groupId);

	TemplateGroup getTemplateGroup(String module, String templateGroupKey);

	List<TemplateGroup> getTemplateGroups(Set<String> moduleNames);

	List<TemplateGroup> queryGroups();

}
