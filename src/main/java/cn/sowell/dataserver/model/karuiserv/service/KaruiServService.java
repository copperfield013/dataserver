package cn.sowell.dataserver.model.karuiserv.service;

import java.util.List;
import java.util.Set;

import cn.sowell.dataserver.model.karuiserv.pojo.KaruiServ;
import cn.sowell.dataserver.model.tmpl.duplicator.ModuleTemplateDuplicator;
import cn.sowell.dataserver.model.tmpl.service.OpenTemplateService;

public interface KaruiServService extends OpenTemplateService<KaruiServ>, ModuleTemplateDuplicator{
	List<KaruiServ> queryAll();

	void reloadCache();

	boolean validateTitleExistion(String title);

	boolean validatePathExistion(String path);

	void remove(Set<Long> ksIds);

	void toggleDisabled(Set<Long> ksIds, boolean disabled);

}
