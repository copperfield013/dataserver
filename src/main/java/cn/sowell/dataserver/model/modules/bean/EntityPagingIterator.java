package cn.sowell.dataserver.model.modules.bean;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;


public class EntityPagingIterator implements Iterator<ModuleEntityPropertyParser>{

	//对象静态属性

	//所有数据的条数
	private final int totalCount;
	//当前迭代器可以查出的最多的数据条数
	private final int dataCount;
	//起始页码
	private final int startPageNo;
	//起始需要忽略的数据条数
	private final int ignoreDataCount;
	
	//对象可变属性
	private Iterator<ModuleEntityPropertyParser> cacheItr;
	private int current = 0;
	private int pageNoInc = 0;
	private int hasIgnored = 0;
	private EntityPagingQueryProxy queryProxy;
	private Predicate<Integer> hasNextPredicate;
	private UserIdentifier user;
	
	
	public EntityPagingIterator(
			int totalCount,
			int dataCount,
			int ignoreDataCount,
			int startPageNo, 
			UserIdentifier user,
			EntityPagingQueryProxy proxy) {
		super();
		this.totalCount = totalCount;
		this.dataCount = dataCount;
		this.ignoreDataCount = ignoreDataCount;
		this.startPageNo = startPageNo;
		this.queryProxy = proxy;
		this.user = user;
	}

	@Override
	public boolean hasNext() {
		if(hasNextPredicate != null) {
			return hasNextPredicate.test(current);
		}else {
			return current < dataCount;
		}
	}

	
	private long lastCacheDataTime = System.currentTimeMillis();
	private float speed = 0;
	@Override
	public synchronized ModuleEntityPropertyParser next() {
		if(hasNext() && (cacheItr == null || !cacheItr.hasNext())){
			Set<ModuleEntityPropertyParser> set = 
					queryProxy.load(startPageNo + (pageNoInc++), this.user);
			if(set != null){
				cacheItr = set.iterator();
				long currentTime = System.currentTimeMillis();
				speed = set.size() / ((float)(currentTime - lastCacheDataTime) / 1000);
				lastCacheDataTime = currentTime;;
			}
		}
		if(cacheItr != null && cacheItr.hasNext()){
			if(ignoreDataCount > hasIgnored){
				hasIgnored++;
				cacheItr.next();
				return this.next();
			}
			current++;
			return cacheItr.next();
		}else{
			return null;
		}
	}
	
	public int getTotalCount(){
		return totalCount;
	}
	
	public int getCurrent(){
		return current;
	}

	public int getDataCount() {
		return dataCount;
	}

	/**
	 * 调用next方法的速度，单位条/秒
	 * @return
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * 根据速度计算获得剩余的条数所需要的时间
	 * @return
	 */
	public float getRemainSecond() {
		if(speed > 0){
			return (dataCount - current) / speed;
		}
		return 0;
	}

	public void setHasNextPredicate(Predicate<Integer> hasNextPredicate) {
		this.hasNextPredicate = hasNextPredicate;
	}

}
