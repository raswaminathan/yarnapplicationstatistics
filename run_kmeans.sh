
#!/bin/bash

num_executors=$1
emem=$2
executor_cores=$3

inputFile=$4
k=$5
iterations=$6


$SPARK_HOME/bin/spark-submit --class com.rahulswaminathan.yarnapplicationstatistics.JavaKMeans --master yarn-cluster --num-executors $num_executors  --executor-memory $emem --executor-cores $executor_cores target/yarnapplicationstatistics-1.0-SNAPSHOT-jar-with-dependencies.jar $inputFile $k $iterations

