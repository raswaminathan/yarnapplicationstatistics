


#!/bin/bash

dmem=$1
emem=$2
QUEUE=$3

$SPARK_HOME/bin/spark-submit --class com.rahulswaminathan.yarnapplicationstatistics.JavaSparkPi --master yarn-cluster --num-executors 4 --driver-memory $dmem --executor-memory $emem --executor-cores 4 --queue $QUEUE target/yarnapplicationstatistics-1.0-SNAPSHOT-jar-with-dependencies.jar 1


#blank space
