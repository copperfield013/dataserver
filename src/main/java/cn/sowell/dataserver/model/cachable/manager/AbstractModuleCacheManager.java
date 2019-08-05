package cn.sowell.dataserver.model.cachable.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;

import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.dataserver.model.cachable.dao.CachableDao;
import cn.sowell.dataserver.model.cachable.prepare.ModuleTemplateReferData;
import cn.sowell.dataserver.model.cachable.prepare.PreparedToCache;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.dict.validator.ModuleTemplateReferDataGenerator;
import cn.sowell.dataserver.model.tmpl.manager.ModuleCachableManager;
import cn.sowell.dataserver.model.tmpl.pojo.Cachable;

public abstract class AbstractModuleCacheManager<T extends Cachable, D extends CachableDao<T>, GP extends PreparedToCache, P extends PreparedToCache> implements ModuleCachableManager<T>{
	
	private Map<Long, T> cacheMap;
	
	private final D dao;

	private final ModuleCachableMetaSupportor metaSupportor;
	
	protected AbstractModuleCacheManager(D dao, ModuleCachableMetaSupportor metaSupportor) {
		Assert.notNull(dao);
		Assert.notNull(metaSupportor);
		this.dao = dao;
		this.metaSupportor = metaSupportor;
	}
	
	protected Map<Long, T> getCachableMap() {
		synchronized (this) {
			if(cacheMap == null) {
				ModuleTemplateReferDataGenerator referDataGenerator = metaSupportor.getTemplateReferDataGenetator();
				cacheMap = new ConcurrentHashMap<Long, T>();
				List<T> latests = dao.queryAll();
				Map<String, List<T>> moduleLatestsMap = CollectionUtils.toListMap(latests, Cachable::getModule);
				Iterator<Entry<String, List<T>>> itr = moduleLatestsMap.entrySet().iterator();
				while(itr.hasNext()) {
					Entry<String, List<T>> entry = itr.next();
					if(!metaSupportor.checkModule(entry.getKey())) {
						itr.remove();
					}
				}
				GP globalPreparedToCache = getGlobalPreparedToCache();
				moduleLatestsMap.forEach((moduleName, moduleLatests)->{
					ModuleTemplateReferData moduleReferData = referDataGenerator.generate(moduleName);
					if(moduleReferData == null) return;
					if(globalPreparedToCache != null) {
						globalPreparedToCache.setReferData(moduleReferData);
					}
					moduleLatests.forEach(latest->{
						P prepareToCache = extractPrepare(globalPreparedToCache, latest);
						if(prepareToCache != null && prepareToCache.getReferData() == null) {
							prepareToCache.setReferData(globalPreparedToCache.getReferData());
						}
						handlerCache(latest, prepareToCache);
						cacheMap.put(latest.getId(), latest);
					});
				});
				cacheMap.values().forEach(cache->handlerCacheAfterAllLoaded(cache));
			}
			return cacheMap;
		}
	}
	


	/**
	 * 在所有的数据都加载结束之后进行处理
	 * @param cache
	 */
	protected void handlerCacheAfterAllLoaded(T cache) {
		
	}

	/**
	 * 
	 * @return
	 */
	protected abstract GP getGlobalPreparedToCache();
	
	protected abstract P extractPrepare(GP globalPreparedToCache, T cachable);
	
	protected abstract P getPreparedToCache(T cachable);
	
	/**
	 * 
	 * @param latest
	 * @param prepareToCache
	 */
	protected abstract void handlerCache(T latest, P prepareToCache);
	
	protected void afterReloadCache(T cachable) {}
	
	protected abstract T createCachablePojo();

	protected abstract Long doCreate(T cachable);

	protected abstract void doUpdate(T cachable);
	
	@Override
	public T get(long cachableId) {
		return getCachableMap().get(cachableId);
	}

	@Override
	public List<T> queryByModule(String moduleName) {
		return getCachableMap().values().stream().filter(cachable->moduleName.equals(cachable.getModule())).collect(Collectors.toList());
	}

	@Override
	public Long merge(T cachable) {
		Long cachableId;
		cachable.setUpdateTime(new Date());
		if(cachable.getId() != null) {
			doUpdate(cachable);
			cachableId = cachable.getId();
		}else {
			cachable.setCreateTime(cachable.getUpdateTime());
			cachableId = doCreate(cachable);
		}
		TransactionAspectSupport.currentTransactionStatus().flush();
		dao.getNormalOperateDao().clear();
		reloadCache(cachableId);
		return cachableId;
	}

	

	@Override
	public void remove(long cachableId) {
		T pojo = createCachablePojo();
		pojo.setId(cachableId);
		dao.getNormalOperateDao().remove(pojo);
		reloadCache(cachableId);
	}


	@Override
	public void reloadCache(long cachableId) {
		synchronized (this) {
			if(cacheMap == null) {
				cacheMap = new HashMap<>();
			}
		}
		synchronized (cacheMap) {
			T cachable = dao.get(cachableId);
			if(cachable != null && metaSupportor.checkModule(cachable.getModule())) {
				P prepared = getPreparedToCache(cachable);
				if(prepared != null && prepared.getReferData() == null) {
					prepared.setReferData(metaSupportor.getModuleTemplateReferData(cachable.getModule()));
				}
				handlerCache(cachable, prepared);
				cacheMap.put(cachableId, cachable);
				handlerCacheAfterAllLoaded(cachable);
				afterReloadCache(cachable);
			}else {
				cacheMap.remove(cachableId);
			}
		}
	}
	
	@Override
	public void reloadCache() {
		clearCache();
		getCachableMap();
	}

	@Override
	public void clearCache() {
		if(this.cacheMap != null) {
			synchronized (this.cacheMap) {
				this.cacheMap = null;
			}
		}
	}
	
	protected D getDao() {
		return dao;
	}
	
	protected ModuleCachableMetaSupportor getMetaSupportor() {
		return metaSupportor;
	}
}
