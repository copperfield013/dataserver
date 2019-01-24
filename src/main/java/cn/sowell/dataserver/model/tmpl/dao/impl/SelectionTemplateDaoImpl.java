package cn.sowell.dataserver.model.tmpl.dao.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.dataserver.model.tmpl.dao.SelectionTemplateDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;

@Repository
public class SelectionTemplateDaoImpl 
	extends AbstractListTemplateDao<TemplateSelectionTemplate, TemplateSelectionColumn, TemplateSelectionCriteria>
	implements SelectionTemplateDao{

	@Autowired
	public SelectionTemplateDaoImpl(@Autowired NormalOperateDao nDao, @Autowired SessionFactory sFactory) {
		super(nDao, sFactory);
	}

	@Override
	public Class<TemplateSelectionTemplate> getListTemplateClass() {
		return TemplateSelectionTemplate.class;
	}

	@Override
	public Class<TemplateSelectionColumn> getListColumnClass() {
		return TemplateSelectionColumn.class;
	}

	@Override
	public Class<TemplateSelectionCriteria> getListCriteriaClass() {
		return TemplateSelectionCriteria.class;
	}

}
