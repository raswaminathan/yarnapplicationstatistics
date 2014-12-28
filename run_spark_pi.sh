#!/bin/bash

dmem=$1
emem=$2
QUEUE=$3

println "Running Spark job in " + $QUEUE

$SPARK_HOME/bin/spark-submit --class org.apache.spark.examples.SparkPi --master yarn-cluster --num-executors 1 --driver-memory $dmem --executor-memory $emem --executor-cores 1 --queue $QUEUE $SPARK_HOME/examples/target/scala-2.10/spark-examples*.jar 10
