default: antlr

antlr: src/grammar/DM.g
	java -cp lib/antlr-3.1.2.jar org.antlr.Tool src/grammar/DM.g

clean:
	rm -rf bin
	rm -rf src/grammar/*.py
