#!/bin/bash  

mvn install

java -cp target/yarnapplicationstatistics-1.0-SNAPSHOT.jar:target/dependency-jars/* com.rahulswaminathan.yarnapplicationstatistics.GetYarnMetrics
