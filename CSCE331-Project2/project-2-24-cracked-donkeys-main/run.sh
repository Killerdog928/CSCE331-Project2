#!/bin/bash

javac --source-path src -d out src/App.java -Werror
java -cp out -p vendor App