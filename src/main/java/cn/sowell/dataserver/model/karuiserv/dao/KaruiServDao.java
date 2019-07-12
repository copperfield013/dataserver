package cn.sowell.dataserver.model.karuiserv.dao;

import java.util.List;

import cn.sowell.dataserver.model.cachable.dao.CachableDao;
import cn.sowell.dataserver.model.karuiserv.pojo.KaruiServ;
import cn.sowell.dataserver.model.karuiserv.pojo.KaruiServCriteria;

public interface KaruiServDao extends CachableDao<KaruiServ>{

	List<KaruiServCriteria> queryAllCriterias();

	List<KaruiServCriteria> getCriteriasByKsId(Long ksId);

	void updateDisabled(Long ksId, boolean disabled);

}
