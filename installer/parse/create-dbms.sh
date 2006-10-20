#!/bin/sh

cd ../web/WEB-INF/db
export LIB=../lib

java -jar ${LIB}mckoidb.jar -create $JDBC_USER $JDBC_PASSWORD
