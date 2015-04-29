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

Running the Graphing package:

We are using Python 2.7.2 to graph the data that Spark gets. Assuming you have nothing installed, and you are on Mac OSX with Homebrew installed, run the following commands:

brew install python
sudo pip install MySQL-python

To verify that MySQLdb is installed correctly, run the following commands:

python
import MySQLdb

If you get an error indicating that MySQLdb was not found, then you need to edit your PATH variable to set your /usr/local/bin before your /usr/bin. This will change your path so your Homebrew python is the primary source of your python, which is what pip uses when it installs your packages. Restart your Terminal and try the python and import statements again; you should not receive any errors.

Run the following commands in Terminal:

brew install pkg-config
pip install matplotlib

To run the grapher, type in the following command into Termainl:

python Grapher.py

This will run the graphing script indefinitely. Ctrl c will stop the graph from updating, and once you close the graph, the python program will terminate.

Using the Grapher:

To use the grapher, open graphProperties.ini. Here you will find the different properties that you need to utilize the graphing tool. Make sure all of the MySQL information allows you to access the MySQL table that StatsD is writing to, and that you have the desired data tag entered. In addition, you can change the number of datapoints that the graph remembers, or the frequency of how often it pulls from the database.