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

COMMENT: SLASH SLASH ~NEWLINE* NEWLINE {$channel=HIDDEN;}
       | SLASH STAR ( options {greedy=false;} : . )* STAR SLASH {$channel=HIDDEN;}
    ;

NEWLINE: '\r\n' | '\r' | '\n';

STRING: DQUOTE ( ESC_SEQ | ~(BACKSLASH|DQUOTE) )* DQUOTE;

TEXT: OBRACE DQUOTE
	  ( options {greedy=false;} : . )*
      // TODO: probably want to improve this, in case people do silly things
      // like having escaped "} inside a text document
      DQUOTE CBRACE;

DMI: SQUOTE ( ESC_SEQ | ~(SQUOTE|BACKSLASH) )* SQUOTE;

fragment DIGIT: '0'..'9';
fragment LETTER: 'a'..'z' | 'A'..'Z';
fragment EXPONENT: ('e'|'E') (PLUS | MINUS)? (DIGIT)+;
fragment HEX_DIGIT: (DIGIT|'a'..'f'|'A'..'F');

fragment ESC_SEQ:   BACKSLASH ('b'|'t'|'n'|'f'|'r'|DQUOTE|SQUOTE|BACKSLASH)
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   BACKSLASH ('0'..'3') ('0'..'7') ('0'..'7')
    |   BACKSLASH ('0'..'7') ('0'..'7')
    |   BACKSLASH ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   BACKSLASH 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
