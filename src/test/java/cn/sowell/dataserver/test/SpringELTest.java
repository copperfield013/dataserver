package cn.sowell.dataserver.test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class SpringELTest {
	@Test
	public void test() {
		
		Map<String, String> map = new HashMap<>();
		map.put("name", "1111");
		map.put("age", "20");
		
		String originText = "名字：${name}，年龄：${age}";
		
		Pattern pattern = Pattern.compile("\\$\\{([^\\}]+)\\}");
		
		Matcher matcher = pattern.matcher(originText);
		StringBuffer buffer = new StringBuffer();
		while(matcher.find()) {
			String propertyName = matcher.group(1);
			String propertyValue = map.get(propertyName);
			matcher.appendReplacement(buffer, propertyValue);
		}
		
		System.out.println(buffer);
		
		/*
		
		String text = "名字：#{#entity['name']}";
		Map<String, String> map = new HashMap<>();
		map.put("name", "1111");
		ExpressionParser parser = new SpelExpressionParser();
		Expression expression = parser.parseExpression(text, new TemplateParserContext());
		EvaluationContext context = new StandardEvaluationContext();
		context.setVariable("entity", map);
		Object value = expression.getValue(context);
		System.out.println(value);*/
	}
}
