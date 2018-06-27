package cn.sowell.dataserver.model.modules.pojo;

import java.util.Calendar;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class EntityHistoryItem {
	private Long id;
	private Date time;
	private String userName;
	private String desc;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@JSONField(name="monthKey")
	public Long getMonthKey() {
		if(time != null){
			Calendar c = Calendar.getInstance();
			c.setTime(time);
			c.set(Calendar.DATE, 1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			return c.getTimeInMillis();
		}
		return null;
	}
	@JSONField(name="timeKey")
	public Long getTimeKey() {
		if(time != null){
			return time.getTime();
		}else{
			return null;
		}
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
}
