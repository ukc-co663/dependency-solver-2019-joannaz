#!/bin/bash
rm -rf lib/*
mkdir -p lib
wget -O lib/fastjson-1.2.45.jar http://search.maven.org/remotecontent?filepath=com/alibaba/fastjson/1.2.45/fastjson-1.2.45.jar
wget -O lib/logicng-1.4.1.jar http://central.maven.org/maven2/org/logicng/logicng/1.4.1/logicng-1.4.1.jar
wget -O lib/ guava-27.0.1-jre-javadoc.jar http://central.maven.org/maven2/com/google/guava/guava/27.0.1-jre/guava-27.0.1-jre-javadoc.jar