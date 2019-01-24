package cn.sowell.dataserver.model.tmpl.bean.express.xml;

import cn.sowell.dataserver.model.tmpl.bean.express.xml.XMLParser.AttributeContext;
import cn.sowell.dataserver.model.tmpl.bean.express.xml.XMLParser.DocumentContext;

public class XMLVisitorImpl extends XMLParserBaseVisitor<byte[]>{
	
	@Override
	public byte[] visitDocument(DocumentContext ctx) {
		// TODO Auto-generated method stub
		return super.visitDocument(ctx);
	}
	
	@Override
	public byte[] visitAttribute(AttributeContext ctx) {
		return super.visitAttribute(ctx);
	}
}