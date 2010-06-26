// $ANTLR 3.2 Sep 23, 2009 12:02:23 src/grammar/DM.g 2010-06-26 09:41:50

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class DMParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "PLUS", "MINUS", "MULT", "DIV", "NUMBER", "DIGIT", "WHITESPACE"
    };
    public static final int NUMBER=8;
    public static final int WHITESPACE=10;
    public static final int PLUS=4;
    public static final int DIGIT=9;
    public static final int MINUS=5;
    public static final int MULT=6;
    public static final int DIV=7;
    public static final int EOF=-1;

    // delegates
    // delegators


        public DMParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public DMParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return DMParser.tokenNames; }
    public String getGrammarFileName() { return "src/grammar/DM.g"; }


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



    // $ANTLR start "expr"
    // src/grammar/DM.g:29:1: expr : term ( ( PLUS | MINUS ) term )* ;
    public final void expr() throws RecognitionException {
        try {
            // src/grammar/DM.g:29:6: ( term ( ( PLUS | MINUS ) term )* )
            // src/grammar/DM.g:29:8: term ( ( PLUS | MINUS ) term )*
            {
            pushFollow(FOLLOW_term_in_expr60);
            term();

            state._fsp--;

            // src/grammar/DM.g:29:13: ( ( PLUS | MINUS ) term )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>=PLUS && LA1_0<=MINUS)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // src/grammar/DM.g:29:15: ( PLUS | MINUS ) term
            	    {
            	    if ( (input.LA(1)>=PLUS && input.LA(1)<=MINUS) ) {
            	        input.consume();
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_term_in_expr75);
            	    term();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "expr"


    // $ANTLR start "term"
    // src/grammar/DM.g:31:1: term : factor ( ( MULT | DIV ) factor )* ;
    public final void term() throws RecognitionException {
        try {
            // src/grammar/DM.g:31:6: ( factor ( ( MULT | DIV ) factor )* )
            // src/grammar/DM.g:31:8: factor ( ( MULT | DIV ) factor )*
            {
            pushFollow(FOLLOW_factor_in_term87);
            factor();

            state._fsp--;

            // src/grammar/DM.g:31:15: ( ( MULT | DIV ) factor )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=MULT && LA2_0<=DIV)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // src/grammar/DM.g:31:17: ( MULT | DIV ) factor
            	    {
            	    if ( (input.LA(1)>=MULT && input.LA(1)<=DIV) ) {
            	        input.consume();
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_factor_in_term101);
            	    factor();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "term"


    // $ANTLR start "factor"
    // src/grammar/DM.g:33:1: factor : NUMBER ;
    public final void factor() throws RecognitionException {
        try {
            // src/grammar/DM.g:33:8: ( NUMBER )
            // src/grammar/DM.g:33:10: NUMBER
            {
            match(input,NUMBER,FOLLOW_NUMBER_in_factor113); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "factor"

    // Delegated rules


 

    public static final BitSet FOLLOW_term_in_expr60 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_set_in_expr64 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_term_in_expr75 = new BitSet(new long[]{0x0000000000000032L});
    public static final BitSet FOLLOW_factor_in_term87 = new BitSet(new long[]{0x00000000000000C2L});
    public static final BitSet FOLLOW_set_in_term91 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_factor_in_term101 = new BitSet(new long[]{0x00000000000000C2L});
    public static final BitSet FOLLOW_NUMBER_in_factor113 = new BitSet(new long[]{0x0000000000000002L});

}