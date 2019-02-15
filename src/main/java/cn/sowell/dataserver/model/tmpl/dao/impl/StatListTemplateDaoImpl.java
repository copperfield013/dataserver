package cn.sowell.dataserver.model.tmpl.dao.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.dataserver.model.tmpl.dao.StatListTemplateDao;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatList;

@Repository
public class StatListTemplateDaoImpl 
	extends AbstractListTemplateDao<TemplateStatList, TemplateStatColumn, TemplateStatCriteria>
	implements StatListTemplateDao{

	protected StatListTemplateDaoImpl(@Autowired NormalOperateDao nDao, @Autowired SessionFactory sFactory) {
		super(nDao, sFactory);
	}

	@Override
	public Class<TemplateStatList> getListTemplateClass() {
		return TemplateStatList.class;
	}

	@Override
	public Class<TemplateStatColumn> getListColumnClass() {
		return TemplateStatColumn.class;
	}

	@Override
	public Class<TemplateStatCriteria> getListCriteriaClass() {
		return TemplateStatCriteria.class;
	}

}
