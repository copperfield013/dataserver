package cn.sowell.dataserver.model.tmpl.dao.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.dataserver.model.tmpl.dao.ListTemplateDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

@Repository
public class ListTemplateDaoImpl 
	extends AbstractListTemplateDao<TemplateListTemplate, TemplateListColumn, TemplateListCriteria> 
	implements ListTemplateDao{

	@Autowired
	protected ListTemplateDaoImpl(@Autowired NormalOperateDao nDao, @Autowired SessionFactory sFactory) {
		super(nDao, sFactory);
	}

	@Override
	public Class<TemplateListTemplate> getListTemplateClass() {
		return TemplateListTemplate.class;
	}

	@Override
	public Class<TemplateListColumn> getListColumnClass() {
		return TemplateListColumn.class;
	}

	@Override
	public Class<TemplateListCriteria> getListCriteriaClass() {
		return TemplateListCriteria.class;
	}

}
