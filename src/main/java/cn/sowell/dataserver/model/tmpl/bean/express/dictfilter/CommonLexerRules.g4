lexer grammar CommonLexerRules;
	
fragment INT	: [0-9]+;
ENDLINE	: ';';
ENTER	: '\r'?'\n' -> skip;
WS	: [\t ]+ -> skip ;
