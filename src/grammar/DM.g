grammar DM;

options {
	language = Python;
}

tokens {
	PLUS = '+';
	MINUS = '-';
	STAR = '*';
	SLASH = '/';
	UNDERSCORE = '_';
	DOT = '.';
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

expr	: term ( ( PLUS | MINUS ) term )* ;

term	: factor;

factor	: INT ;


/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

ID:	(LETTER | UNDERSCORE) (LETTER | UNDERSCORE | DIGIT)*;

INT: DIGIT+;

FLOAT: DIGIT+ DOT (DIGIT)* EXPONENT?
     | DOT DIGIT+ EXPONENT?
     | DIGIT+ EXPONENT;

COMMENT: SLASH SLASH ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
       | SLASH STAR ( options {greedy=false;} : . )* STAR SLASH {$channel=HIDDEN;}
    ;

fragment DIGIT: '0'..'9';
fragment LETTER: 'a'..'z' | 'A'..'Z';
fragment EXPONENT: ('e'|'E') (PLUS | MINUS)? (DIGIT)+;
fragment HEX_DIGIT : (DIGIT|'a'..'f'|'A'..'F');
