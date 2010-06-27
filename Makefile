default: antlr

antlr: src/grammar/DM.g
	java -cp lib/antlrworks-1.4.jar org.antlr.Tool src/grammar/DM.g

poop: antlr
	javac -cp "lib/antlrworks-1.4.jar" -d bin/ src/grammar/*.java

clean:
	rm -rf bin
	rm -rf src/grammar/*.py
