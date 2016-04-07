package com.rahulswaminathan.yarnapplicationstatistics;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.avro.data.Json;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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
    public static final String FAIR_SCHEDULER_FILENAME = "fair_scheduler_metrics.txt";
    private SQLWrapper mySqlWrapper;
    private static final String DATABASE_NAME = "test";
    private static final String MYSQL_USERNAME = "root";
    private static final String SERVER_LOCATION = "localhost";
    private boolean isInitialized = false;
    Set<String> parentQueueTables = new HashSet<String>();
    Set<String> leafQueueTables = new HashSet<String>();

    private volatile boolean running = true;
    private static int WAIT_TIME = 1000;
    private StatsDLogger logger;

    public SchedulerThread() {
        logger = new StatsDLogger();
        mySqlWrapper = new SQLWrapper(DATABASE_NAME , SERVER_LOCATION, MYSQL_USERNAME);
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
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FAIR_SCHEDULER_FILENAME, true));
            writer.write("queueName,parentQueue,schedulingPolicy,numPendingApps,numActiveApps,minResources,maxResources,usedResources" +
                    "steadyFairResources,fairResources,clusterResources");
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

                Gson gson = new Gson();
                JsonObject jo = gson.fromJson(schedulerResponse, JsonElement.class).getAsJsonObject();

                if (jo.get("scheduler").getAsJsonObject().get("schedulerInfo").
                        getAsJsonObject().get("type").getAsString().equals("fairScheduler")) {

                    StringBuilder stringToWrite = new StringBuilder();
                  //  stringToWrite.append("queueName,parentQueue,minResources,maxResources,usedResources" +
                //            "steadyFairResources,fairResources,clusterResources");
                    Map<JsonElement, String> queuesToParentsMap = getAllQueuesToParents(jo);
                    for (JsonElement e : queuesToParentsMap.keySet()) {
                        JsonObject obj = e.getAsJsonObject();
                        stringToWrite.append(obj.get("queueName") + ",");
                        stringToWrite.append(queuesToParentsMap.get(e) + ",");
                        stringToWrite.append(obj.get("schedulingPolicy") + ",");
                        if (obj.has("numPendingApps")) {
                            stringToWrite.append(obj.get("numPendingApps") + ",");
                        } else {
                            stringToWrite.append("N/A" + ",");
                        }
                        if (obj.has("numActiveApps")) {
                            stringToWrite.append(obj.get("numActiveApps") + ",");
                        } else {
                            stringToWrite.append("N/A" + ",");
                        }
                        stringToWrite.append(obj.get("minResources") + ",");
                        stringToWrite.append(obj.get("maxResources") + ",");
                        stringToWrite.append(obj.get("usedResources") + ",");
                        stringToWrite.append(obj.get("steadyFairResources") + ",");
                        stringToWrite.append(obj.get("fairResources") + ",");
                        stringToWrite.append(obj.get("clusterResources"));
                        stringToWrite.append("\n");
                    }

                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(FAIR_SCHEDULER_FILENAME, true));
                        writer.write(stringToWrite.toString());
                        writer.newLine();
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {


                    Map<JsonElement, String> queuesToParentsMap = getAllQueuesToParentsForCapacityScheduler(jo);

                    if (!isInitialized) {
                        initializeQueuesTables(queuesToParentsMap);
                        isInitialized = true;
                    } else {
                        for (JsonElement e : queuesToParentsMap.keySet()) {
                            updateQueueInfoInTable(e);
                        }
                    }

                    //Scheduler.queue[] list = readClusterSchedulerJsonResponse(schedulerResponse);

//                    String timeStamp = Long.toString(System.currentTimeMillis());
//                    int totalContainers = getTotalContainers(list);
//                    int totalActiveApplications = getTotalActiveApplications(list);
//                    int totalApplications = getTotalApplications(list);
//                    int maxApplications = getMaxApplications(list);
//
//                    String time_space = generateSpaces(TIMESTAMP.length() - timeStamp.length() + 1);
//                    String tc_space = generateSpaces(TOTAL_CONTAINERS.length() - Integer.toString(totalContainers).length() + 1);
//                    String taa_space = generateSpaces(TOTAL_ACTIVE_APPLICATIONS.length() - Integer.toString(totalActiveApplications).length() + 1);
//                    String ta_space = generateSpaces(TOTAL_APPLICATIONS.length() - Integer.toString(totalApplications).length() + 1);
//
//                    StringBuilder stringToWrite = new StringBuilder();
//                    stringToWrite.append(timeStamp + time_space + totalContainers + tc_space + totalActiveApplications +
//                            taa_space + totalApplications + ta_space + maxApplications);
//
//                    logger.logGauge(TOTAL_CONTAINERS, totalContainers);
//                    logger.logGauge(TOTAL_ACTIVE_APPLICATIONS, totalActiveApplications);
//                    logger.logGauge(TOTAL_APPLICATIONS, totalApplications);
//                    logger.logGauge(MAX_APPLICATIONS, maxApplications);
//
//                    try {
//                        BufferedWriter writer = new BufferedWriter(new FileWriter(SCHEDULER_METRICS_FILENAME, true));
//                        writer.write(stringToWrite.toString());
//                        writer.newLine();
//                        writer.flush();
//                        writer.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
                //System.out.println(schedulerResponse);
                /// SHOULD POST MESSAGES TO KAFKA

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeQueuesTables(Map<JsonElement, String> queuesToParentsMap) {
        for (JsonElement e : queuesToParentsMap.keySet()) {
            JsonObject obj = e.getAsJsonObject();
            String queueName = obj.get("queueName").getAsString();
            if (parentQueueTables.contains(queueName) || leafQueueTables.contains(queueName)) {
                continue;
            }
            if (!mySqlWrapper.createTagValueTable(queueName)) {
                continue;
            };
            // if it has queues it is a parent queue
            if (obj.has("queues")) {
                parentQueueTables.add(queueName);
                try {
                    mySqlWrapper.insertIntoTable(queueName, "capacity", obj.get("capacity").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "maxCapacity", obj.get("maxCapacity").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "numApplications", obj.get("numApplications").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "absoluteCapacity", obj.get("absoluteCapacity").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "absoluteMaxCapacity", obj.get("absoluteMaxCapacity").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "absoluteUsedCapacity", obj.get("absoluteUsedCapacity").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "parentQueueName", queuesToParentsMap.get(e));
                } catch (Exception ex) {

                }
            } else {
                leafQueueTables.add(queueName);
                try {
                    mySqlWrapper.insertIntoTable(queueName, "capacity", obj.get("capacity").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "maxCapacity", obj.get("maxCapacity").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "numApplications", obj.get("numApplications").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "absoluteCapacity", obj.get("absoluteCapacity").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "absoluteMaxCapacity", obj.get("absoluteMaxCapacity").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "absoluteUsedCapacity", obj.get("absoluteUsedCapacity").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "maxApplications", obj.get("maxApplications").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "maxApplicationsPerUser", obj.get("maxApplicationsPerUser").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "numActiveApplications", obj.get("numActiveApplications").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "numContainers", obj.get("numContainers").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "absoluteUsedCapacity", obj.get("absoluteUsedCapacity").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "numPendingApplications", obj.get("numPendingApplications").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "resourcesUsed", obj.get("resourcesUsed").toString());
                    mySqlWrapper.insertIntoTable(queueName, "state", obj.get("state").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "usedCapacity", obj.get("usedCapacity").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "userLimit", obj.get("userLimit").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "users", obj.get("users").toString());
                    mySqlWrapper.insertIntoTable(queueName, "type", obj.get("type").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "userAMResourceLimit", obj.get("userAMResourceLimit").toString());
                    mySqlWrapper.insertIntoTable(queueName, "usedAMResource", obj.get("usedAMResource").toString());
                    mySqlWrapper.insertIntoTable(queueName, "AMResourceLimit", obj.get("AMResourceLimit").toString());
                    mySqlWrapper.insertIntoTable(queueName, "userLimitFactor", obj.get("userLimitFactor").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "preemptionDisabled", obj.get("preemptionDisabled").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "hideReservationQueues", obj.get("hideReservationQueues").getAsString());
                    mySqlWrapper.insertIntoTable(queueName, "nodeLabels", obj.get("nodeLabels").toString());
                    mySqlWrapper.insertIntoTable(queueName, "parentQueueName", queuesToParentsMap.get(e));
                } catch (Exception ex) {

                }
            }
        }
    }

    private boolean updateQueueInfoInTable(JsonElement e) {
        JsonObject obj = e.getAsJsonObject();
        String queueName = obj.get("queueName").getAsString();
        if (parentQueueTables.contains(queueName)) {
            try {
                mySqlWrapper.updateValue(queueName, "capacity", obj.get("capacity").getAsString());
                mySqlWrapper.updateValue(queueName, "maxCapacity", obj.get("maxCapacity").getAsString());
                mySqlWrapper.updateValue(queueName, "numApplications", obj.get("numApplications").getAsString());
                mySqlWrapper.updateValue(queueName, "absoluteCapacity", obj.get("absoluteCapacity").getAsString());
                mySqlWrapper.updateValue(queueName, "absoluteMaxCapacity", obj.get("absoluteMaxCapacity").getAsString());
                mySqlWrapper.updateValue(queueName, "absoluteUsedCapacity", obj.get("absoluteUsedCapacity").getAsString());
            } catch (Exception ex) {

            }
            return true;
        } else if (leafQueueTables.contains(queueName)){
            try {
                mySqlWrapper.updateValue(queueName, "capacity", obj.get("capacity").getAsString());
                mySqlWrapper.updateValue(queueName, "maxCapacity", obj.get("maxCapacity").getAsString());
                mySqlWrapper.updateValue(queueName, "numApplications", obj.get("numApplications").getAsString());
                mySqlWrapper.updateValue(queueName, "absoluteCapacity", obj.get("absoluteCapacity").getAsString());
                mySqlWrapper.updateValue(queueName, "absoluteMaxCapacity", obj.get("absoluteMaxCapacity").getAsString());
                mySqlWrapper.updateValue(queueName, "absoluteUsedCapacity", obj.get("absoluteUsedCapacity").getAsString());
                mySqlWrapper.updateValue(queueName, "maxApplications", obj.get("maxApplications").getAsString());
                mySqlWrapper.updateValue(queueName, "maxApplicationsPerUser", obj.get("maxApplicationsPerUser").getAsString());
                mySqlWrapper.updateValue(queueName, "numActiveApplications", obj.get("numActiveApplications").getAsString());
                mySqlWrapper.updateValue(queueName, "numContainers", obj.get("numContainers").getAsString());
                mySqlWrapper.updateValue(queueName, "absoluteUsedCapacity", obj.get("absoluteUsedCapacity").getAsString());
                mySqlWrapper.updateValue(queueName, "numPendingApplications", obj.get("numPendingApplications").getAsString());
                mySqlWrapper.updateValue(queueName, "resourcesUsed", obj.get("resourcesUsed").toString());
                mySqlWrapper.updateValue(queueName, "state", obj.get("state").getAsString());
                mySqlWrapper.updateValue(queueName, "usedCapacity", obj.get("usedCapacity").getAsString());
                mySqlWrapper.updateValue(queueName, "userLimit", obj.get("userLimit").getAsString());
                mySqlWrapper.updateValue(queueName, "users", obj.get("users").toString());
                mySqlWrapper.updateValue(queueName, "type", obj.get("type").getAsString());
                mySqlWrapper.updateValue(queueName, "userAMResourceLimit", obj.get("userAMResourceLimit").toString());
                mySqlWrapper.updateValue(queueName, "usedAMResource", obj.get("usedAMResource").toString());
                mySqlWrapper.updateValue(queueName, "AMResourceLimit", obj.get("AMResourceLimit").toString());
                mySqlWrapper.updateValue(queueName, "userLimitFactor", obj.get("userLimitFactor").getAsString());
                mySqlWrapper.updateValue(queueName, "preemptionDisabled", obj.get("preemptionDisabled").getAsString());
                mySqlWrapper.updateValue(queueName, "hideReservationQueues", obj.get("hideReservationQueues").getAsString());
                mySqlWrapper.updateValue(queueName, "nodeLabels", obj.get("nodeLabels").toString());
            } catch (Exception ex) {

            }
            return true;
        } else {
            return false;
        }
    }

    private Map<JsonElement, String> getAllQueuesToParents(JsonObject topObject) {
        Map<JsonElement, String> queuesToParentMap = new HashMap<JsonElement, String>();

        JsonElement rootQueueElement = topObject.get("scheduler").getAsJsonObject().get("schedulerInfo").
                getAsJsonObject().get("rootQueue");

        queuesToParentMap.put(rootQueueElement, "");

        JsonObject rootQueueObject = rootQueueElement.getAsJsonObject();

        if (rootQueueObject.has("childQueues")) {
            JsonElement childQueues = rootQueueObject.get("childQueues");
            queuesToParentMap.putAll(getAllChildQueues(childQueues, rootQueueObject.get("queueName").getAsString()));
        }

        return queuesToParentMap;
    }

    private Map<JsonElement, String> getAllChildQueues(JsonElement childQueues, String parent) {
        Map<JsonElement, String> queuesToParentMap = new HashMap<JsonElement, String>();

        if (childQueues.isJsonArray()) {
            // this means that there are multiple child queues
            JsonArray cqs = childQueues.getAsJsonArray();

            for (int i = 0; i<cqs.size(); i++) {
                JsonElement queue = cqs.get(i);
                queuesToParentMap.put(queue, parent);

                if (queue.getAsJsonObject().has("childQueues")) {
                    JsonElement subChildQueues = queue.getAsJsonObject().get("childQueues");
                    queuesToParentMap.putAll(getAllChildQueues(subChildQueues, queue.getAsJsonObject().get("queueName").getAsString()));
                }
            }
        } else {
            queuesToParentMap.put(childQueues, parent);
        }

        return queuesToParentMap;
    }

    private Map<JsonElement, String> getAllQueuesToParentsForCapacityScheduler(JsonObject topObject) {
        Map<JsonElement, String> queuesToParentMap = new HashMap<JsonElement, String>();

        JsonElement rootQueueElement = topObject.get("scheduler").getAsJsonObject().get("schedulerInfo").
                getAsJsonObject();

        queuesToParentMap.put(rootQueueElement, "");

        JsonObject rootQueueObject = rootQueueElement.getAsJsonObject();

        if (rootQueueObject.has("queues")) {
            JsonElement childQueues = rootQueueObject.get("queues").getAsJsonObject().get("queue");
            queuesToParentMap.putAll(getAllChildQueuesForCapacityScheduler(childQueues, rootQueueObject.get("queueName").getAsString()));
        }

        return queuesToParentMap;
    }

    private Map<JsonElement, String> getAllChildQueuesForCapacityScheduler(JsonElement childQueues, String parent) {
        Map<JsonElement, String> queuesToParentMap = new HashMap<JsonElement, String>();

        if (childQueues.isJsonArray()) {
            // this means that there are multiple child queues
            JsonArray cqs = childQueues.getAsJsonArray();

            for (int i = 0; i<cqs.size(); i++) {
                JsonElement queue = cqs.get(i);
                queuesToParentMap.put(queue, parent);

                if (queue.getAsJsonObject().has("queues")) {
                    JsonElement subChildQueues = queue.getAsJsonObject().get("queues").getAsJsonObject().get("queue");
                    queuesToParentMap.putAll(getAllChildQueuesForCapacityScheduler(subChildQueues, queue.getAsJsonObject().get("queueName").getAsString()));
                }
            }
        } else {
            queuesToParentMap.put(childQueues, parent);
        }

        return queuesToParentMap;
    }

    private Scheduler.queue[] readClusterSchedulerJsonResponse(String clusterSchedulerResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonNode node = mapper.readTree(clusterSchedulerResponse);
        node = node.get("scheduler").get("schedulerInfo").get("queues");
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