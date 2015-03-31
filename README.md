yarnapplicationstatistics
=========================

To execute:

Use 'run_metrics.sh' which builds the project using Maven and executes the resultant jar. The script run_spark_pi executes the SparkPi job using the $SPARK_HOME environment variable. The inputs are driver memory, executer memory, and queue. The run_spark_pi script is used to start the SparkPi job in a new JVM in the GetYarnMetrics class. The main class is found in GetYarnMetrics. The other classes are wrappers for JSON parsing. The YARN conf files are copied into this repository.

The data text files in the repo are named as follows: data_$dmem_$emem_$numIterations_$QUEUE1_$QUEUE2.

Hadoop and Spark Installation:

We are currently using Hadoop 2.4.0 due to compatability issues with later versions of Hadoop and Spark. Install Hadoop 2.4.0 and then change the configuration files in etc/hadoop to the ones found in the hadoop_files directory of this project. The web url for hadoop will be running on port 8088. Start hadoop using sbin/start-dfs.sh and sbin/start-yarn.sh and make sure that the installation is correct by running an example job. 

Any version of spark that is packaged with yarn can be used. On the cluster we are using Spark 1.0.1, on my local machine I am using Spark 1.1.0. The configuration files for Spark can be found in the spark_files directory in this project. Make sure the contents of these files match the files found in the conf/ directory of the Spark installation. Run an example job to make sure that Spark is configured properly.



