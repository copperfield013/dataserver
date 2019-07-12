package cn.sowell.dataserver.model.karuiserv.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.cachable.manager.AbstractModuleCacheManager;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.karuiserv.dao.KaruiServDao;
import cn.sowell.dataserver.model.karuiserv.manager.GlobalPreparedToKaruiServ;
import cn.sowell.dataserver.model.karuiserv.manager.GlobalPreparedToKaruiServ.PreparedToKaruiServ;
import cn.sowell.dataserver.model.karuiserv.manager.KaruiServJsonMetaManager;
import cn.sowell.dataserver.model.karuiserv.manager.KaruiServManager;
import cn.sowell.dataserver.model.karuiserv.pojo.KaruiServ;
import cn.sowell.dataserver.model.karuiserv.pojo.KaruiServCriteria;
import cn.sowell.dataserver.model.tmpl.manager.DetailTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.ListTemplateManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;
import cn.sowell.dataserver.model.tmpl.strategy.NormalDaoSetUpdateStrategy;

@Repository
public class KaruiServManagerImpl 
	extends AbstractModuleCacheManager<KaruiServ, KaruiServDao, GlobalPreparedToKaruiServ, PreparedToKaruiServ>
	implements KaruiServManager{

	@Resource
	KaruiServJsonMetaManager jsonMetaManager;
	
	@Resource
	DetailTemplateManager dtmplManager;
	
	@Resource
	ListTemplateManager ltmplManager;
	
	
	private Map<Long, KaruiServCriteria> criteriasMap;

	protected KaruiServManagerImpl(KaruiServDao dao, ModuleCachableMetaSupportor metaSupportor) {
		super(dao, metaSupportor);
	}
	
	
	private Collection<KaruiServCriteria> getKaruiServCriterias() {
		if(this.criteriasMap == null) {
			synchronized(this) {
				if(this.criteriasMap == null) {
					List<KaruiServCriteria> criterias = getDao().queryAllCriterias();
					if(criterias != null) {
						this.criteriasMap = CollectionUtils.toMap(criterias, c->c.getId());
					}
				}
			}
		}
		return this.criteriasMap.values();
	}

	@Override
	protected GlobalPreparedToKaruiServ getGlobalPreparedToCache() {
		GlobalPreparedToKaruiServ gp = new GlobalPreparedToKaruiServ();
		
		
		return gp;
	}

	@Override
	protected PreparedToKaruiServ extractPrepare(GlobalPreparedToKaruiServ globalPreparedToCache, KaruiServ ks) {
		return getPreparedToCache(ks);
	}

	@Override
	protected PreparedToKaruiServ getPreparedToCache(KaruiServ ks) {
		PreparedToKaruiServ prepared = new PreparedToKaruiServ();
		/*
		 * if(ks.getResponseJsonMetaId() != null) { KaruiServJsonMeta responseJsonMeta =
		 * jsonMetaManager.get(ks.getResponseJsonMetaId());
		 * prepared.setResponseJsonMeta(responseJsonMeta); }
		 * if(ks.getCriteriaJsonMetaId() != null) { KaruiServJsonMeta criteriaJsonMeta =
		 * jsonMetaManager.get(ks.getCriteriaJsonMetaId());
		 * prepared.setResponseJsonMeta(criteriaJsonMeta); }
		 */
		List<KaruiServCriteria> ksCriterias = getKaruiServCriterias().stream().filter((c)->ks.getId().equals(c.getKaruiServId())).collect(Collectors.toList());
		prepared.setCriterias(ksCriterias);
		if(ks.getDetailTemplateId() != null) {
			prepared.setDetailTemplate(dtmplManager.get(ks.getDetailTemplateId()));
		}
		if(ks.getListTemplateId() != null) {
			prepared.setListTemplate(ltmplManager.get(ks.getListTemplateId()));
		}
		return prepared;
	}

	@Override
	protected void handlerCache(KaruiServ ks, PreparedToKaruiServ prepareToCache) {
		/*
		 * ks.setResponseJsonMeta(prepareToCache.getResponseJsonMeta());
		 * ks.setCriteriaJsonMeta(prepareToCache.getCriteriaJsonMeta());
		 */
		ks.setCriterias(prepareToCache.getCriterias());
		ks.setListTemplate(prepareToCache.getListTemplate());
		ks.setDetailTemplate(prepareToCache.getDetailTemplate());
	}

	@Override
	protected KaruiServ createCachablePojo() {
		return new KaruiServ();
	}

	@Override
	protected Long doCreate(KaruiServ ks) {
		ks.setCreateTime(new Date());
		ks.setUpdateTime(ks.getCreateTime());
		Long ksId = getDao().getNormalOperateDao().save(ks);
		if(ks.getCriterias() != null) {
			for (KaruiServCriteria criteria : ks.getCriterias()) {
				criteria.setKaruiServId(ksId);
				criteria.setCreateTime(ks.getCreateTime());
				criteria.setUpdateTime(ks.getCreateTime());
				getDao().getNormalOperateDao().save(criteria);
			}
		}
		return ksId;
	}

	@Override
	protected void doUpdate(KaruiServ ks) {
		KaruiServ origin = getDao().getNormalOperateDao().get(KaruiServ.class, ks.getId());
		
		Collection<KaruiServCriteria> originCriterias = getDao().getCriteriasByKsId(ks.getId());
		origin.setTitle(ks.getTitle());
		origin.setPath(ks.getPath());
		origin.setAuthority(ks.getAuthority());
		origin.setDescription(ks.getDescription());
		origin.setType(ks.getType());
		origin.setDetailTemplateId(ks.getDetailTemplateId());
		origin.setListTemplateId(ks.getListTemplateId());
		origin.setRequestMeta(ks.getRequestMeta());
		origin.setResponseMeta(ks.getResponseMeta());
//		origin.setCriteriaJsonMetaId(ks.getCriteriaJsonMetaId());
//		origin.setResponseJsonMetaId(ks.getResponseJsonMetaId());
		origin.setUpdateTime(new Date());
		NormalDaoSetUpdateStrategy.build(KaruiServCriteria.class, getDao().getNormalOperateDao(), 
				cri->cri.getId(), 
				(oCriteria, criteria)->{
					oCriteria.setName(criteria.getName());
					oCriteria.setUpdateTime(origin.getUpdateTime());
				}, 
				criteria->{
					criteria.setKaruiServId(origin.getId());
					criteria.setCreateTime(origin.getUpdateTime());
					criteria.setUpdateTime(criteria.getCreateTime());
				})
			.doUpdate(originCriterias, ks.getCriterias());
	}


	@Override
	public List<KaruiServ> queryAll() {
		return new ArrayList<KaruiServ>(getCachableMap().values());
	}

	
	@Override
	public synchronized void clearCache() {
		super.clearCache();
		criteriasMap = null;
	}


	@Override
	public void updateKaruServByListTemplate(TemplateListTemplate ltmpl) {
		if(ltmpl != null && ltmpl.getId() != null) {
			getCachableMap().values().forEach(ks->{
				if(ltmpl.getId().equals(ks.getListTemplateId())) {
					ks.setListTemplate(ltmpl);
				}
			});
		}
	}
	
	@Override
	public void updateKaruServByDetailTemplate(TemplateDetailTemplate dtmpl) {
		if(dtmpl != null && dtmpl.getId() != null) {
			getCachableMap().values().forEach(ks->{
				if(dtmpl.getId().equals(ks.getDetailTemplateId())) {
					ks.setDetailTemplate(dtmpl);
				}
			});
		}
	}


	static final Integer ONE = 1;
	
	@Override
	public void toggleDisabled(Long ksId, boolean disabled) {
		KaruiServ ks = get(ksId);
		if(ks != null) {
			if(ONE.equals(ks.getDisabled())^disabled) {
				synchronized(ks) {
					if(ONE.equals(ks.getDisabled())^disabled) {
						getDao().updateDisabled(ksId, disabled);
						ks.setDisabled(disabled?1:null);
					}
				}
			}
		}
	}

}
