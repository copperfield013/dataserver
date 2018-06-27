package cn.sowell.dataserver.model.tmpl.strategy;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import cn.sowell.copframe.dao.utils.NormalOperateDao;

public abstract class NormalDaoSetUpdateStrategy<T> extends SetUpdateStrategy<T>{
	
	NormalOperateDao nDao;
	
	public NormalDaoSetUpdateStrategy(NormalOperateDao nDao){
		this.nDao = nDao;
	}
	
	@Override
	protected void doDaoUpdate(T originPojo) {
		nDao.update(originPojo);
	}
	
	@Override
	protected void doDaoCreate(T pojo) {
		nDao.save(pojo);
	}
	
	@Override
	protected void doDaoDelete(Map<Long, T> originMap, Long toRemoveId) {
		nDao.remove(originMap.get(toRemoveId));
	}

	public static <T1> SetUpdateStrategy<T1> build(
			Class<T1> clazz,
			NormalOperateDao nDao,
			Function<T1, Long> idGetter,
			BiConsumer<T1, T1> updateHandler,
			Consumer<T1> createHandler){
		return new NormalDaoSetUpdateStrategy<T1>(nDao) {
			
			@Override
			protected boolean handlerUpdatePojo(T1 originPojo, T1 pojo) {
				updateHandler.accept(originPojo, pojo);
				return true;
			}
			
			@Override
			protected boolean handlerCreatePojo(T1 pojo) {
				createHandler.accept(pojo);
				return true;
			}
			
			@Override
			protected Long getPojoId(T1 pojo) {
				return idGetter.apply(pojo);
			}

		};
	}
	
	
}
