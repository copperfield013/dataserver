package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_sa_tmpl_group_dictfilter")
public class TemplateGroupDictionaryFilter {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Transient
	private Long tmplGroupId;
	
	@Column(name="filter_id")
	private Long filterId;
	
	@Transient
	private TemplateDictionaryFilter filter;
	
	@Column(name="c_with_module")
	private Integer withModule;
	
	@Column(name="c_with_dtmpl")
	private Integer withDetailTemplate;
	
	@Column(name="c_with_ltmpl")
	private Integer withListTemplate;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getTmplGroupId() {
		return tmplGroupId;
	}
	public void setTmplGroupId(Long tmplGroupId) {
		this.tmplGroupId = tmplGroupId;
	}
	public Long getFilterId() {
		return filterId;
	}
	public void setFilterId(Long filterId) {
		this.filterId = filterId;
	}
	public Integer getWithListTemplate() {
		return withListTemplate;
	}
	public void setWithListTemplate(Integer withListTemplate) {
		this.withListTemplate = withListTemplate;
	}
	public Integer getWithModule() {
		return withModule;
	}
	public void setWithModule(Integer withModule) {
		this.withModule = withModule;
	}
	public TemplateDictionaryFilter getFilter() {
		return filter;
	}
	public void setFilter(TemplateDictionaryFilter filter) {
		this.filter = filter;
	}
	public Integer getWithDetailTemplate() {
		return withDetailTemplate;
	}
	public void setWithDetailTemplate(Integer withDetailTemplate) {
		this.withDetailTemplate = withDetailTemplate;
	}
	
}
