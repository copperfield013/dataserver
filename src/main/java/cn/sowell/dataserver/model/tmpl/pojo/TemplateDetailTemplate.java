package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="t_sa_tmpl_detail_template")
public class TemplateDetailTemplate extends AbstractDetailTemplate<TemplateDetailFieldGroup, TemplateDetailField>{
	
}
