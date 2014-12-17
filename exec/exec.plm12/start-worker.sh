#!/bin/bash
JAR=exec.plm12-0.0.1-SNAPSHOT-jar-with-dependencies.jar
IP=localhost
java -Xmx2g -jar $JAR worker $IP

