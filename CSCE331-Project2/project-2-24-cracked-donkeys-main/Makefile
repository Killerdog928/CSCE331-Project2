all: doc build

doc:
	javadoc --source-path src -d doc src/App.java

build:
	javac --source-path src -d out src/App.java -Werror

run: build
	java -cp out -p vendor App
