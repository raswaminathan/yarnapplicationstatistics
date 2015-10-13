package com.rahulswaminathan.yarnapplicationstatistics;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Created by rahulswaminathan on 1/30/15.
 */
public class SchedulerDaemon {

    /**
     * A daemon that gathers information about the scheduler from the RM rest api. The run method launches a new thread
     * that gathers information periodically and posts messages to statsd using the logging api.
     */
    public SchedulerDaemon() {

    }

    public void run() {
        Runnable run = new SchedulerThread();
        new Thread(run).start();
    }
}

class SchedulerThread implements Runnable {

    private static final String TOTAL_CONTAINERS = "totalContainers";
    private static final String TOTAL_ACTIVE_APPLICATIONS = "totalActiveApplications";
    private static final String TOTAL_APPLICATIONS = "totalApplications";
    private static final String MAX_APPLICATIONS = "maxApplications";
    private static final String TIMESTAMP = "Current Time Stamp";
    private static final String SCHEDULER_METRICS_FILENAME = "scheduler_metrics.txt";

    private volatile boolean running = true;
    private static int WAIT_TIME = 1000;
    private StatsDLogger logger;

    public SchedulerThread() {
        logger = new StatsDLogger();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(SCHEDULER_METRICS_FILENAME, true));
            writer.write(TIMESTAMP + " " + TOTAL_CONTAINERS + " " + TOTAL_ACTIVE_APPLICATIONS + " " + TOTAL_APPLICATIONS + " " +
                    MAX_APPLICATIONS);
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
        String url = "http://" + pp.getYarnWEBUI() + "/ws/v1/cluster/scheduler";
        HttpGetHandler hgh = new HttpGetHandler(url);
        System.out.println("scheduler daemon is running");

        while (running) {
            try {
                Thread.sleep(WAIT_TIME);
                String schedulerResponse = hgh.sendGet();
                Scheduler.queue[] list = readClusterSchedulerJsonResponse(schedulerResponse);

                String timeStamp = Long.toString(System.currentTimeMillis());
                int totalContainers = getTotalContainers(list);
                int totalActiveApplications = getTotalActiveApplications(list);
                int totalApplications = getTotalApplications(list);
                int maxApplications = getMaxApplications(list);

                String time_space = generateSpaces(TIMESTAMP.length() - timeStamp.length() + 1);
                String tc_space = generateSpaces(TOTAL_CONTAINERS.length() - Integer.toString(totalContainers).length() + 1);
                String taa_space = generateSpaces(TOTAL_ACTIVE_APPLICATIONS.length() - Integer.toString(totalActiveApplications).length() + 1);
                String ta_space = generateSpaces(TOTAL_APPLICATIONS.length() - Integer.toString(totalApplications).length() + 1);

                StringBuilder stringToWrite = new StringBuilder();
                stringToWrite.append(timeStamp + time_space + totalContainers + tc_space + totalActiveApplications +
                        taa_space + totalApplications + ta_space + maxApplications);

                logger.logGauge(TOTAL_CONTAINERS, totalContainers);
                logger.logGauge(TOTAL_ACTIVE_APPLICATIONS, totalActiveApplications);
                logger.logGauge(TOTAL_APPLICATIONS, totalApplications);
                logger.logGauge(MAX_APPLICATIONS, maxApplications);

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(SCHEDULER_METRICS_FILENAME, true));
                    writer.write(stringToWrite.toString());
                    writer.newLine();
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //System.out.println(schedulerResponse);
                /// SHOULD POST MESSAGES TO KAFKA

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Scheduler.queue[] readClusterSchedulerJsonResponse(String clusterSchedulerResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonNode node = mapper.readTree(clusterSchedulerResponse);
        node = node.get("scheduler").get("schedulerInfo").get("queues").get("queue");
        TypeReference<Scheduler.queue[]> typeRef = new TypeReference<Scheduler.queue[]>() {};
        return mapper.readValue(node.traverse(), typeRef);
    }

    private int getTotalContainers(Scheduler.queue[] list) {
        int totalContainers = 0;
        for (Scheduler.queue q : list) {
            totalContainers += q.getNumContainers();
        }
        return totalContainers;
    }

    private int getTotalActiveApplications(Scheduler.queue[] list) {
        int totalActiveApps = 0;
        for (Scheduler.queue q : list) {
            totalActiveApps += q.getNumActiveApplications();
        }
        return totalActiveApps;
    }

    private int getTotalApplications(Scheduler.queue[] list) {
        int totalApps = 0;
        for (Scheduler.queue q : list) {
            totalApps += q.getNumApplications();
        }
        return totalApps;
    }

    private int getMaxApplications(Scheduler.queue[] list) {
        int maxApps = 0;
        for (Scheduler.queue q : list) {
            maxApps += q.getMaxApplications();
        }
        return maxApps;
    }
}