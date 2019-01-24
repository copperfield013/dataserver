package test;

import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.dataserver.model.tmpl.bean.express.xml.XMLLexer;
import cn.sowell.dataserver.model.tmpl.bean.express.xml.XMLParser;
import cn.sowell.dataserver.model.tmpl.bean.express.xml.XMLVisitorImpl;

public class TestXMLParser {
	public static void main(String[] args) {

		try {
			Resource fr = new FileSystemResource("d://TestXML");
			String str = TextUtils.readAsString(fr.getInputStream()).trim();

			CodePointCharStream inputStream = CharStreams.fromString(str);
			XMLLexer lexer = new XMLLexer(inputStream);
			CommonTokenStream tokenStream = new CommonTokenStream(lexer);
			XMLParser parser = new XMLParser(tokenStream);
			testVisitor(parser);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void testVisitor(XMLParser parser) {
		XMLVisitorImpl visitor = new XMLVisitorImpl();
		visitor.visit(parser.document());
	}
}
