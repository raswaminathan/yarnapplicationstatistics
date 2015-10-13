package com.rahulswaminathan.yarnapplicationstatistics;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Created by rahulswaminathan on 1/30/15.
 */
public class ClusterMetricsDaemon {

    /**
     * Daemon that uses the RM rest api to get information pertaining to cluster metrics. The daemon is started using the
     * run method which launches the listener in a new thread. Information is sent to statsd using the logging api.
     */
    public ClusterMetricsDaemon() {

    }

    public void run() {
        Runnable run = new ClusterMetricsThread();
        new Thread(run).start();
    }
}

class ClusterMetricsThread implements Runnable {

    private static final String ALLOCATED_MB = "allocatedMB";
    private static final String APPS_COMPLETED = "appsCompleted";
    private static final String APPS_SUBMITTED = "appsSubmitted";
    private static final String APPS_RUNNING = "appsRunning";
    private static final String AVAILABLE_MB = "availableMB";
    private static final String ACTIVE_NODES = "activeNodes";
    private static final String TOTAL_NODES = "totalNodes";
    private static final String APPS_FAILED = "appsFailed";
    private static final String CONTAINERS_ALLOCATED = "containersAllocated";
    private static final String TIMESTAMP = "Current Time Stamp (millis)";
    private volatile boolean running = true;
    private static int WAIT_TIME = 1000;
    private StatsDLogger logger;
    private static final String CLUSTER_METRICS_FILENAME = "cluster_metrics_stats.txt";

    public ClusterMetricsThread() {
        logger = new StatsDLogger();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(CLUSTER_METRICS_FILENAME, true));
            writer.write(TIMESTAMP + " " + ALLOCATED_MB + " " + APPS_COMPLETED + " " + APPS_SUBMITTED + " " +
                    APPS_RUNNING + " " + AVAILABLE_MB + " " + ACTIVE_NODES + " " +
                    TOTAL_NODES + " " + APPS_FAILED + " " + CONTAINERS_ALLOCATED);
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateSpaces(int length) {
        StringBuilder toReturn = new StringBuilder();

        for (int i = 0; i<length; i++) {
            toReturn.append(" ");
        }
        return toReturn.toString();
    }

    public void run() {

        PropsParser pp = new PropsParser();
        String url = "http://" + pp.getYarnWEBUI() + "/ws/v1/cluster/metrics";
        HttpGetHandler hgh = new HttpGetHandler(url);
        System.out.println("Cluster metrics daemon is running");
        while (running) {
            try {
                Thread.sleep(WAIT_TIME);
                String clusterMetricsResponse = hgh.sendGet();
                ObjectMapper mapper = new ObjectMapper();
                ClusterMetrics metrics = mapper.readValue(clusterMetricsResponse, ClusterMetrics.class);

                String timeStamp = Long.toString(System.currentTimeMillis());
                int allocatedMB = (int) metrics.getClusterMetrics().getAllocatedMB();
                int appsCompleted = metrics.getClusterMetrics().getAppsCompleted();
                int appsSubmitted = metrics.getClusterMetrics().getAppsSubmitted();
                int appsRunning = metrics.getClusterMetrics().getAppsRunning();
                int availableMB = (int) metrics.getClusterMetrics().getAvailableMB();
                int activeNodes = metrics.getClusterMetrics().getActiveNodes();
                int totalNodes = metrics.getClusterMetrics().getTotalNodes();
                int appsFailed = metrics.getClusterMetrics().getAppsFailed();
                int containersAllocated = metrics.getClusterMetrics().getContainersAllocated();

                String time_space = generateSpaces(TIMESTAMP.length() - timeStamp.length() + 1);
                String amb_space = generateSpaces(ALLOCATED_MB.length() - Integer.toString(allocatedMB).length() + 1);
                String ac_space = generateSpaces(APPS_COMPLETED.length() - Integer.toString(appsCompleted).length() + 1);
                String as_space = generateSpaces(APPS_SUBMITTED.length() - Integer.toString(appsSubmitted).length() + 1);
                String ar_space = generateSpaces(APPS_RUNNING.length() - Integer.toString(appsRunning).length() + 1);
                String avmb_space = generateSpaces(AVAILABLE_MB.length() - Integer.toString(availableMB).length() + 1);
                String anodes_space = generateSpaces(ACTIVE_NODES.length() - Integer.toString(activeNodes).length() + 1);
                String tnodes_space = generateSpaces(TOTAL_NODES.length() - Integer.toString(totalNodes).length() + 1);
                String af_space = generateSpaces(APPS_FAILED.length() - Integer.toString(appsFailed).length() + 1);

                StringBuilder stringToWrite = new StringBuilder();
                stringToWrite.append(timeStamp + time_space + allocatedMB + amb_space + appsCompleted + ac_space + appsSubmitted + as_space +
                        appsRunning + ar_space + availableMB + avmb_space + activeNodes + anodes_space +
                        totalNodes + tnodes_space + appsFailed + af_space + containersAllocated);

                logger.logGauge(ALLOCATED_MB, allocatedMB);
                logger.logGauge(APPS_COMPLETED, appsCompleted);
                logger.logGauge(APPS_SUBMITTED, appsSubmitted);
                logger.logGauge(APPS_RUNNING, appsRunning);
                logger.logGauge(AVAILABLE_MB, availableMB);
                logger.logGauge(ACTIVE_NODES, activeNodes);
                logger.logGauge(TOTAL_NODES, totalNodes);
                logger.logGauge(APPS_FAILED, appsFailed);
                logger.logGauge(CONTAINERS_ALLOCATED, containersAllocated);

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(CLUSTER_METRICS_FILENAME, true));
                    writer.write(stringToWrite.toString());
                    writer.newLine();
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("while loop in cluster metrics daemon exited for some reason");
    }
}