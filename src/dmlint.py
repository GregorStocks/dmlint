import sys
import traceback

from grammar.DMLexer import DMLexer
from grammar.DMParser import DMParser
from antlr3 import ANTLRInputStream, CommonTokenStream
from preprocessor import PreprocessedFile

def main(filename):
	char_stream = ANTLRInputStream(PreprocessedFile(filename))
	lexer = DMLexer(char_stream)
	tokens = CommonTokenStream(lexer)
	parser = DMParser(tokens);

	try:
		parser.expr()
	except RecognitionException:
		traceback.print_stack()

if len(sys.argv) > 1:
	main(sys.argv[1])
else:
	main('C:/Users/me/code/isno/isno.dme')
