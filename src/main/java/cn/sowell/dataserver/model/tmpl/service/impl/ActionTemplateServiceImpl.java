package cn.sowell.dataserver.model.tmpl.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.datacenter.entityResolver.impl.ABCNodeProxy;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionField;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateActionTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupAction;
import cn.sowell.dataserver.model.tmpl.service.ActionTemplateService;
import cn.sowell.dataserver.model.tmpl.service.TemplateService;

@Service
public class ActionTemplateServiceImpl implements ActionTemplateService{

	@Resource
	ModulesService mService;
	
	@Resource
	TemplateService tService;
	
	static Logger logger = Logger.getLogger(ActionTemplateServiceImpl.class);
	
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public int doAction(TemplateActionTemplate atmpl, Set<String> codes, boolean isTransaction,
			UserIdentifier currentUser) {
		int sucs = 0;
		for (String code : codes) {
			try {
				Map<String, Object> fieldValueMap = generateFieldValueMap(atmpl);
				fieldValueMap.put(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL, code);
				mService.mergeEntity(atmpl.getModule(), fieldValueMap, currentUser);
				if(!isTransaction) {
					//如果是非事务型，那么每修改一个实体都提交一次
					TransactionAspectSupport.currentTransactionStatus().flush();
				}
				sucs++;
			} catch (Exception e) {
				if(isTransaction) {
					throw new RuntimeException("操作实体[code=" + code + "]时发生错误", e);
				}else {
					logger.error("操作实体[code=" + code + "]时发生错误", e);
				}
			}
		}
		if(sucs == 0) {
			throw new RuntimeException("对实体[codes=" + codes + "]执行多选操作[id=" + atmpl.getId() + ", title=" + atmpl.getTitle() + "]全部失败");
		}
		return sucs;
	}


	private Map<String, Object> generateFieldValueMap(TemplateActionTemplate atmpl) {
		List<TemplateActionFieldGroup> groups = atmpl.getGroups();
		Map<String, Object> entityMap = new HashMap<String, Object>();
		if(groups != null) {
			for (TemplateActionFieldGroup group : groups) {
				List<TemplateActionField> fields = group.getFields();
				if(fields != null) {
					for (TemplateActionField field : fields) {
						if(field.getFieldAvailable()) {
							if(!Integer.valueOf(1).equals(group.getIsArray())) {
								entityMap.put(field.getFieldName(), field.getViewValue());
							}else {
								//TODO: 一对多的字段
								
							}
						}
					}
				}
			}
		}
		entityMap.remove(ABCNodeProxy.CODE_PROPERTY_NAME_NORMAL);
		return entityMap;
	}
	
	@Override
	public void coverActionFields(TemplateGroupAction groupAction, Map<String, Object> map) {
		TemplateActionTemplate atmpl = tService.getActionTemplate(groupAction.getAtmplId());
		map.putAll(generateFieldValueMap(atmpl));
	}

}
