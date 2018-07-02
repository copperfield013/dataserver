package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.FieldConfigure;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.RelationFieldConfigure;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.tmpl.DataServerConstants;
import cn.sowell.dataserver.model.tmpl.dao.DetailTemplateDao;
import cn.sowell.dataserver.model.tmpl.dao.ListTemplateDao;
import cn.sowell.dataserver.model.tmpl.dao.TempalteGroupDao;
import cn.sowell.dataserver.model.tmpl.dao.TemplateDao;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;
import cn.sowell.dataserver.model.tmpl.service.AdminIdGetter;
import cn.sowell.dataserver.model.tmpl.service.TemplateService;
import cn.sowell.dataserver.model.tmpl.strategy.TemplateUpdateStrategy;
import cn.sowell.dataserver.model.tmpl.strategy.TemplateUpdateStrategyFactory;

@Service
public class TemplateServiceImpl implements TemplateService{

	@Resource
	NormalOperateDao nDao;
	
	@Resource
	ListTemplateDao lDao;
	
	@Resource
	DetailTemplateDao dDao;
	
	@Resource
	TemplateDao tDao;
	
	@Resource
	TempalteGroupDao gDao;

	@Resource
	AdminIdGetter adminIdGetter;
	
	@Resource
	TemplateUpdateStrategyFactory tmplUpdateStrategyFactory;
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	@Override
	public List<TemplateListTemplate> queryLtmplList(String module, UserIdentifier user) {
		return lDao.queryLtmplList(module, user.getId(), null);
	}
	
	@Override
	public AbstractTemplate getTemplate(long tmplId, String tmplType) {
		if(DataServerConstants.TEMPLATE_TYPE_DETAIL.equals(tmplType)) {
			return getDetailTemplate(tmplId);
		}else if(DataServerConstants.TEMPLATE_TYPE_LIST.equals(tmplType)){
			return getListTemplate(tmplId);
		}
		return null;
	}
	
	@Override
	public TemplateDetailTemplate getDetailTemplate(long tmplId) {
		TemplateDetailTemplate data = nDao.get(TemplateDetailTemplate.class, tmplId);
		if(data != null){
			List<TemplateDetailFieldGroup> groups = getTemplateGroups(fFactory.getModuleResolver(data.getModule()), data.getId());
			if(groups != null){
				data.setGroups(groups);
			}
		}
		return data;
	}

	@Override
	public TemplateListTemplate getListTemplate(long tmplId) {
		TemplateListTemplate tmpl = nDao.get(TemplateListTemplate.class, tmplId);
		if(tmpl != null){
			List<TemplateListColumn> columns = lDao.getColumnsByTmplId(tmpl.getId());
			tmpl.setColumns(columns);
			Set<TemplateListCriteria> criterias = lDao.getCriteriaByTmplId(tmpl.getId());
			tmpl.setCriterias(criterias);
		}
		return tmpl;
	}


	@Override
	public void removeTemplate(UserIdentifier user, Long tmplId, String tmplType) {
		AbstractTemplate template = getTemplate(tmplId, tmplType);
		if(template != null) {
			nDao.remove(template);
		}
	}

	@Override
	@Transactional
	public <T extends AbstractTemplate> Long mergeTemplate(T template) {
		TemplateUpdateStrategy<T> strategy = tmplUpdateStrategyFactory.getStrategy(template);
		if(template.getId() != null) {
			strategy.update(template);
			return template.getId();
		}else {
			return strategy.create(template);
		}
	}
	

	
	@Override
	public List<TemplateDetailTemplate> getAllDetailTemplateList(String module, UserIdentifier user, PageInfo pageInfo,
			boolean loadDetail) {
		List<TemplateDetailTemplate> list = dDao.getTemplateList(module, user, pageInfo);
		if(loadDetail){
			FusionContextConfigResolver resolver = fFactory.getModuleResolver(module);
			list.forEach(data->{
				data.setGroups(getTemplateGroups(resolver, data.getId()));
			});
		}
		return list;
	}
	
	
	
	
	private List<TemplateDetailFieldGroup> getTemplateGroups(FusionContextConfigResolver resolver, Long tmplId) {
		List<TemplateDetailFieldGroup> groups = dDao.getTemplateGroups(tmplId);
		Map<Long, List<TemplateDetailField>> fieldMap = dDao.getTemplateFieldsMap(CollectionUtils.toSet(groups, group->group.getId()));
		groups.forEach(group->{
			List<TemplateDetailField> fields = fieldMap.get(group.getId());
			if(fields != null){
				group.setFields(fields);
			}
			if(Integer.valueOf(1).equals(group.getIsArray())) {
				DictionaryComposite composite = group.getComposite();
				if(composite != null && TextUtils.hasText(composite.getName()) && composite.getRelationSubdomain() == null) {
					FieldConfigure conf = resolver.getFieldConfigure(composite.getName());
					if(conf instanceof RelationFieldConfigure) {
						composite.setRelationSubdomain(((RelationFieldConfigure) conf).getLabelDomain());
					}
				}
				
			}
		});
		return groups;
	}
	
	@Override
	public List<TemplateGroup> queryTemplateGroups(String module) {
		return gDao.queryGroups(module);
	}
	
	@Override
	public void saveGroup(TemplateGroup group, UserIdentifier user) {
		group.setUpdateTime(new Date());
		if(!TextUtils.hasText(group.getKey())) {
			group.setKey(TextUtils.uuid(5, 62));
		}
		if(group.getId() != null) {
			nDao.update(group);
		}else {
			group.setCreateUserId((Long) user.getId());
			group.setCreateTime(new Date());
			nDao.save(group);
		}
	}
	
	@Override
	public TemplateGroup getTemplateGroup(Long groupId) {
		return gDao.getGroup(groupId);
	}
	
	@Override
	public void remveTemplateGroup(Long groupId) {
		TemplateGroup group = new TemplateGroup();
		group.setId(groupId);
		nDao.remove(group);
	}
	
	@Override
	public TemplateGroup getTemplateGroup(String module, String templateGroupKey) {
		return gDao.getTemplateGroup(module, templateGroupKey);
	}
	
	@Override
	public Map<String, List<TemplateGroup>> queryTemplateGroups(Set<String> moduleNames) {
		List<TemplateGroup> groups = gDao.getTemplateGroups(moduleNames);
		return CollectionUtils.toListMap(groups, group->group.getModule());
	}
	
	@Override
	public TemplateDetailTemplate getDetailTemplateByGroupId(Long templateGroupId) {
		TemplateDetailTemplate dtmpl = dDao.getDetailTemplateByGroupId(templateGroupId);
		if(dtmpl != null){
			List<TemplateDetailFieldGroup> groups = getTemplateGroups(fFactory.getModuleResolver(dtmpl.getModule()), dtmpl.getId());
			if(groups != null){
				dtmpl.setGroups(groups);
			}
		}
		return dtmpl;
	}
	
}
