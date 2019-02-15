package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="t_sa_tmpl_stat_list_template")
public class TemplateStatList extends AbstractListTemplate<TemplateStatColumn, TemplateStatCriteria>{
	
}
