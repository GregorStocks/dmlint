import sys
import traceback

from grammar.DMLexer import DMLexer
from grammar.DMParser import DMParser
from antlr3 import ANTLRInputStream, CommonTokenStream
from preprocessor import PreprocessedFile

def main(filename, just_preprocess = True, parse_it = False):
	preprocessed = PreprocessedFile(filename)
	if just_preprocess:
		print preprocessed.read()
		return
	char_stream = ANTLRInputStream(preprocessed)
	lexer = DMLexer(char_stream)
	tokens = CommonTokenStream(lexer)
	if not parse_it:
		tokens.fillBuffer()
		return

	parser = DMParser(tokens);

	try:
		parser.expr()
	except RecognitionException:
		traceback.print_stack()

if len(sys.argv) > 1:
	main(sys.argv[1], False)
else: 
	main('C:/Users/me/code/openss13/trunk/spacestation13/spacestation13.dme', False)
