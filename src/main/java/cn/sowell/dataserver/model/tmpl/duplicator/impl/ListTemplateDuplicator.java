package cn.sowell.dataserver.model.tmpl.duplicator.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.tmpl.duplicator.ModuleTemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.manager.ListTemplateManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

@Component
public class ListTemplateDuplicator implements ModuleTemplateDuplicator{

	@Resource
	ListTemplateManager ltmplManager;
	
	@Resource
	ModulesService mService;
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	@Resource
	DictionaryService dictService;
	
	@Resource
	ModuleCachableMetaSupportor metaSupportor;
	
	static Logger logger = Logger.getLogger(ListTemplateDuplicator.class);
	
	
	@Override
	public Long copy(Long tmplId, String targetReference) {
		return copyListTemplate(tmplId, targetReference);
	}
	
	/**
	 * 复制一个列表模板到某个模块当中
	 * @param ltmplId
	 * @param targerModuleName
	 * @return
	 */
	public Long copyListTemplate(Long ltmplId, String targetModuleName) {
		Assert.notNull(ltmplId);
		Assert.hasText(targetModuleName);
		TemplateListTemplate ltmpl = ltmplManager.get(ltmplId);
		ModuleMeta sourceModule = mService.getModule(ltmpl.getModule());
		if(ltmpl != null) {
			FusionContextConfig config = fFactory.getModuleConfig(targetModuleName);
			if(config != null) {
				//复制一个列表模板对象
				TemplateListTemplate newTmpl = new TemplateListTemplate();
				newTmpl.setTitle("(复制自" + sourceModule.getTitle() + "-" + ltmpl.getTitle() + ")");
				newTmpl.setModule(targetModuleName);
				newTmpl.setCreateTime(new Date());
				newTmpl.setUpdateTime(newTmpl.getCreateTime());
				newTmpl.setDefaultPageSize(ltmpl.getDefaultPageSize());
				newTmpl.setUnmodifiable(ltmpl.getUnmodifiable());
				if(ltmpl.getDefaultOrderFieldId() != null) {
					DictionaryField targetOrderField = dictService.mapModuleField(targetModuleName, dictService.getField(ltmpl.getModule(), ltmpl.getDefaultOrderFieldId()));
					if(targetOrderField != null) {
						newTmpl.setDefaultOrderFieldId(targetOrderField.getId());
						newTmpl.setDefaultOrderDirection(ltmpl.getDefaultOrderDirection());
					}
				}
				copyListTemplateCriterias(ltmpl, newTmpl);
				copyListTemplateColumns(ltmpl, newTmpl);
				Long newTmplId = ltmplManager.merge(newTmpl);
				return newTmplId;
			}
		}
		return null;
	}
	
	private void copyListTemplateColumns(TemplateListTemplate ltmpl, TemplateListTemplate newTmpl) {
		String targetModuleName = newTmpl.getModule();
		List<TemplateListColumn> columns = ltmpl.getColumns();
		if(columns != null) {
			for (TemplateListColumn column : columns) {
				TemplateListColumn nColumn = new TemplateListColumn();
				if(nColumn.getFieldAvailable()) {
					if(column.getSpecialField() == null) {
						if(column.getFieldId() != null) {
							DictionaryField field = dictService.mapModuleField(targetModuleName, dictService.getField(ltmpl.getModule(), column.getFieldId()));
							if(field != null) {
								nColumn.setFieldId(field.getId());
							}else {
								logger.error("展示字段[" + column.getTitle() + "]的fieldId属性[" 
											+ column.getFieldId() + "]无法在模块[" + targetModuleName + "]中找到对应的字段");
								continue;
							}
						}else {
							logger.error("展示字段[" + column.getTitle() + "]的specialField属性和fieldId属性不能均为空");
							continue;
						}
					}else {
						nColumn.setSpecialField(column.getSpecialField());
					}
					nColumn.setTitle(column.getTitle());
					nColumn.setOrder(column.getOrder());
					nColumn.setOrderable(column.getOrderable());
					nColumn.setViewOption(column.getViewOption());
					nColumn.setCreateTime(newTmpl.getCreateTime());
					nColumn.setUpdateTime(newTmpl.getUpdateTime());
					newTmpl.getColumns().add(nColumn);
				}
			}
		}
		
	}

	private void copyListTemplateCriterias(TemplateListTemplate ltmpl, TemplateListTemplate newTmpl) {
		String targetModuleName = newTmpl.getModule();
		List<TemplateListCriteria> criterias = ltmpl.getCriterias();
		if(criterias != null) {
			Map<String, Set<String>> fieldInputTypeMap = dictService.getFieldInputTypeMap();
			for (TemplateListCriteria criteria : criterias) {
				TemplateListCriteria nCriteria = new TemplateListCriteria();
				if(criteria.getComposite() == null) {
					//字段条件
					if(criteria.getFieldAvailable()) {
						DictionaryField field = dictService.mapModuleField(targetModuleName, dictService.getField(ltmpl.getModule(), criteria.getFieldId()));
						if(field != null) {
							if(criteria.getRelationLabel() != null) {
								DictionaryComposite composite = field.getComposite();
								if(composite.getRelationSubdomain() == null
										|| !composite.getRelationSubdomain().contains(criteria.getRelation())) {
									logger.error("关系字段条件的label[" + criteria.getRelationLabel() 
									+ "]在目标模块[" + targetModuleName + "]对应的composite[" 
									+ composite.getRelationSubdomain() + "]中不存在");
									continue;
								}
								nCriteria.setRelationLabel(criteria.getRelationLabel());
							}
							if(metaSupportor.supportFieldInputType(criteria.getInputType(), field.getType(), fieldInputTypeMap)) {
								nCriteria.setInputType(criteria.getInputType());
							}else {
								nCriteria.setInputType("text");
								logger.error("条件字段[" + criteria.getTitle() + "]匹配到模块[" 
										+ targetModuleName + "]的字段[" + field.getFullKey() + "]的类型[" + field.getType() 
										+ "]不支持原表单类型[" + criteria.getInputType() + "]，将替换为文本类型");
							}
							nCriteria.setFieldId(field.getId());
						}else {
							logger.error("找不到条件字段[" 
									+ criteria.getTitle() + "]在目标模块[" + targetModuleName + "]中匹配的字段");
							continue;
						}
					}
				}else {
					//composite条件
					DictionaryComposite targetComposite = dictService.mapModuleComposite(targetModuleName, criteria.getComposite());
					if(targetComposite != null) {
						nCriteria.setCompositeId(targetComposite.getId());
						nCriteria.setComposite(targetComposite);
						nCriteria.setInputType(criteria.getInputType());
					}else {
						logger.error("找不到条件关系[" +  criteria.getComposite().getTitle() + "]在目标模块[" + targetModuleName + "]中匹配的的关系");
						continue;
					}
				}
				nCriteria.setTitle(criteria.getTitle());
				nCriteria.setRelation(criteria.getRelation());
				nCriteria.setQueryShow(criteria.getQueryShow());
				nCriteria.setComparator(criteria.getComparator());
				nCriteria.setOrder(criteria.getOrder());
				nCriteria.setViewOption(criteria.getViewOption());
				nCriteria.setDefaultValue(criteria.getDefaultValue());
				nCriteria.setPlaceholder(criteria.getPlaceholder());
				nCriteria.setCreateTime(newTmpl.getCreateTime());
				
				newTmpl.getCriterias().add(nCriteria);
			}
		}
	}
}
