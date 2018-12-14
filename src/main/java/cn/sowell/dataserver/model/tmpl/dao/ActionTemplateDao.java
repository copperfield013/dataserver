package cn.sowell.dataserver.model.tmpl.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionArrayEntity;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionArrayEntityField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionTemplate;

public interface ActionTemplateDao {

	List<TemplateActionTemplate> queryTemplates();

	List<TemplateActionFieldGroup> queryFieldGroups();

	List<TemplateActionField> queryTemplateFields();

	List<TemplateActionFieldGroup> getTemplateGroups(Long atmplId);

	Map<Long, List<TemplateActionField>> getTemplateFieldsMap(Set<Long> groupIdSet);

	/**
	 * 查询所有操作模板中，一对多的实体
	 * @return
	 */
	List<TemplateActionArrayEntity> queryArrayEntities();

	List<TemplateActionArrayEntity> queryArrayEntities(Long atmplId);
	
	/**
	 * 查询所有操作模板中，一对多的实体字段
	 * @return
	 */
	List<TemplateActionArrayEntityField> queryArrayEntityFields();
	
	Map<Long, List<TemplateActionArrayEntityField>> queryArrayEntityFields(Set<Long> entityIds);


}
