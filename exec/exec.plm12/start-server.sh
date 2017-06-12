#!/bin/bash
JAR=exec.plm12-0.0.1-SNAPSHOT-jar-with-dependencies.jar

# querytype
# feature-comparison
# f1-and-size
# definition-sites
# f1-for-input
# f1-for-input-several

SELECTOR=querytype
SERVERIP=localhost
DATASET=5700 
java -Xmx4g -jar $JAR $SELECTOR $SERVERIP $DATASET

