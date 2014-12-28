#!/bin/bash  


rm -r target/
mvn install

java -cp target/yarnapplicationstatistics-1.0-SNAPSHOT.jar:target/dependency-jars/*:jars_needed/*:$HADOOP_CONF_DIR/yarn-site.xml:$SPARK_HOME/lib/spark-assembly-1.1.0-hadoop2.4.0.jar com.rahulswaminathan.yarnapplicationstatistics.GetYarnMetrics
