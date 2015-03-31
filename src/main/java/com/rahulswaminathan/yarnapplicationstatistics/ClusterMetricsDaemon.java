package com.rahulswaminathan.yarnapplicationstatistics;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Random;

/**
 * Created by rahulswaminathan on 1/30/15.
 */
public class ClusterMetricsDaemon {

    private static final String PREFIX = "my.prefix";
    private static final String SERVER_LOCATION = "localhost";
    private static final int PORT = 8125;
    StatsDClient statsd;

    public ClusterMetricsDaemon() {
        statsd = new NonBlockingStatsDClient(PREFIX,
                SERVER_LOCATION, PORT);
    }

    public void run() {
        Runnable run = new ClusterMetricsThread(statsd);
        new Thread(run).start();
    }
}

class ClusterMetricsThread implements Runnable {

    private volatile boolean running = true;
    private static int WAIT_TIME = 10000;
    StatsDClient statsd;

    public ClusterMetricsThread(StatsDClient statsdd) {
        this.statsd = statsdd;
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

                //System.out.println(clusterMetricsResponse);

                statsd.recordGaugeValue("allocatedMB", metrics.getClusterMetrics().getAllocatedMB());
                /// SHOULD POST MESSAGES TO KAFKA

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("while loop in cluster metrics daemon exited for some reason");
    }
}