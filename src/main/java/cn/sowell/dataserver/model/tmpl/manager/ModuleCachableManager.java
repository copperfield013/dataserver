package cn.sowell.dataserver.model.tmpl.manager;

import java.util.List;

import cn.sowell.dataserver.model.tmpl.pojo.Cachable;

public interface ModuleCachableManager<T extends Cachable> {
	T get(long cachableId);
	List<T> queryByModule(String moduleName);
	/**
	 * 
	 * @param cachable
	 * @return
	 */
	Long merge(T cachable);
	void remove(long cachableId);
	void reloadCache(long cachableId);
	void clearCache();
	void reloadCache();
}
