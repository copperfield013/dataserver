package cn.sowell.dataserver.model.tmpl.pojo;

import java.util.function.BiFunction;

import org.springframework.util.Assert;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.datacenter.entityResolver.impl.RelationEntityPropertyParser;
import cn.sowell.dataserver.model.abc.service.ABCExecuteService;

public class ArrayEntityProxy {

	private static ThreadLocal<UserIdentifier> localUser = new ThreadLocal<>();
	public static void setLocalUser(UserIdentifier user) {
		localUser.set(user);
	}


	private ABCExecuteService abcService;
	private String relationName;
	private String relationEntityCode;
	private String moduleName;
	
	public ArrayEntityProxy(ABCExecuteService abcService, String moduleName, String relationName,
			String relationEntityCode) {
		Assert.notNull(abcService, "ArrayEntityProxy初始化必须传入ABCExecuteService");
		Assert.hasText(moduleName, "ArrayEntityProxy初始化必须传入moduleName");
		Assert.hasText(relationName, "ArrayEntityProxy初始化必须传入relationName");
		Assert.hasText(relationEntityCode, "ArrayEntityProxy初始化必须传入relationEntityCode");
		
		this.abcService = abcService;
		this.moduleName = moduleName;
		this.relationName = relationName;
		this.relationEntityCode = relationEntityCode;
	}
	
	private ThreadLocal<RelationEntityPropertyParser> localParser = new ThreadLocal<>();
	
	
	
	private <T> T _getFieldValue(String fieldName, BiFunction<RelationEntityPropertyParser, String, T> func) {
		RelationEntityPropertyParser parser = getParser();
		if(fieldName != null) {
			return func.apply(parser, fieldName);
		}
		return null;
	}
	
	private synchronized RelationEntityPropertyParser getParser() {
		RelationEntityPropertyParser parser = localParser.get();
		if(parser == null) {
			UserIdentifier user = localUser.get();
			Assert.notNull(user, "获得ArrayEntityProxy之前需要设置user");
			parser = abcService.getRelationEntityParser(moduleName, relationName, relationEntityCode, localUser.get());
			localParser.set(parser);
		}
		return parser;
	}

	public Object getFieldValue(String fieldName) {
		return _getFieldValue(fieldName, RelationEntityPropertyParser::getProperty);
	}
	
	public String getFormatedFieldValue(String fieldName) {
		return _getFieldValue(fieldName, RelationEntityPropertyParser::getFormatedProperty);
	}

}