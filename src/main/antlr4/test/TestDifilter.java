package test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import cn.sowell.dataserver.antlr.AntlrUtils;
import cn.sowell.dataserver.antlr.grammar.difilter.DifilterLexer;
import cn.sowell.dataserver.antlr.grammar.difilter.DifilterParser;
import cn.sowell.dataserver.antlr.grammar.difilter.DifilterVisitorImpl;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;

public class TestDifilter {
	public static void main(String[] args) throws Exception {
		Map<Long, DictionaryField> fieldMap = new HashMap<>();
		DifilterVisitorImpl visitor = new DifilterVisitorImpl(fieldMap);
		Supplier<Set<DictionaryField>> supplier = AntlrUtils.getPreparedSupplier(
				visitor, 
				"d://TestDictFilter", 
				DifilterLexer::new, 
				DifilterParser::new,
				DifilterParser::progs);
		System.out.println();
		System.out.println(visitor.getCurrentRange());
		supplier.get();
		System.out.println(visitor.getCurrentRange());
	}
}
