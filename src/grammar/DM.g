grammar DM;

tokens {
	PLUS = '+';
	MINUS = '-';
	STAR = '*';
	SLASH = '/';
	UNDERSCORE = '_';
	DOT = '.';
}

@header {
	import org.annares.cpp.*;
}
@members {
	public static void main(String[] args) throws Exception {
		Preprocessor pp = new Preprocessor();
		pp.addWarning(Warning.IMPORT);
		pp.setListener(new PreprocessorListener());
		pp.addInput(new FileLexerSource(new File("~/code/isno/isno.dme")));

		if (pp.getFeature(Feature.VERBOSE)) {
			System.err.println("#"+"include \"...\" search starts here:");
			for (String dir : pp.getQuoteIncludePath())
				System.err.println("  " + dir);
			System.err.println("#"+"include <...> search starts here:");
			for (String dir : pp.getSystemIncludePath())
				System.err.println("  " + dir);
			System.err.println("End of search list.");
		}

		try {
			for (;;) {
				Token tok = pp.token();
				if (tok == null)
					break;
				if (tok.getType() == Token.EOF)
					break;
				System.out.print(tok.getText());
			}
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			Source	s = pp.getSource();
			while (s != null) {
				System.err.println(" -> " + s);
				s = s.getParent();
			}
		}

		DMLexer lex = new DMLexer(new ANTLRFileStream(args[0]));
		CommonTokenStream tokens = new CommonTokenStream(lex);

		DMParser parser = new DMParser(tokens);

		try {
			parser.expr();
		} catch (RecognitionException e) {
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

COMMENT: SLASH SLASH ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
       | SLASH STAR ( options {greedy=false;} : . )* STAR SLASH {$channel=HIDDEN;}
    ;

fragment DIGIT: '0'..'9';
fragment LETTER: 'a'..'z' | 'A'..'Z';
fragment EXPONENT: ('e'|'E') (PLUS | MINUS)? (DIGIT)+;
fragment HEX_DIGIT : (DIGIT|'a'..'f'|'A'..'F');
