package cn.sowell.dataserver.model.tmpl.manager.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.Constants;
import cn.sowell.dataserver.model.cachable.manager.AbstractModuleCacheManager;
import cn.sowell.dataserver.model.cachable.prepare.PreparedToCache;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.tmpl.dao.TemplateDictionaryFilterDao;
import cn.sowell.dataserver.model.tmpl.manager.DetailTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.DictionaryFilterManager;
import cn.sowell.dataserver.model.tmpl.manager.ListTemplateManager;
import cn.sowell.dataserver.model.tmpl.manager.TemplateGroupManager;
import cn.sowell.dataserver.model.tmpl.pojo.FilteredDictionary;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDictionaryFilter;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupDictionaryFilter;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

@Component
public class DictionaryFilterManagerImpl
		extends AbstractModuleCacheManager<TemplateDictionaryFilter, TemplateDictionaryFilterDao, PreparedToCache, PreparedToCache> 
		implements DictionaryFilterManager{

	@Autowired 
	protected DictionaryFilterManagerImpl(@Autowired TemplateDictionaryFilterDao dao, @Autowired ModuleCachableMetaSupportor metaSupportor) {
		super(dao, metaSupportor);
	}

	@Resource
	TemplateGroupManager tmplGroupManager;
	
	@Resource
	DictionaryService dictService;
	
	@Resource
	ListTemplateManager ltmplManager;
	
	@Resource
	DetailTemplateManager dtmplManager;
	

	@Override
	public FilteredDictionary prefilter(long filterId) {
		TemplateDictionaryFilter filter = get(filterId);
		if(filter != null) {
		}
		return null;
	}
	

	@Override
	public FilteredDictionary filter(long tmplFilterId) {
		TemplateGroupDictionaryFilter tmplFilter = tmplGroupManager.getDictionaryFilter(tmplFilterId);
		if(tmplFilter != null) {
			TemplateGroup tmplGroup = tmplGroupManager.get(tmplFilter.getTmplGroupId());
			FilteredDictionary dict = prefilter(tmplFilter.getFilterId());
			if(isTrue(tmplFilter.getWithListTemplate())) {
				TemplateListTemplate ltmpl = ltmplManager.get(tmplGroup.getListTemplateId());
				coverFieldTitle(dict, ltmpl);
			}
			if(isTrue(tmplFilter.getWithDetailTemplate())) {
				TemplateDetailTemplate dtmpl = dtmplManager.get(tmplGroup.getDetailTemplateId());
				coverFieldTitle(dict, dtmpl);
			}
			return dict;
		}
		return null;
	}

	private void coverFieldTitle(FilteredDictionary dict, TemplateListTemplate ltmpl) {
		List<TemplateListColumn> columns = ltmpl.getColumns();
		if(columns != null) {
			Map<DictionaryField, String> fieldTitleMap = new HashMap<>();
			columns.forEach(column->{
				if(column.getFieldAvailable() && column.getFieldId() != null && TextUtils.hasText(column.getTitle())) {
					DictionaryField field = dictService.getField(ltmpl.getModule(), column.getFieldId());
					if(!fieldTitleMap.containsKey(field)) {
						fieldTitleMap.put(field, column.getTitle());
					}
				}
			});
			dict.getFieldTitleCoverMap().putAll(fieldTitleMap);
		}
		
	}

	private void coverFieldTitle(FilteredDictionary dict, TemplateDetailTemplate dtmpl) {
		List<TemplateDetailFieldGroup> fieldGroups = dtmpl.getGroups();
		Map<DictionaryComposite, String> compositeTitleMap = new HashMap<>();
		Map<DictionaryField, String> fieldTitleMap = new HashMap<>();
		fieldGroups.forEach(group->{
			DictionaryComposite composite = group.getComposite();
			if(composite != null && !compositeTitleMap.containsKey(group.getComposite()) && TextUtils.hasText(group.getTitle())) {
				compositeTitleMap.put(group.getComposite(), group.getTitle());
			}
			
			List<TemplateDetailField> fields = group.getFields();
			if(fields != null) {
				fields.forEach(dtmplField->{
					if(dtmplField.getFieldAvailable() && dtmplField.getFieldId() != null && TextUtils.hasText(dtmplField.getTitle())) {
						DictionaryField field = dictService.getField(dtmpl.getModule(), dtmplField.getFieldId());
						if(!fieldTitleMap.containsKey(field)) {
							fieldTitleMap.put(field, dtmplField.getTitle());
						}
					}
				});
			}
		});
		dict.getCompositeTitleCover().putAll(compositeTitleMap);
		dict.getFieldTitleCoverMap().putAll(fieldTitleMap);
	}

	private boolean isTrue(Integer value) {
		return Constants.TRUE.equals(value);
	}


	@Override
	protected PreparedToCache getGlobalPreparedToCache() {
		return null;
	}


	@Override
	protected PreparedToCache extractPrepare(PreparedToCache globalPreparedToCache, TemplateDictionaryFilter cachable) {
		return null;
	}

	
	@Override
	protected PreparedToCache getPreparedToCache(TemplateDictionaryFilter filter) {
		return null;
	}

	@Override
	protected void handlerCache(TemplateDictionaryFilter latest, PreparedToCache prepareToCache) {
	}


	@Override
	protected TemplateDictionaryFilter createCachablePojo() {
		return new TemplateDictionaryFilter();
	}


	@Override
	protected Long doCreate(TemplateDictionaryFilter filter) {
		Date now = new Date();
		filter.setCreateTime(now);
		Long filterId = getDao().getNormalOperateDao().save(filter);
		return filterId;
	}


	@Override
	protected void doUpdate(TemplateDictionaryFilter filter) {
		Date now = new Date();
		TemplateDictionaryFilter originFilter = get(filter.getId());
		originFilter.setUpdateTime(now);
		originFilter.setTitle(filter.getTitle());
		getDao().getNormalOperateDao().update(filter);
	}

	@Override
	public Map<Long, List<TemplateGroup>> queryRelatedGroupsMap(List<TemplateDictionaryFilter> filters) {
		
		return null;
	}
	
}
