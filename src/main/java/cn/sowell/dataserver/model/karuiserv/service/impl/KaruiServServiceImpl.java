package cn.sowell.dataserver.model.karuiserv.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import cn.sowell.dataserver.model.karuiserv.manager.KaruiServManager;
import cn.sowell.dataserver.model.karuiserv.pojo.KaruiServ;
import cn.sowell.dataserver.model.karuiserv.service.KaruiServService;
import cn.sowell.dataserver.model.tmpl.service.impl.AbstractTemplateService;

@Service
public class KaruiServServiceImpl 
	extends AbstractTemplateService<KaruiServ, KaruiServManager> 
	implements KaruiServService{

	protected KaruiServServiceImpl(KaruiServManager manager) {
		super(manager);
	}

	@Override
	public Long copy(Long tmplId, String targetReference) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<KaruiServ> queryAll() {
		return getManager().queryAll();
	}

	@Override
	public void reloadCache() {
		getManager().reloadCache();
	}

	@Override
	public boolean validateTitleExistion(String title) {
		if(title == null) return false;
		List<KaruiServ> ksList = getManager().queryAll();
		return ksList.stream().anyMatch(ks->title.equals(ks.getTitle()));
	}

	@Override
	public boolean validatePathExistion(String path) {
		if(path == null) return false;
		List<KaruiServ> ksList = getManager().queryAll();
		return ksList.stream().anyMatch(ks->path.equals(ks.getPath()));
	}

	@Override
	public void remove(Set<Long> ksIds) {
		for (Long ksId : ksIds) {
			getManager().remove(ksId);
		}
	}

	@Override
	public void toggleDisabled(Set<Long> ksIds, boolean disabled) {
		for (Long ksId : ksIds) {
			getManager().toggleDisabled(ksId, disabled);
		}
		
	}

}
