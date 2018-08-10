package cn.sowell.dataserver.model.dict.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="v_sa_dictionary_optiongroup")
public class DictionaryOptionGroup {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="c_title")
	private String title;
	
//	@Column(name="c_comment")
//	private String comment;
//	
//	@Column(name="c_disabled")
//	private Integer disabled;
//	
//	@Column(name="c_deleted")
//	private Integer deleted;
//	
//	@Column(name="create_user_id")
//	private Long createUserId;
//	
//	@Column(name="create_time")
//	private Date createTime;
//	
//	@Column(name="update_time")
//	private Date updateTime;
//	
//	@Column(name="c_authority")
//	private String authority;
	
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
//	public String getComment() {
//		return comment;
//	}
//	public void setComment(String comment) {
//		this.comment = comment;
//	}
//	public Integer getDisabled() {
//		return disabled;
//	}
//	public void setDisabled(Integer disabled) {
//		this.disabled = disabled;
//	}
//	public Integer getDeleted() {
//		return deleted;
//	}
//	public void setDeleted(Integer deleted) {
//		this.deleted = deleted;
//	}
//	public Long getCreateUserId() {
//		return createUserId;
//	}
//	public void setCreateUserId(Long createUserId) {
//		this.createUserId = createUserId;
//	}
//	public Date getCreateTime() {
//		return createTime;
//	}
//	public void setCreateTime(Date createTime) {
//		this.createTime = createTime;
//	}
//	public Date getUpdateTime() {
//		return updateTime;
//	}
//	public void setUpdateTime(Date updateTime) {
//		this.updateTime = updateTime;
//	}
//	public String getAuthority() {
//		return authority;
//	}
//	public void setAuthority(String authority) {
//		this.authority = authority;
//	}
}
