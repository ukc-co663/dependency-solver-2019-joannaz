#!/bin/bash
rm -rf lib/*
mkdir -p lib
sudo wget -O lib/fastjson-1.2.45.jar http://search.maven.org/remotecontent?filepath=com/alibaba/fastjson/1.2.45/fastjson-1.2.45.jar
sudo wget -O lib/logicng-1.4.1.jar http://central.maven.org/maven2/org/logicng/logicng/1.4.1/logicng-1.4.1.jar
sudo wget -O lib/guava-25.1-jre.jar http://central.maven.org/maven2/com/google/guava/guava/25.1-jre/guava-25.1-jre.jar
sudo wget -O lib/antlr4-runtime-4.7.jar http://central.maven.org/maven2/org/antlr/antlr4-runtime/4.7/antlr4-runtime-4.7.jar
