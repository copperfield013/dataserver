package cn.sowell.dataserver.model.tmpl.duplicator;

public interface TemplateDuplicator<R> {

	Long copy(Long tmplId, R targetReference);

}
