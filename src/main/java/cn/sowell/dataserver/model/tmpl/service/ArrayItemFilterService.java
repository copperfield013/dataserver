package cn.sowell.dataserver.model.tmpl.service;

import java.util.Collection;
import java.util.Map;

import com.abc.rrc.query.queryrecord.criteria.Criteria;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailArrayItemFilter;

public interface ArrayItemFilterService extends OpenTemplateService<TemplateDetailArrayItemFilter>{

	Map<String, Collection<Criteria>> getArrayItemFilterCriteriasMap(Long dtmplId, UserIdentifier user);

}
