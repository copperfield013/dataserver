package cn.sowell.dataserver.model.modules.bean;

import java.util.List;
import java.util.Set;

import com.abc.mapping.entity.Entity;
import com.abc.rrc.query.entity.EntitySortedPagedQuery;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.utils.Assert;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;

public class EntityQueryAdapter implements EntityPagingQueryProxy{

	private EntitySortedPagedQuery sortedPagedQuery;
	private int pageSize;
	private FusionContextConfigResolver resolver;
	
	
	
	public EntityQueryAdapter(EntitySortedPagedQuery sortedPagedQuery, 
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
		List<Entity> entities = sortedPagedQuery.visit(pageNo);
		return CollectionUtils.toSet(entities, entity->resolver.createParser(entity, user, null));
	}

}
