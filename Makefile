default: antlr

antlr: src/grammar/DM.g
	cd src/grammar & \
	java -cp ../../lib/antlr-3.1.2.jar org.antlr.Tool -o ../grammar-genfiles/ DM.g

clean:
	rm -rf bin
	rm -rf src/grammar-genfiles
