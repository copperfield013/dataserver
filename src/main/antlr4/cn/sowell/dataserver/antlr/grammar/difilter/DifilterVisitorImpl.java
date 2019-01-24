package cn.sowell.dataserver.antlr.grammar.difilter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;

import com.beust.jcommander.internal.Sets;

import cn.sowell.dataserver.antlr.grammar.difilter.DifilterParser.BaseStatContext;
import cn.sowell.dataserver.antlr.grammar.difilter.DifilterParser.ExprFilterCriteriaContext;
import cn.sowell.dataserver.antlr.grammar.difilter.DifilterParser.ExprFilterCriteriaStringContext;
import cn.sowell.dataserver.antlr.grammar.difilter.DifilterParser.ExprFilterRangeContext;
import cn.sowell.dataserver.antlr.grammar.difilter.DifilterParser.FieldIdRangeContext;
import cn.sowell.dataserver.model.dict.pojo.DictionaryField;

public class DifilterVisitorImpl extends DifilterParserBaseVisitor<Set<DictionaryField>>{
	private Map<Long, DictionaryField> allRangeMap;
    private Set<DictionaryField> currentRange = new LinkedHashSet<>();

    public DifilterVisitorImpl(Map<Long, DictionaryField> map) {
		this.allRangeMap = map;
	}
    

	@Override
	public Set<DictionaryField> visitBaseStat(BaseStatContext ctx) {
		switch (ctx.opt.getType()) {
			case DifilterLexer.KW_ALL:
				this.currentRange = new LinkedHashSet<>(allRangeMap.values());
				break;
			case DifilterLexer.KW_EMPTY:
				this.currentRange = new LinkedHashSet<>();
				break;
			case DifilterLexer.KW_RANGE:
				this.currentRange = visit(ctx.exprRange());
				break;
			default:
				throw new RuntimeException();
		}
		return this.currentRange;
	}
	
	
	@Override
	public Set<DictionaryField> visitFieldIdRange(FieldIdRangeContext ctx) {
		Set<DictionaryField> fields = Sets.newLinkedHashSet();
		ctx.FIELD_ID().forEach((fieldIdString)->{
			Long fieldId = Long.valueOf(fieldIdString.getText());
			DictionaryField field = allRangeMap.get(fieldId);
			if(field != null) {
				fields.add(field);
			}
		});
		return fields;
	}
	
	
	public Set<DictionaryField> getCurrentRange() {
		return Collections.synchronizedSet(this.currentRange);
	}
	
	@Override
	public Set<DictionaryField> visitExprFilterRange(ExprFilterRangeContext ctx) {
		return doOperate(ctx.opr.getType(), visit(ctx.exprRange()));
	}
	
	@Override
	public Set<DictionaryField> visitExprFilterCriteria(ExprFilterCriteriaContext ctx) {
		return doOperate(ctx.opr.getType(), visit(ctx.exprFilterCriteriaString()));
	}
	
	private synchronized Set<DictionaryField> doOperate(int oprType, Set<DictionaryField> set) {
		switch(oprType) {
			case DifilterLexer.OPR_ADD:
				this.currentRange.addAll(set);
				break;
			case DifilterLexer.OPR_SUB:
				this.currentRange.removeAll(set);
				break;
			default: throw new RuntimeException();
		}
		return this.currentRange;
	}
	
	
	@Override
	public Set<DictionaryField> visitExprFilterCriteriaString(ExprFilterCriteriaStringContext ctx) {
		return filter(getTargetGetter(ctx.target), getCriteriaPredicate(ctx.crtr, getString(ctx.val)));
		
	}
	
	private Set<DictionaryField> filter(Function<DictionaryField, String> targetGetter,
			Predicate<String> criteriaPredicate) {
		return this.currentRange.stream()
			.filter((field)->criteriaPredicate.test(targetGetter.apply(field)))
			.collect(Collectors.toSet());
	}


	private String getString(Token val) {
		if(val.getType() == DifilterLexer.STRING) {
			String string = val.getText();
			return string.substring(1, string.length() - 1);
		}
		return null;
	}


	private Predicate<String> getCriteriaPredicate(Token crtr, String comparedValue) {
		switch (crtr.getType()) {
			case DifilterLexer.CRTR_STR_EQU:
				return (sourceFieldName)->sourceFieldName.equals(comparedValue);
			case DifilterLexer.CRTR_STR_MATCH:
				return Pattern.compile(comparedValue).asPredicate();
			default : throw new RuntimeException();
		}
	}


	private Function<DictionaryField, String> getTargetGetter(Token target) {
		switch (target.getType()) {
			case DifilterLexer.KW_TYPE:
				return  DictionaryField::getType;
			case DifilterLexer.KW_TITLE:
				return DictionaryField::getTitle;
			default : throw new RuntimeException();
		}
	}


	
	
}
