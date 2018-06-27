package cn.sowell.dataserver.model.tmpl.strategy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.sowell.copframe.utils.CollectionUtils;

public abstract class SetUpdateStrategy<T> {

	/**
	 * 用于获得pojo的id的方法
	 * @param pojo
	 * @return
	 */
	protected abstract Long getPojoId(T pojo);

	/**
	 * 处理更新的对象。注意后续将被更新的对象是原始对象originPojo
	 * @param originPojo 从数据库拉取的原始数据对象
	 * @param pojo 更新后的数据源
	 * @return 如果返回false，那么将跳过当前节点的更新
	 */
	protected abstract boolean handlerUpdatePojo(T originPojo, T pojo);
	/**
	 * 处理创建的对象
	 * @param pojo
	 * @return 返回false时，跳过当前对象的创建
	 */
	protected abstract boolean handlerCreatePojo(T pojo);
	
	/**
	 * 调用dao更新对象的方法
	 * @param originPojo
	 */
	protected abstract void doDaoUpdate(T originPojo);
	/**
	 * 调用dao创建对象的方法
	 * @param pojo
	 */
	protected abstract void doDaoCreate(T pojo);
	/**
	 * 移除数据库中记录的方法
	 * @param originMap 
	 * @param toRemoveId
	 */
	protected abstract void doDaoDelete(Map<Long, T> originMap, Long toRemoveId);
	
	
	public void doUpdate(Set<T> originSet, Set<T> toUpdateSet){
		Map<Long, T> originMap = CollectionUtils.toMap(originSet, originItem->getPojoId(originItem));
		Set<Long> toRemoveIds = new HashSet<Long>(originMap.keySet());
		
		if(toUpdateSet != null){
			for (T pojo : toUpdateSet) {
				Long pojoId = getPojoId(pojo);
				if(pojoId != null){
					toRemoveIds.remove(pojoId);
					//修改
					T originPojo = originMap.get(pojoId);
					if(handlerUpdatePojo(originPojo, pojo)){
						doDaoUpdate(originPojo);
					}
				}else{
					if(handlerCreatePojo(pojo)){
						doDaoCreate(pojo);
					}
				}
			}
		}
		for (Long toRemoveId : toRemoveIds) {
			doDaoDelete(originMap, toRemoveId);
		}
	}
	
}
