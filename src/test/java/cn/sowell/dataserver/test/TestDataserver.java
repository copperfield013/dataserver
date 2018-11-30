package cn.sowell.dataserver.test;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.modules.service.ViewDataService;
import cn.sowell.dataserver.model.modules.service.impl.EntityView;
import cn.sowell.dataserver.model.modules.service.impl.EntityViewCriteria.CriteriaEntry;
import cn.sowell.dataserver.model.modules.service.impl.ListTemplateEntityViewCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.service.TemplateService;

//@ContextConfiguration(locations = "classpath*:spring-dataserver.xml")
public class TestDataserver {
	
	@Resource
	ModulesService mService;
	
	@Resource
	TemplateService tService;
	
	@Resource
	ViewDataService vService;
	
	Logger logger = Logger.getLogger(TestDataserver.class);
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	public void test() {
		try {
			List<TemplateGroup> groups = tService.queryTemplateGroups("people");
			System.out.println(groups);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testQuery() {
		ListTemplateEntityViewCriteria criteria = new ListTemplateEntityViewCriteria();
		criteria.setModule("people");
		criteria.setListTemplateId(32l);
		CriteriaEntry entry = new CriteriaEntry();
		entry.setFieldId(703l);
		entry.setComparator("s1");
		entry.setValue("未婚");
		criteria.getCriteriaEntries().add(entry);
		UserIdentifier user = null;
		criteria.setUser(user);
		/*Map<Long, String> listTemplateCriteria = new HashMap<>();
		listTemplateCriteria.put(59l, "1");
		criteria.setListTemplateCriteria(listTemplateCriteria);*/
		EntityView view = vService.query(criteria);
		System.out.println(view.toJson());
	}
	
}
