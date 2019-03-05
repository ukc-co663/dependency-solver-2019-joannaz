#!/bin/bash
rm -rf lib/*
mkdir -p lib
wget -O lib/fastjson-1.2.45.jar http://search.maven.org/remotecontent?filepath=com/alibaba/fastjson/1.2.45/fastjson-1.2.45.jar
wget -O lib/logicng-1.4.1.jar http://central.maven.org/maven2/org/logicng/logicng/1.4.1/logicng-1.4.1.jar
wget -O lib/guava-25.1-jre.jar http://central.maven.org/maven2/com/google/guava/guava/25.1-jre/guava-25.1-jre.jar
wget -O lib/antlr4-runtime-4.7.jar http://central.maven.org/maven2/org/antlr/antlr4-runtime/4.7/antlr4-runtime-4.7.jar
wget -O lib/jgrapht-jdk1.5-0.7.3.jar http://central.maven.org/maven2/org/jgrapht/jgrapht-jdk1.5/0.7.3/jgrapht-jdk1.5-0.7.3.jar