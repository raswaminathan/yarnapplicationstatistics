yarnapplicationstatistics
=========================

To execute:

Use 'run_metrics.sh' which builds the project using Maven and executes the resultant jar. The script run_spark_pi executes the SparkPi job using the $SPARK_HOME environment variable. The inputs are driver memory, executer memory, and queue. The run_spark_pi script is used to start the SparkPi job in a new JVM in the GetYarnMetrics class. The main class is found in GetYarnMetrics. The other classes are wrappers for JSON parsing.

The data text files in the repo are named as follows: data_$dmem_$emem_$QUEUE1_$QUEUE2.

