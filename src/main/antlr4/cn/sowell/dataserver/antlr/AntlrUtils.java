package cn.sowell.dataserver.antlr;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import cn.sowell.copframe.dao.deferedQuery.Function;
import cn.sowell.copframe.utils.TextUtils;

public class AntlrUtils {

	public static <L extends Lexer, P extends Parser, R> Supplier<R> getPreparedSupplier(ParseTreeVisitor<R> visitor, String path, 
			Function<CharStream, L> lexerConstructor, 
			Function<TokenStream, P> parserConstructor,
			Function<P, ParseTree> mainExpr) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		try {
			Resource fr = new FileSystemResource(path);
			String str = TextUtils.readAsString(fr.getInputStream()).trim();

			CodePointCharStream inputStream = CharStreams.fromString(str);
			L lexer = lexerConstructor.apply(inputStream);
			CommonTokenStream tokenStream = new CommonTokenStream(lexer);
			P parser = parserConstructor.apply(tokenStream);
			ParseTree tree = mainExpr.apply(parser);
			return ()->visitor.visit(tree);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	} 
	
	public static <L extends Lexer, P extends Parser, R> R visit(ParseTreeVisitor<R> visitor, String path, 
			Function<CharStream, L> lexerConstructor, 
			Function<TokenStream, P> parserConstructor,
			Function<P, ParseTree> mainExpr) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return getPreparedSupplier(visitor, path, lexerConstructor, parserConstructor, mainExpr).get();
	}

}
