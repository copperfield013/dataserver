package cn.sowell.dataserver.model.cachable.dao;

import java.util.List;

import cn.sowell.copframe.dao.utils.NormalOperateDao;

public interface CachableDao<T>{
	 List<T> queryAll();
	 T get(long cachableId);
	 NormalOperateDao getNormalOperateDao();
}
