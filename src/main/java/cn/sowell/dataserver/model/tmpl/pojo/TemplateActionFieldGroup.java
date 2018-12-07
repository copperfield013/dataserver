package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;

import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;

@Entity
@Table(name="t_sa_tmpl_action_fieldgroup")
public class TemplateActionFieldGroup {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="tmpl_id")
	private Long tmplId;
	
	@Column(name="c_title")
	private String title;
	
	@Column(name="c_is_array")
	private Integer isArray;
	
	@Column(name="stmpl_id")
	private Long selectionTemplateId;
	
	@Column(name="composite_id")
	private Long compositeId;
	
	@Transient
	private String relationLabelAccess;
	
	@Transient
	private String additionRelationLabelAccess;
	
	@Column(name="c_unallowed_create")
	private Integer unallowedCreate;
	
	@Transient
	private DictionaryComposite composite;
	
	@Column(name="c_order")
	private Integer order;
	
	@Column(name="c_unmodifiable")
	private Integer unmodifiable;
	
	@Column(name="update_time")
	private Date updateTime;
	
	
	@Transient
	private List<TemplateActionField> fields = new ArrayList<TemplateActionField>();
	
	@JSONField(name="relationSubdomain")
	public Set<String> getRelationSubdomain() {
		if(composite != null) {
			return composite.getRelationSubdomain();
		}
		return null;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getTmplId() {
		return tmplId;
	}
	public void setTmplId(Long tmplId) {
		this.tmplId = tmplId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public Integer getUnmodifiable() {
		return unmodifiable;
	}
	public void setUnmodifiable(Integer unmodifiable) {
		this.unmodifiable = unmodifiable;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public List<TemplateActionField> getFields() {
		return fields;
	}
	public void setFields(List<TemplateActionField> fields) {
		this.fields = fields;
	}
	public Integer getIsArray() {
		return isArray;
	}
	public void setIsArray(Integer isArray) {
		this.isArray = isArray;
	}
	public Long getCompositeId() {
		return compositeId;
	}
	public void setCompositeId(Long compositeId) {
		this.compositeId = compositeId;
	}
	public DictionaryComposite getComposite() {
		return composite;
	}
	public void setComposite(DictionaryComposite composite) {
		this.composite = composite;
	}

	public Long getSelectionTemplateId() {
		return selectionTemplateId;
	}

	public void setSelectionTemplateId(Long selectionTemplateId) {
		this.selectionTemplateId = selectionTemplateId;
	}

	public String getRelationLabelAccess() {
		return relationLabelAccess;
	}

	public void setRelationLabelAccess(String relationLabelAccess) {
		this.relationLabelAccess = relationLabelAccess;
	}

	public String getAdditionRelationLabelAccess() {
		return additionRelationLabelAccess;
	}

	public void setAdditionRelationLabelAccess(String additionRelationLabelAccess) {
		this.additionRelationLabelAccess = additionRelationLabelAccess;
	}

	public Integer getUnallowedCreate() {
		return unallowedCreate;
	}

	public void setUnallowedCreate(Integer unallowedCreate) {
		this.unallowedCreate = unallowedCreate;
	}

}
