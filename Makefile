default: grammar

antlr: src/grammar/DM.g
	java -cp lib/antlrworks-1.4.jar org.antlr.Tool src/grammar/DM.g -o src/gen/grammar

grammar: antlr
	javac -cp "lib/antlrworks-1.4.jar" -d bin/ src/gen/grammar/*.java src/*.java

clean:
	rm -rf bin
	rm -rf src/gen
