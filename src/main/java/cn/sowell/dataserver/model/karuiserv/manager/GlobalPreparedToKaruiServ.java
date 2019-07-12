package cn.sowell.dataserver.model.karuiserv.manager;

import java.util.List;
import java.util.Map;

import cn.sowell.dataserver.model.cachable.prepare.PreparedToCache;
import cn.sowell.dataserver.model.karuiserv.pojo.KaruiServCriteria;
import cn.sowell.dataserver.model.karuiserv.pojo.KaruiServJsonMeta;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

public class GlobalPreparedToKaruiServ extends PreparedToCache{
	
	public Map<Long, KaruiServJsonMeta> getJsonMetaMap() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static class PreparedToKaruiServ extends PreparedToCache{
		
		
		private List<KaruiServCriteria> criterias;
		private TemplateListTemplate listTemplate;
		private TemplateDetailTemplate detailTemplate;
		private KaruiServJsonMeta responseJsonMeta;
		private KaruiServJsonMeta criteriaJsonMeta;

		public List<KaruiServCriteria> getCriterias() {
			return this.criterias;
		}

		public TemplateListTemplate getListTemplate() {
			return this.listTemplate;
		}

		public TemplateDetailTemplate getDetailTemplate() {
			return this.detailTemplate;
		}

		public KaruiServJsonMeta getResponseJsonMeta() {
			return this.responseJsonMeta;
		}

		public KaruiServJsonMeta getCriteriaJsonMeta() {
			return this.criteriaJsonMeta;
		}

		public void setCriterias(List<KaruiServCriteria> criterias) {
			this.criterias = criterias;
		}

		public void setListTemplate(TemplateListTemplate listTemplate) {
			this.listTemplate = listTemplate;
		}

		public void setDetailTemplate(TemplateDetailTemplate detailTemplate) {
			this.detailTemplate = detailTemplate;
		}

		public void setResponseJsonMeta(KaruiServJsonMeta responseJsonMeta) {
			this.responseJsonMeta = responseJsonMeta;
		}
		
	}
}
