package cn.sowell.dataserver.model.tmpl.duplicator.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;
import cn.sowell.dataserver.model.dict.service.DictionaryService;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.tmpl.duplicator.ModuleTemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.manager.TemplateGroupManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupAction;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupPremise;

@Component
public class TemplateGroupDuplicator implements ModuleTemplateDuplicator{

	@Resource
	TemplateGroupManager tmplGroupManager;
	
	@Resource
	ModulesService mService;
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	@Resource
	ListTemplateDuplicator ltmplDuplicator;
	
	@Resource
	DetailTemplateDuplicator dtmplDuplicator;
	
	@Resource
	ActionTemplateDuplicator atmplDuplicator;
	
	@Resource
	DictionaryService dictService;
	
	static Logger logger = Logger.getLogger(TemplateGroupDuplicator.class);
	
	
	@Override
	public Long copy(Long tmplGroupId, String targetModuleName) {
		Assert.notNull(tmplGroupId);
		Assert.hasText(targetModuleName);
		
		TemplateGroup tmplGroup = tmplGroupManager.get(tmplGroupId);
		if(tmplGroup != null) {
			ModuleMeta sourceModule = mService.getModule(tmplGroup.getModule());
			FusionContextConfig config = fFactory.getModuleConfig(targetModuleName);
			if(config != null) {
				Long newListTmplId = ltmplDuplicator.copy(tmplGroup.getListTemplateId(), targetModuleName);
				if(newListTmplId != null) {
					Long newDetailTmplId = dtmplDuplicator.copy(tmplGroup.getDetailTemplateId(), targetModuleName);
					if(newDetailTmplId != null) {
						TemplateGroup newGroup = new TemplateGroup();
						List<TemplateGroupPremise> premises = tmplGroup.getPremises();
						if(premises != null && !premises.isEmpty()) {
							for (TemplateGroupPremise premise : premises) {
								if(premise.getFieldId() != null) {
									DictionaryField targetPremiseField = dictService.mapModuleField(targetModuleName, dictService.getField(tmplGroup.getModule(), premise.getFieldId()));
									if(targetPremiseField != null) {
										TemplateGroupPremise newPremise = new TemplateGroupPremise();
										newPremise.setFieldId(targetPremiseField.getId());
										newPremise.setFieldValue(premise.getFieldValue());
										newPremise.setOrder(premise.getOrder());
										newGroup.getPremises().add(newPremise);
									}else {
										logger.error("复制模板组合[" + sourceModule.getName() 
											+ "]的前提字段[" + premise.getFieldTitle() + "]失败，在目标模块[" 
											+ targetModuleName + "]找不到对应的字段");
									}
								}else {
									logger.error("复制模板组合[" + sourceModule.getName() 
										+ "]的前提字段[" + premise.getFieldTitle() + "]失败，fieldId为空");
								}
							}
						}
						
						List<TemplateGroupAction> actions = tmplGroup.getActions();
						Map<Long, Long> atmplIdMap = new HashMap<>();
						if(actions != null && !actions.isEmpty()) {
							for (TemplateGroupAction action : actions) {
								TemplateGroupAction nAction = new TemplateGroupAction();
								if(!atmplIdMap.containsKey(action.getAtmplId())) {
									Long newAtmplId = atmplDuplicator.copy(action.getAtmplId(), targetModuleName);
									atmplIdMap.put(action.getAtmplId(), newAtmplId);
								}
								nAction.setAtmplId(atmplIdMap.get(action.getAtmplId()));
								nAction.setFace(action.getFace());
								nAction.setMultiple(action.getMultiple());
								nAction.setOrder(action.getOrder());
								nAction.setTitle(action.getTitle());
								nAction.setIconClass(action.getIconClass());
								nAction.setOutgoing(action.getOutgoing());
								nAction.setType(action.getType());
								newGroup.getActions().add(nAction);
							}
						}
						
						newGroup.setTitle("(复制自" + sourceModule.getTitle() + "-" + tmplGroup.getTitle() + ")");
						newGroup.setModule(targetModuleName);
						newGroup.setDisabled(tmplGroup.getDisabled());
						newGroup.setHideCreateButton(tmplGroup.getHideCreateButton());
						newGroup.setHideExportButton(tmplGroup.getHideExportButton());
						newGroup.setHideImportButton(tmplGroup.getHideImportButton());
						newGroup.setHideQueryButton(tmplGroup.getHideQueryButton());
						newGroup.setHideDeleteButton(tmplGroup.getHideDeleteButton());
						newGroup.setListTemplateId(newListTmplId);
						newGroup.setDetailTemplateId(newDetailTmplId);
						return tmplGroupManager.merge(newGroup);
					}
				}
			}
		}
		return null;
	}

}
