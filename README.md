yarnapplicationstatistics
=========================

Logging to SQL:

This project provides the user the opportunity to see logs in mySQL from the YARN RM as well as from the application itself. When writing an application, the user can instantiate an ApplicationLogger object and use the api to send count and gauge messages to StatsD. These messages are sent to mySQL by the SQLWriter. Internally, the 3 daemons (ClusterMetrics, SchedulerMetrics, and MonitorApplications) automatically write to mySQL tables. The user controls what metrics are written using counts.properties and gauges.properties. Once they add a count or gauge, they must update this file with the table name and the count/gauge. 

How to execute:

Install Hadoop and Spark as detailed below and test using an example job. Launch the daemons using the launch_daemons script. Launch the SQLWriter using the launch_sql_writer script. Run any job and watch as the metrics are collected in mySQL!. The sql_test script is a good tool for testing..it prints out the tables as they are written.

To execute:

Use 'run_metrics.sh' which builds the project using Maven and executes the resultant jar. The script run_spark_pi executes the SparkPi job using the $SPARK_HOME environment variable. The inputs are driver memory, executer memory, and queue. The run_spark_pi script is used to start the SparkPi job in a new JVM in the GetYarnMetrics class. The main class is found in GetYarnMetrics. The other classes are wrappers for JSON parsing. The YARN conf files are copied into this repository.

The data text files in the repo are named as follows: data_$dmem_$emem_$numIterations_$QUEUE1_$QUEUE2.

Hadoop and Spark Installation:

We are currently using Hadoop 2.4.0 due to compatability issues with later versions of Hadoop and Spark. Install Hadoop 2.4.0 and then change the configuration files in etc/hadoop to the ones found in the hadoop_files directory of this project. The web url for hadoop will be running on port 8088. Start hadoop using sbin/start-dfs.sh and sbin/start-yarn.sh and make sure that the installation is correct by running an example job. 

Any version of spark that is packaged with yarn can be used. On the cluster we are using Spark 1.0.1, on my local machine I am using Spark 1.1.0. The configuration files for Spark can be found in the spark_files directory in this project. Make sure the contents of these files match the files found in the conf/ directory of the Spark installation. Run an example job to make sure that Spark is configured properly.



