grammar DictFilter;

@header{package cn.sowell.dataserver.model.tmpl.bean.express.df;}

content	: (stat ENDLINE)*;

stat	: exprBase 
		| exprFilterRange
		| exprFilterCriteria
		;

exprBase 	: 'base' opt=(BASE_RANGE_ALL|BASE_RANGE_EMPTY|BASE_RANGE_RANGE) exprRange?;

BASE_RANGE_ALL		: 'all';
BASE_RANGE_EMPTY	: 'empty';
BASE_RANGE_RANGE	: 'range';

exprRange	: '(' exprFieldname (',' exprFieldname)* ')';

exprFieldname	: '`' ~('`')+ ('.' ~('`')+)* '`';


		
exprFilterRange	: INC_EXC=(KW_INCLUDE | KW_EXCLUDE) 'filter' 'range' exprRange;
exprFilterCriteria	: INC_EXC=(KW_INCLUDE | KW_EXCLUDE) 'filter' exprFilterCriteriaString;
exprFilterCriteriaString: target=(KW_TYPE | KW_TITLE) crtr=(CRTR_STR_EQU | CRTR_STR_MATCH) '"' val=STRS '"';


KW_INCLUDE	: 'include';
KW_EXCLUDE	: 'exclude';

KW_TYPE		: 'type';
KW_TITLE		: 'title';

CRTR_STR_EQU	: '==';
CRTR_STR_MATCH	: 'match';

ENDLINE	: ';';
ENTER	: '\r'?'\n' -> skip;
WS	: [\t ]+ -> skip ;

STRS:	('a'..'z' | 'A'..'Z' | '0'..'9' | '\u4E00'..'\u9FA5' 
	| '\uF900'..'\uFA2D' | '(' | ')' | '（' | '）' | '^' | '$'
	| '\\' | '.' | '+' + '-' | '/' | '-'
	)+;
