package cn.sowell.dataserver.model.tmpl.service;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.MutablePropertyValues;

import com.abc.application.BizFusionContext;
import com.abc.rrc.query.criteria.EntityCriteriaFactory;

import cn.sowell.dataserver.model.modules.pojo.criteria.NormalCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListCriteria;

public interface ListCriteriaFactory {

	Map<Long, NormalCriteria> getCriteriasFromRequest(MutablePropertyValues pvs,
			Map<Long, ? extends AbstractListCriteria> defaultCriteriaMap);

	EntityCriteriaFactory appendCriterias(Collection<NormalCriteria> nCriterias, String moduleName,
			BizFusionContext context);

	Map<Long, String> exractTemplateCriteriaMap(HttpServletRequest request);

	

	

}
