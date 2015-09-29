
#!/bin/bash

dmem=$1
emem=$2
QUEUE=$3
inputFile=$4
k=$5
iterations=$6


$SPARK_HOME/bin/spark-submit --class com.rahulswaminathan.yarnapplicationstatistics.JavaKMeans --master yarn-cluster --num-executors 4 --driver-memory $dmem --executor-memory $emem --executor-cores 4 --queue $QUEUE target/yarnapplicationstatistics-1.0-SNAPSHOT-jar-with-dependencies.jar $inputFile $k $iterations


