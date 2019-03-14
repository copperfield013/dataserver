package cn.sowell.dataserver.model.tmpl.service;

import java.util.List;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.dataserver.model.abc.service.AbstractEntityQueryParameter.ArrayItemCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailArrayItemFilter;

public interface ArrayItemFilterService extends OpenTemplateService<TemplateDetailArrayItemFilter>{

	List<ArrayItemCriteria> getArrayItemFilterCriterias(Long dtmplId, UserIdentifier user);

}
