package com.buttware.dmlint;
import com.buttware.dmlint.grammar.*;
import org.antlr.runtime.*;

class DMLint {
    public static void main(String[] args) throws Exception {
		PreprocessedFileStream f = new PreprocessedFileStream(args[0]);

		//System.out.println(f.getString());
        DMLexer lex = new DMLexer(f);
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
