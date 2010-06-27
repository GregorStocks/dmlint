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
	INDENT;
	DEDENT;
	SPACE = ' ';
	TAB = '\t';
	BACKSLASH = '\\';
	SQUOTE = '\'';
	DQUOTE = '"'; // to fix vim's syntax highlighting i'm gonna say " here
	OBRACE = '{';
	CBRACE = '}';
	OPAREN = '(';
	CPAREN = ')';
	GT = '>';
	LT = '<';
	EQ = '=';
	AND = '&';
	OR = '|';
	OBRACKET = '[';
	CBRACKET = ']';
	NOT = '!';
	COMMA = ',';
	QUESTION = '?';
	COLON = ':';
	MODULO = '%';
	SEMICOLON = ';';
	BNOT = '~';
	XOR = '^';
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

/*COMMENT: SLASH SLASH ( options {greedy=false;} : .*)
NEWLINE {$channel=HIDDEN;}
       | SLASH STAR  ( options {greedy=false;} : ({ LA(2)!='/' }? '*'|~('*'|'/' ))*) STAR SLASH {$channel=HIDDEN;}
    ;*/

COMMENT
    : '/*' ( options {greedy=false;} : .*) '*/' {$channel=HIDDEN;}
    ;
LINE_COMMENT
    : '//' ~('\n')* '\n' {$channel=HIDDEN;}
    ;


NEWLINE: '\r\n' | '\n'; // preprocessing might take care of this for us

STRING: DQUOTE ( ESC_SEQ | ~(BACKSLASH|DQUOTE|OBRACKET) )* DQUOTE;

INTERPOLATED_STRING_START: DQUOTE (ESC_SEQ | ~(BACKSLASH|DQUOTE|OBRACKET))* OBRACKET;
INTERPOLATED_STRING_MIDDLE: CBRACKET (ESC_SEQ | ~(BACKSLASH|DQUOTE|OBRACKET))* OBRACKET;
INTERPOLATED_STRING_END: CBRACKET (ESC_SEQ | ~(BACKSLASH|DQUOTE))* DQUOTE;

DMI: SQUOTE ( ESC_SEQ | ~(SQUOTE|BACKSLASH) )* SQUOTE;

fragment DIGIT: '0'..'9';
fragment LETTER: 'a'..'z' | 'A'..'Z';
fragment EXPONENT: ('e'|'E') (PLUS | MINUS)? (DIGIT)+;

fragment ESC_SEQ:   BACKSLASH (DQUOTE|SQUOTE|BACKSLASH|OBRACKET|CBRACKET|'...'|LT|GT|(LETTER|DIGIT)+);

