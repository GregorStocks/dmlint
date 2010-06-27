grammar DM;


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
	OBRACE = '{';
	CBRACE = '}';
	OPAREN = '(';
	CPAREN = ')';
	GT = '>';
	LT = '<';
	EQ = '=';
	AND = '&';
	OR = '|';
	LBRACKET = '[';
	RBRACKET = ']';
	NOT = '!';
	COMMA = ',';
	QUESTION = '?';
	COLON = ':';
	MODULO = '%';
	SEMICOLON = ';';
	BNOT = '~';
	XOR = '^';
}

@members {
    public static void main(String[] args) throws Exception {
        DMLexer lex = new DMLexer(new ANTLRFileStream(args[0]));
       	CommonTokenStream tokens = new CommonTokenStream(lex);

        DMParser parser = new DMParser(tokens);

        try {
            parser.expr();
        } catch (RecognitionException e)  {
            e.printStackTrace();
        }
    }
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


NEWLINE: '\n'; // preprocessing removes \r

STRING: '"' ( ESC_SEQ | ~(BACKSLASH|'"') )* '"';

DMI: '\'' ( ESC_SEQ | ~('\''|BACKSLASH) )* '\'';

fragment DIGIT: '0'..'9';
fragment LETTER: 'a'..'z' | 'A'..'Z';
fragment EXPONENT: ('e'|'E') (PLUS | MINUS)? (DIGIT)+;

fragment ESC_SEQ:   BACKSLASH ('"'|'\''|BACKSLASH|LBRACKET|RBRACKET|'...'|LT|GT|(LETTER|DIGIT)+);

