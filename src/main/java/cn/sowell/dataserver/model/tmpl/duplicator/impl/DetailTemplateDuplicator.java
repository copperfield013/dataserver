package cn.sowell.dataserver.model.tmpl.duplicator.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.dataserver.model.dict.pojo.DictionaryComposite;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.tmpl.duplicator.ModuleTemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.manager.DetailTemplateManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;

@Component
public class DetailTemplateDuplicator extends AbstractTemplateDulicator implements ModuleTemplateDuplicator{

	@Resource
	ModulesService mService;
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	@Resource
	DetailTemplateManager dtmplManager;
	
	@Resource
	SelectionTemplateDuplicator stmplDupliactor;
	
	static Logger logger = Logger.getLogger(DetailTemplateDuplicator.class);
	
	@Override
	public Long copy(Long dtmplId, String targetModuleName) {
		Assert.notNull(dtmplId);
		Assert.hasText(targetModuleName);
		TemplateDetailTemplate dtmpl = dtmplManager.get(dtmplId);
		ModuleMeta sourceModule = mService.getModule(dtmpl.getModule());
		if(dtmpl != null) {
			FusionContextConfig config = fFactory.getModuleConfig(targetModuleName);
			if(config != null) {
				TemplateDetailTemplate newTmpl = new TemplateDetailTemplate();
				//复制
				newTmpl.setModule(targetModuleName);
				newTmpl.setTitle("(复制自" + sourceModule.getTitle() + "-" + dtmpl.getTitle() + ")");
				newTmpl.setCreateTime(new Date());
				newTmpl.setUpdateTime(newTmpl.getCreateTime());
				List<TemplateDetailFieldGroup> groups = dtmpl.getGroups();
				if(groups != null) {
					//遍历所有字段组
					for (TemplateDetailFieldGroup group : groups) {
						TemplateDetailFieldGroup nGroup = new TemplateDetailFieldGroup();
						if(group.getFields() != null) {
							//遍历字段组内的所有字段
							for (TemplateDetailField field : group.getFields()) {
								//只有字段是可用的情况下才复制该字段
								if(field.getFieldAvailable()) {
									//映射获得目标模块对应的字段
									DictionaryField targetField = dictService.mapModuleField(targetModuleName, dictService.getField(dtmpl.getModule(), field.getFieldId()));
									if(targetField != null) {
										TemplateDetailField nField = new TemplateDetailField();
										nField.setFieldId(targetField.getId());
										nField.setFieldName(targetField.getFullKey());
										nField.setTitle(field.getTitle());
										nField.setOrder(field.getOrder());
										nField.setColNum(field.getColNum());
										nField.setUnmodifiable(field.getUnmodifiable());
										nField.setViewValue(field.getViewValue());
										nField.setType(field.getType());
										nField.setValidators(field.getValidators());
										nField.setOptionGroupId(field.getOptionGroupId());
										nGroup.getFields().add(nField);
									}
								}
							}
						}
						if(group.getComposite() != null) {
							try {
								//如果字段组的composite不为空的话，那么就要根据新模板中已经匹配好的fields推断出其对应的composite
								DictionaryComposite targetGroupComposite = analyzeModuleMapComposite(targetModuleName, nGroup.getFields(), group);
								if(targetGroupComposite != null) {
									nGroup.setIsArray(targetGroupComposite.getIsArray());
									nGroup.setCompositeId(targetGroupComposite.getId());
								}
								if(group.getSelectionTemplateId() != null) {
									//复制并创建一个新的选择模板
									Long stmplId = stmplDupliactor.copy(group.getSelectionTemplateId(), targetGroupComposite);
									if(stmplId != null) {
										nGroup.setSelectionTemplateId(stmplId);
									}
								}
							} catch (Exception e) {
								logger.error("解析字段组[" + group.getTitle() + "]的composite时发生错误，将不复制该字段组", e);
								continue;
							}
						}
						nGroup.setTitle(group.getTitle());
						nGroup.setOrder(group.getOrder());
						nGroup.setUnallowedCreate(group.getUnallowedCreate());
						nGroup.setUnmodifiable(group.getUnmodifiable());
						nGroup.setUpdateTime(newTmpl.getUpdateTime());
						newTmpl.getGroups().add(nGroup);
					}
				}
				Long newTmplId = dtmplManager.merge(newTmpl);
				return newTmplId;
			}
		}
		return null;
	}

}
