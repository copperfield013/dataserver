package cn.sowell.dataserver.model.karuiserv.manager.impl;

import org.springframework.stereotype.Repository;

import cn.sowell.dataserver.model.cachable.manager.AbstractModuleCacheManager;
import cn.sowell.dataserver.model.cachable.prepare.PreparedToCache;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.karuiserv.dao.KaruiServJsonMetaDao;
import cn.sowell.dataserver.model.karuiserv.manager.KaruiServJsonMetaManager;
import cn.sowell.dataserver.model.karuiserv.pojo.KaruiServJsonMeta;

@Repository
public class KaruiServJsonMetaManagerImpl 
	extends AbstractModuleCacheManager<KaruiServJsonMeta, KaruiServJsonMetaDao, PreparedToCache, PreparedToCache>
	implements KaruiServJsonMetaManager {

	protected KaruiServJsonMetaManagerImpl(KaruiServJsonMetaDao dao, ModuleCachableMetaSupportor metaSupportor) {
		super(dao, metaSupportor);
	}

	@Override
	protected PreparedToCache getGlobalPreparedToCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PreparedToCache extractPrepare(PreparedToCache globalPreparedToCache, KaruiServJsonMeta cachable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PreparedToCache getPreparedToCache(KaruiServJsonMeta cachable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void handlerCache(KaruiServJsonMeta latest, PreparedToCache prepareToCache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected KaruiServJsonMeta createCachablePojo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Long doCreate(KaruiServJsonMeta cachable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doUpdate(KaruiServJsonMeta cachable) {
		// TODO Auto-generated method stub
		
	}

}
