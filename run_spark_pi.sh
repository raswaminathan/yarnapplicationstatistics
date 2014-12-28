#!/bin/bash

dmem=$1
emem=$2
QUEUE=$3


$SPARK_HOME/bin/spark-submit --class org.apache.spark.examples.JavaWordCount --master yarn-cluster --num-executors 1 --driver-memory 1g --executor-memory 1g --executor-cores 1 --queue a $SPARK_HOME/lib/spark-examples*.jar ~/BigData/README.md
$SPARK_HOME/bin/spark-submit --class org.apache.spark.examples.SparkPi --master spark://127.0.0.1:7077 --num-executors 1 --driver-memory 1g --executor-memory 1g --executor-cores 1 --queue a $SPARK_HOME/lib/spark-examples*.jar 1
$SPARK_HOME/bin/spark-submit --class org.apache.spark.examples.SparkPi --master yarn-cluster --num-executors 1 --driver-memory $dmem --executor-memory $emem --executor-cores 1 --queue $QUEUE $SPARK_HOME/lib/spark-examples*.jar 1