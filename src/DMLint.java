package com.buttware.dmlint;
import com.buttware.dmlint.grammar.DMLexer;
import org.antlr.runtime.*;

class DMLint {
    public static void main(String[] args) throws Exception {
        DMLexer lex = new DMLexer(new ANTLRFileStream(args[0]));
       	CommonTokenStream tokens = new CommonTokenStream(lex);

		// run the lexer - remove when running parser too
		tokens.getTokens();

        /*DMParser parser = new DMParser(tokens);

        try {
            parser.expr();
        } catch (RecognitionException e)  {
            e.printStackTrace();
        }*/
    }
}
