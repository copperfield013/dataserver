package cn.sowell.dataserver.model.tmpl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.sowell.dataserver.model.tmpl.manager.ArrayItemFilterManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailArrayItemFilter;
import cn.sowell.dataserver.model.tmpl.service.ArrayItemFilterService;

@Service
public class ArrayItemFilterServiceImpl 
	extends AbstractTemplateService<TemplateDetailArrayItemFilter, ArrayItemFilterManager>
	implements ArrayItemFilterService{

	@Autowired
	public ArrayItemFilterServiceImpl(@Autowired ArrayItemFilterManager manager) {
		super(manager);
	}
	
	

}
