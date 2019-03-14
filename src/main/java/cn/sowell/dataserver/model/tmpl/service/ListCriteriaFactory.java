package cn.sowell.dataserver.model.tmpl.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.MutablePropertyValues;

import com.abc.panel.EntitySortedPagedQueryFactory;
import com.abc.rrc.query.criteria.EntityCriteriaFactory;
import com.abc.rrc.query.criteria.MultiAttrCriteriaFactory;

import cn.sowell.dataserver.model.abc.service.EntityQueryParameter;
import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupPremise;

public interface ListCriteriaFactory {

	Map<Long, NormalCriteria> getCriteriasFromRequest(MutablePropertyValues pvs,
			Map<Long, ? extends AbstractListCriteria> defaultCriteriaMap);

	void appendCriterias(Collection<NormalCriteria> nCriterias, String moduleName,
			EntityCriteriaFactory criteriaFactory);

	Map<Long, String> exractTemplateCriteriaMap(HttpServletRequest request);

	void appendPremiseCriteria(String moduleName, List<TemplateGroupPremise> premises, Set<NormalCriteria> criteria);

	<CRI extends AbstractListCriteria> void coverCriteriaForUpdate(CRI originCriteria, CRI criteria);

	void appendArrayItemCriteriaParameter(EntitySortedPagedQueryFactory sortedPagedQueryFactory,
			EntityQueryParameter queryParam);

	void appendCriterias(List<NormalCriteria> criterias, String moduleName,
			MultiAttrCriteriaFactory multiCriteriaFactory);

	

	

}
