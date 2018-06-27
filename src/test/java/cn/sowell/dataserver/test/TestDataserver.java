package cn.sowell.dataserver.test;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.service.TemplateService;

/*@ContextConfiguration(locations = "classpath*:spring-dataserver.xml")
@RunWith(SpringJUnit4ClassRunner.class)*/
public class TestDataserver {
	
	@Resource
	ModulesService mService;
	
	@Resource
	TemplateService tService;
	
	Logger logger = Logger.getLogger(TestDataserver.class);
	
	
	public void test() {
		try {
			List<TemplateGroup> groups = tService.queryTemplateGroups("people");
			System.out.println(groups);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
