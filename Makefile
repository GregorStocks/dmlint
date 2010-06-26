default: grammar

antlr: src/grammar/DM.g
	java -cp lib/antlrworks-1.4.jar org.antlr.Tool -o src/grammar/ src/grammar/DM.g

grammar: antlr
	javac -d bin -cp lib/antlr-runtime-3.2.jar:lib/annares-cpp.jar src/grammar/*.java
clean:
	rm -rf bin
