package cn.sowell.dataserver.model.tmpl.duplicator.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.sowell.datacenter.entityResolver.Composite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.dict.validator.ModuleCachableMetaSupportor;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.tmpl.duplicator.TemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.manager.SelectionTemplateManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionColumn;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;

@Component
public class SelectionTemplateDuplicator implements TemplateDuplicator<DictionaryComposite>{

	@Resource
	SelectionTemplateManager stmplManager;

	@Resource
	ModulesService mService;

	@Resource
	DictionaryService dictService;
	
	@Resource
	ModuleCachableMetaSupportor metaSupportor;
	
	static Logger logger = Logger.getLogger(SelectionTemplateDuplicator.class);
	
	@Override
	public Long copy(Long stmplId, DictionaryComposite targetComposite) {
		return copySeletionTemplate(targetComposite, stmplId);
	}

	private Long copySeletionTemplate(DictionaryComposite targetGroupComposite,
			Long selectionTemplateId) {
		if(Composite.RELATION_ADD_TYPE.equals(targetGroupComposite.getAddType())) {
			TemplateSelectionTemplate stmpl = stmplManager.get(selectionTemplateId);
			if(stmpl != null && stmpl.getRelationName() != null) {
				String targetModuleName = stmpl.getModule();
				ModuleMeta sourceModule = mService.getModule(targetModuleName);
				if(sourceModule != null) {
					//复制一个选择模板对象
					TemplateSelectionTemplate newTmpl = new TemplateSelectionTemplate();
					newTmpl.setCompositeId(targetGroupComposite.getId());
					newTmpl.setRelationName(targetGroupComposite.getName());
					newTmpl.setTitle("(复制自" + sourceModule.getTitle() + "-" + stmpl.getTitle() + ")");
					newTmpl.setModule(targetModuleName);
					newTmpl.setCreateTime(new Date());
					newTmpl.setUpdateTime(newTmpl.getCreateTime());
					newTmpl.setDefaultPageSize(stmpl.getDefaultPageSize());
					newTmpl.setMultiple(stmpl.getMultiple());
					newTmpl.setNonunique(stmpl.getNonunique());
					if(stmpl.getDefaultOrderFieldId() != null) {
						DictionaryField targetOrderField = dictService.mapModuleField(targetModuleName, dictService.getField(stmpl.getModule(), stmpl.getDefaultOrderFieldId()));
						if(targetOrderField != null) {
							newTmpl.setDefaultOrderFieldId(targetOrderField.getId());
							newTmpl.setDefaultOrderDirection(stmpl.getDefaultOrderDirection());
						}
					}
					copySelectionTemplateCriterias(stmpl, newTmpl);
					copySelectionTemplateColumns(stmpl, newTmpl);
					Long newTmplId = stmplManager.merge(newTmpl);
					return newTmplId;
				}else {
					logger.error("没有找到目标模块[" + targetModuleName + "]");
				}
			}else {
				logger.error("找不到原选择模板，或者原选择模板的RelationName为空");
			}
		}
		return null;
	}

	private void copySelectionTemplateColumns(TemplateSelectionTemplate stmpl, TemplateSelectionTemplate newTmpl) {
		String targetModuleName = newTmpl.getModule();
		List<TemplateSelectionColumn> columns = stmpl.getColumns();
		if(columns != null) {
			for (TemplateSelectionColumn column : columns) {
				TemplateSelectionColumn nColumn = new TemplateSelectionColumn();
				if(nColumn.getFieldAvailable()) {
					if(column.getSpecialField() == null) {
						if(column.getFieldId() != null) {
							DictionaryField field = dictService.mapModuleField(targetModuleName, dictService.getField(stmpl.getModule(), column.getFieldId()));
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

	private void copySelectionTemplateCriterias(TemplateSelectionTemplate stmpl, TemplateSelectionTemplate newTmpl) {
		String targetModuleName = newTmpl.getModule();
		List<TemplateSelectionCriteria> criterias = stmpl.getCriterias();
		if(criterias != null) {
			Map<String, Set<String>> fieldInputTypeMap = dictService.getFieldInputTypeMap();
			for (TemplateSelectionCriteria criteria : criterias) {
				TemplateSelectionCriteria nCriteria = new TemplateSelectionCriteria();
				//字段条件
				if(criteria.getFieldAvailable()) {
					DictionaryField field = dictService.mapModuleField(targetModuleName, dictService.getField(stmpl.getModule(), criteria.getFieldId()));
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
