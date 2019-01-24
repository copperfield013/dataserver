lexer grammar DifilterLexer;

@header {package cn.sowell.dataserver.antlr.grammar.difilter;}


ENDSTAT		: ';';

FIELD_NAME	: '`' (~('`' | '\r' | '\n'))* '`';

STRING      : '\'' ( ~('\'' | '\r' | '\n') )* '\'';

S           :   [ \t\r\n]               -> skip ;

fragment
HEXDIGIT    :   [a-fA-F0-9] ;

fragment
DIGIT       :   [0-9] ;

fragment
NameChar    :   NameStartChar
            |   '-' | '_' | '.' | DIGIT
            |   '\u00B7'
            |   '\u0300'..'\u036F'
            |   '\u203F'..'\u2040'
            ;

fragment
NameStartChar
            :   [:a-zA-Z]
            |   '\u2070'..'\u218F'
            |   '\u2C00'..'\u2FEF'
            |   '\u3001'..'\uD7FF'
            |   '\uF900'..'\uFDCF'
            |   '\uFDF0'..'\uFFFD'
            ;

RANGE_START	: '(';
RANGE_END	: ')';
RANGE_SPLIT	: ',';

KW_BASE		: 'base';
KW_ALL		: 'all';
KW_EMPTY	: 'empty';
KW_RANGE	: 'range';




OPR_ADD		: 'add';
OPR_SUB		: 'sub';

KW_FILTER	: 'filter';

KW_TYPE		: 'type';
KW_TITLE		: 'title';

CRTR_STR_EQU	: '==';
CRTR_STR_MATCH	: 'match';

FIELD_ID	: DIGIT+;