package cn.sowell.dataserver.model.abc.service;

import java.util.ArrayList;
import java.util.List;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.datacenter.entityResolver.Composite;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;

public abstract class AbstractEntityQueryParameter {
	private String moduleName;
	private String relationName;
	private UserIdentifier user;
	List<ArrayItemCriteria> arrayItemCriterias = new ArrayList<>();
	public static class ArrayItemCriteria{
		private Composite composite;
		private boolean isRelation;
		private List<NormalCriteria> criterias;
		private String moduleName;
		public Composite getComposite() {
			return composite;
		}
		public void setComposite(Composite composite) {
			this.composite = composite;
		}
		public boolean isRelation() {
			return isRelation;
		}
		public void setRelation(boolean isRelation) {
			this.isRelation = isRelation;
		}
		public List<NormalCriteria> getCriterias() {
			return criterias;
		}
		public void setCriterias(List<NormalCriteria> criterias) {
			this.criterias = criterias;
		}
		public String getModuleName() {
			return moduleName;
		}
		public void setModuleName(String moduleName) {
			this.moduleName = moduleName;
		}
	}
	public AbstractEntityQueryParameter(String moduleName, UserIdentifier user) {
		super();
		this.moduleName = moduleName;
		this.user = user;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public UserIdentifier getUser() {
		return user;
	}
	public void setUser(UserIdentifier user) {
		this.user = user;
	}
	public String getRelationName() {
		return relationName;
	}
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	public List<ArrayItemCriteria> getArrayItemCriterias() {
		return arrayItemCriterias;
	}
	public void setArrayItemCriterias(List<ArrayItemCriteria> arrayItemCriterias) {
		this.arrayItemCriterias = arrayItemCriterias;
	}
}
