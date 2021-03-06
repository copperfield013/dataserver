package cn.sowell.dataserver.model.tmpl.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@Table(name="t_sa_tmpl_group_action")
public class TemplateGroupAction {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="c_title")
	private String title;
	
	/**
	 * 面向列表还是详情
	 */
	@Column(name="c_face")
	private String face;
	
	/**
	 * 
	 */
	@Column(name="c_type")
	private String type;
	
	/**
	 * 多选
	 */
	@Column(name="c_multiple")
	private Integer multiple;
	
	@Column(name="c_icon_class")
	private String iconClass;
	
	@Column(name="c_outgoing")
	private Integer outgoing;
	
	@Column(name="c_order")
	private Integer order;
	
	@Column(name="atmpl_id")
	private Long atmplId;
	
	@Column(name="group_id")
	private Long groupId;
	
	
	@JSONField(serialize=false)
	public static final String ACTION_FACE_LIST = "list";
	
	@JSONField(serialize=false)
	public static final String ACTION_FACE_DETAIL = "detail";
	
	@JSONField(serialize=false)
	public static final Integer ACTION_MULTIPLE_SINGLE = 0;
	
	@JSONField(serialize=false)
	public static final Integer ACTION_MULTIPLE_NORMAL = 1;
	
	@JSONField(serialize=false)
	public static final Integer ACTION_MULTIPLE_TRANSACTION = 2;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getAtmplId() {
		return atmplId;
	}

	public void setAtmplId(Long atmplId) {
		this.atmplId = atmplId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Integer getMultiple() {
		return multiple;
	}

	public void setMultiple(Integer multiple) {
		this.multiple = multiple;
	}

	public String getIconClass() {
		return iconClass;
	}

	public void setIconClass(String iconClass) {
		this.iconClass = iconClass;
	}

	public Integer getOutgoing() {
		return outgoing;
	}

	public void setOutgoing(Integer outgoing) {
		this.outgoing = outgoing;
	}
}
