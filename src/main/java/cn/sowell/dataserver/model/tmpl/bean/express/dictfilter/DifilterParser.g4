parser grammar DifilterParser;

@header {package cn.sowell.dataserver.antlr.grammar.difilter;}

options { tokenVocab=DifilterLexer; }

progs		: (stat ';')*;

stat 		: baseStat
			| exprFilterRange
			| exprFilterCriteria
			;

baseStat	: 'base' opt=(KW_ALL | KW_EMPTY | KW_RANGE) exprRange?;



exprFilterRange	: opr=(OPR_ADD | OPR_SUB) 'range' exprRange;


exprFilterCriteria	: opr=(OPR_ADD | OPR_SUB) 'filter' exprFilterCriteriaString;
exprFilterCriteriaString: target=(KW_TYPE | KW_TITLE) 
						crtr=(CRTR_STR_EQU | CRTR_STR_MATCH) 
						val=(STRING|FIELD_ID);


exprRange	: '(' FIELD_ID (',' FIELD_ID)* ')' 		#fieldIdRange
			;

