package com.rahulswaminathan.yarnapplicationstatistics;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.codehaus.jackson.map.ObjectMapper;

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

    private volatile boolean running = true;
    private static int WAIT_TIME = 1000;
    private StatsDLogger logger;

    public ClusterMetricsThread() {
        logger = new StatsDLogger();
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

                logger.logGauge("allocatedMB", (int) metrics.getClusterMetrics().getAllocatedMB());
                logger.logGauge("appsCompleted", metrics.getClusterMetrics().getAppsCompleted());
                logger.logGauge("appsSubmitted", metrics.getClusterMetrics().getAppsSubmitted());
                logger.logGauge("appsRunning", metrics.getClusterMetrics().getAppsRunning());
                logger.logGauge("availableMB", (int) metrics.getClusterMetrics().getAvailableMB());
                logger.logGauge("activeNodes", metrics.getClusterMetrics().getActiveNodes());
                logger.logGauge("totalNodes", metrics.getClusterMetrics().getTotalNodes());
                logger.logGauge("appsFailed", metrics.getClusterMetrics().getAppsFailed());
                logger.logGauge("containersAllocated", metrics.getClusterMetrics().getContainersAllocated());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("while loop in cluster metrics daemon exited for some reason");
    }
}