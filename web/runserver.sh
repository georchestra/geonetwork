#!/bin/sh
set -x


export MAVEN_OPTS="-XX:CompileCommand=exclude,net/sf/saxon/event/ReceivingContentHandler.startElement"
export MAVEN_OPTS="-Xrunjdwp:transport=dt_socket,address=8001,suspend=n,server=y -XX:PermSize=256m -Xmx512m $MAVEN_OPTS"
export MAVEN_OPTS="-noverify -javaagent:$JREBEL_HOME/jrebel.jar $MAVEN_OPTS"

mvn jetty:run -Dserver=$1
