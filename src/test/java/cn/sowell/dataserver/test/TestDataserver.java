package cn.sowell.dataserver.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.abc.application.BizFusionContext;
import com.abc.mapping.entity.Entity;
import com.abc.panel.Discoverer;
import com.abc.panel.PanelFactory;
import com.abc.query.criteria.Criteria;
import com.abc.query.criteria.CriteriaFactory;
import com.abc.query.entity.impl.EntitySortedPagedQuery;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.FusionContextConfigResolver;
import cn.sowell.datacenter.entityResolver.impl.RelationEntityPropertyParser;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.modules.service.ViewDataService;
import cn.sowell.dataserver.model.modules.service.impl.EntityView;
import cn.sowell.dataserver.model.modules.service.impl.EntityViewCriteria.CriteriaEntry;
import cn.sowell.dataserver.model.modules.service.impl.ListTemplateEntityViewCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.service.TemplateService;

@ContextConfiguration(locations = "classpath*:spring-dataserver.xml")
@RunWith(SpringJUnit4ClassRunner.class)
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
	
	@Test
	@Transactional
	public void testRelation() {
		FusionContextConfig config = fFactory.getModuleConfig("DSNc5a274h");
		FusionContextConfigResolver resolver = config.getConfigResolver();
		
		
		
		BizFusionContext context = new BizFusionContext();
		String relationMapping = "新的测试.t关系";
		context.setMappingName(relationMapping);
		context.setUserCode("u5");
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		
		List<Criteria> criterias = new ArrayList<>();
		CriteriaFactory criteriaFactory = new CriteriaFactory(context);
		Criteria c = criteriaFactory.createLeftLikeQueryCriteria("名字", "1");
		criterias.add(c);
		
		EntitySortedPagedQuery sortedPagedQuery = discoverer.discover(criterias, "编辑时间");
		sortedPagedQuery.setPageSize(10);
		List<Entity> peoples = sortedPagedQuery.visit(1);
		RelationEntityPropertyParser parser = resolver.createRelationParser(peoples.get(0), "t关系", "u5");
		System.out.println(parser.getCode());
		System.out.println(parser.getProperty("t关系.名字"));
	}
	
	//@Test
	public void testRelationQuery() {
		BizFusionContext context = new BizFusionContext();
		String relationMapping = "新的测试.t关系";
		context.setMappingName(relationMapping);
		context.setUserCode("u5");
		Discoverer discoverer=PanelFactory.getDiscoverer(context);
		
		List<Criteria> criterias = new ArrayList<>();
		CriteriaFactory criteriaFactory = new CriteriaFactory(context);
		Criteria c = criteriaFactory.createLeftLikeQueryCriteria("名字", "1");
		criterias.add(c);
		
		EntitySortedPagedQuery sortedPagedQuery = discoverer.discover(criterias, "编辑时间");
		sortedPagedQuery.setPageSize(10);
		List<Entity> peoples = sortedPagedQuery.visit(1);
		System.out.println(peoples);
	}
	
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
