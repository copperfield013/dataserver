package test;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.tmpl.bean.express.df.DictFilterLexer;
import cn.sowell.dataserver.model.tmpl.bean.express.df.DictFilterParser;
import cn.sowell.dataserver.model.tmpl.bean.express.df.DictFilterVisitorImpl;

public class TestDictFilter {

	public static void main(String[] args) {

		try {
			Resource fr = new FileSystemResource("d://TestDictFilter");
			String str = TextUtils.readAsString(fr.getInputStream()).trim();

			CodePointCharStream inputStream = CharStreams.fromString(str);
			DictFilterLexer lexer = new DictFilterLexer(inputStream);
			CommonTokenStream tokenStream = new CommonTokenStream(lexer);
			DictFilterParser parser = new DictFilterParser(tokenStream);

			testVisitor(parser);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void testVisitor(DictFilterParser parser) {
		Set<String> baseRange = new LinkedHashSet<String>();
		baseRange.add("基本.姓名");
		baseRange.add("基本.性别");
		DictFilterVisitorImpl visitor = new DictFilterVisitorImpl(baseRange);

		visitor.visit(parser.content());
		System.out.println();
		System.out.println(visitor.getCurrentSet());

	}
}
