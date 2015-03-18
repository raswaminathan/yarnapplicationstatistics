package com.rahulswaminathan.yarnapplicationstatistics;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.Random;

/**
 * Created by rahulswaminathan on 1/30/15.
 */
public class ClusterMetricsDaemon {

    public ClusterMetricsDaemon() {

    }

    public void run() {
        Runnable run = new ClusterMetricsThread();
        new Thread(run).start();
    }
}

class ClusterMetricsThread implements Runnable {

    private volatile boolean running = true;
    private static int WAIT_TIME = 10000;

    public ClusterMetricsThread() {
    }


    public void run() {

        PropsParser pp = new PropsParser();
        String url = "http://" + pp.getYarnWEBUI() + "/ws/v1/cluster/metrics";
        HttpGetHandler hgh = new HttpGetHandler(url);
        while (running) {
            try {
                Thread.sleep(WAIT_TIME);
                String clusterMetricsResponse = hgh.sendGet();
                ObjectMapper mapper = new ObjectMapper();
                ClusterMetrics metrics = mapper.readValue(clusterMetricsResponse, ClusterMetrics.class);
                System.out.println(clusterMetricsResponse);
                /// SHOULD POST MESSAGES TO KAFKA

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}