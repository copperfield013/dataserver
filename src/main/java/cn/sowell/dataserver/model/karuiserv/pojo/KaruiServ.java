package cn.sowell.dataserver.model.karuiserv.pojo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.sowell.dataserver.model.karuiserv.jsonresolver.ResponseJsonMetaResolver;
import cn.sowell.dataserver.model.tmpl.pojo.Cachable;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

@Entity
@Table(name="t_sa_ks_serv")
public class KaruiServ extends Cachable{
	
	public static final String TYPE_SINGLE_QUERY = "single-query";
	public static final String TYPE_MULTI_QUERY = "multi-query";
	public static final String TYPE_SINGLE_UPDATE = "single-update";
	public static final String TYPE_MULTI_UPDATE = "multi-update";

	@Column(name="c_path")
	private String path;
	
	@Column(name="c_desc")
	private String description;
	
	/**
	 * 服务类型
	 */
	@Column(name="c_type")
	private String type;
	
	@Column(name="dtmpl_id")
	private Long detailTemplateId;
	
	@Column(name="ltmpl_id")
	private Long listTemplateId;
	
	@Transient
	private TemplateDetailTemplate detailTemplate;
	
	@Transient
	private TemplateListTemplate listTemplate;
	
	
	@Column(name="unique_field_id")
	private Long uniqueFieldId;
	
	@Transient
	private List<KaruiServCriteria> criterias;
	
	@Column(name="c_res_meta")
	private String responseMeta;
	
	@Column(name="c_req_meta")
	private String requestMeta;
	
	@Column(name="c_disabled")
	private Integer disabled;
	
	@Transient
	private ResponseJsonMetaResolver responseJsonMetaResolver;
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getDetailTemplateId() {
		return detailTemplateId;
	}
	public void setDetailTemplateId(Long detailTemplateId) {
		this.detailTemplateId = detailTemplateId;
	}
	public Long getListTemplateId() {
		return listTemplateId;
	}
	public void setListTemplateId(Long listTemplateId) {
		this.listTemplateId = listTemplateId;
	}
	public Long getUniqueFieldId() {
		return uniqueFieldId;
	}
	public void setUniqueFieldId(Long uniqueFieldId) {
		this.uniqueFieldId = uniqueFieldId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<KaruiServCriteria> getCriterias() {
		return criterias;
	}
	public void setCriterias(List<KaruiServCriteria> criterias) {
		this.criterias = criterias;
	}
	public TemplateDetailTemplate getDetailTemplate() {
		return detailTemplate;
	}
	public void setDetailTemplate(TemplateDetailTemplate detailTemplate) {
		this.detailTemplate = detailTemplate;
	}
	public TemplateListTemplate getListTemplate() {
		return listTemplate;
	}
	public void setListTemplate(TemplateListTemplate listTemplate) {
		this.listTemplate = listTemplate;
	}
	public String getResponseMeta() {
		return responseMeta;
	}
	public void setResponseMeta(String responseMeta) {
		this.responseMeta = responseMeta;
	}
	public String getRequestMeta() {
		return requestMeta;
	}
	public void setRequestMeta(String requestMeta) {
		this.requestMeta = requestMeta;
	}
	public ResponseJsonMetaResolver getResponseJsonMetaResolver() {
		return responseJsonMetaResolver;
	}
	public void setResponseJsonMetaResolver(ResponseJsonMetaResolver responseJsonMetaResolver) {
		this.responseJsonMetaResolver = responseJsonMetaResolver;
	}
	public Integer getDisabled() {
		return disabled;
	}
	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}
}
