package cn.sowell.dataserver.model.karuiserv.manager;

import java.util.List;

import cn.sowell.dataserver.model.karuiserv.pojo.KaruiServ;
import cn.sowell.dataserver.model.tmpl.manager.ModuleCachableManager;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateListTemplate;

public interface KaruiServManager extends ModuleCachableManager<KaruiServ>{

	List<KaruiServ> queryAll();

	void updateKaruServByListTemplate(TemplateListTemplate ltmpl);

	void updateKaruServByDetailTemplate(TemplateDetailTemplate dtmpl);

	void toggleDisabled(Long ksId, boolean disabled);

}
