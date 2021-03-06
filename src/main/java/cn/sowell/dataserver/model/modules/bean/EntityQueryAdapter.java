package cn.sowell.dataserver.model.modules.bean;

import java.util.List;
import java.util.Set;

import cho.carbon.entity.entity.Entity;
import cho.carbon.query.entity.SortedPagedQuery;
import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.utils.Assert;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;

public class EntityQueryAdapter implements EntityPagingQueryProxy{

	private SortedPagedQuery<Entity> sortedPagedQuery;
	private int pageSize;
	private FusionContextConfigResolver resolver;
	
	
	
	public EntityQueryAdapter(SortedPagedQuery<Entity> sortedPagedQuery, 
			FusionContextConfigResolver fusionContextConfigResolver, Integer pageSize) {
		Assert.notNull(fusionContextConfigResolver);
		sortedPagedQuery.setPageSize(pageSize);
		this.sortedPagedQuery = sortedPagedQuery;
		this.pageSize = pageSize;
		this.resolver = fusionContextConfigResolver;
	}

	@Override
	public int getTotalCount() {
		return sortedPagedQuery.getAllCount();
	}

	@Override
	public int getPageSize() {
		return pageSize;
	}

	@Override
	public Set<ModuleEntityPropertyParser> load(int pageNo, UserIdentifier user) {
		List<Entity> entities = sortedPagedQuery.visitEntity(pageNo);
		return CollectionUtils.toSet(entities, entity->resolver.createParser(entity, user, null));
	}

}
