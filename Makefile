default: grammar

antlr:
	java -cp lib/antlrworks-1.4.jar org.antlr.Tool -o src/grammar/ src/grammar/DM.g

grammar:
	javac -d bin -cp lib/antlr-runtime-3.2.jar src/grammar/*.java
clean:
	rm -rf bin
