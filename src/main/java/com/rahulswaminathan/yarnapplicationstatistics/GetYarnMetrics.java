package com.rahulswaminathan.yarnapplicationstatistics;

/**
 * Created by rahulswaminathan on 10/21/14.
 */
import java.io.*;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import static com.rahulswaminathan.yarnapplicationstatistics.GetYarnMetrics.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class GetYarnMetrics {

    private final String USER_AGENT = "Mozilla/5.0";
    static long totalTimeInMillis = 0;
    static int iterationNumber = 0;
    static String queuesAsString = "";
    static List<String> queuesUsed = new ArrayList<String>();
    static long startTime = 0;
    static Map<Integer, Long> iterationToTimeMap = new HashMap<Integer, Long>();

    public static void main(String[] args) throws Exception {
        int numIterations = 50;
        String dmem = "1g";
        String emem = "1g";

        String[] queues = {"a", "a"};
        iterationNumber = 0;
        totalTimeInMillis = 0;
        queuesUsed = new ArrayList<String>();
        iterationToTimeMap = new HashMap<Integer, Long>();

        startTime = System.currentTimeMillis();
        Runnable run = new StatsThread(numIterations, dmem, emem, queues);
        new Thread(run).start();

        doStats(dmem, emem, queues);
    }

    public static void launchSparkJob(String dmem, String emem, String ... queues) throws Exception{
        iterationNumber++;
        int thisIteration = iterationNumber;
        //System.out.println(thisIteration);

        for (String queue : queues) {
            new ProcessBuilder("/bin/bash", "/Users/rahulswaminathan/" +
                    "IdeaProjects/yarn-application-statistics/run_spark_pi.sh", dmem, emem, queue).start();
        }
    }

    public static void writeQueueInfoToFile(BufferedWriter writer, Scheduler.queue[] list) throws Exception {
        writer.newLine();
        for (Scheduler.queue q : list) {
            writer.write("QUEUE: " + q.getQueueName() + " Resources Used: " + q.getResourcesUsed().getMemory());
            writer.newLine();
            writer.write("Num Containers: " + q.getNumContainers());
            writer.newLine();
            writer.write("Num applications: " + q.getNumApplications());
            writer.newLine();
            writer.write("Num active applications: " + q.getNumActiveApplications());
            writer.newLine();
        }

        writer.write("Total Applications: " + getTotalApplications(list));
        writer.newLine();
        writer.write("Total Active Applications: " + getTotalActiveApplications(list));
        writer.newLine();
        writer.write("Total Containers: " + getTotalContainers(list));
        writer.newLine();
    }

    public static int getTotalContainers(Scheduler.queue[] list) {
        int totalContainers = 0;
        for (Scheduler.queue q : list) {
            totalContainers += q.getNumContainers();
        }
        return totalContainers;
    }

    public static int getTotalActiveApplications(Scheduler.queue[] list) {
        int totalActiveApps = 0;
        for (Scheduler.queue q : list) {
            totalActiveApps += q.getNumActiveApplications();
        }
        return totalActiveApps;
    }

    public static int getTotalApplications(Scheduler.queue[] list) {
        int totalApps = 0;
        for (Scheduler.queue q : list) {
            totalApps += q.getNumApplications();
        }
        return totalApps;
    }

    public static Map<String, Integer> populateQueueNameToApps(Scheduler.queue[] list) {
        Map<String, Integer> toRet = new HashMap<String, Integer>();
        for (Scheduler.queue q : list) {
            toRet.put(q.getQueueName(), q.getNumApplications());
        }
        return toRet;
    }

    public static boolean checkIfDone(boolean hasStarted, Scheduler.queue[] list) throws Exception{
        if (!hasStarted)
            return false;

        Map<String, Integer> queueNameToApps = populateQueueNameToApps(list);
        boolean isDone = true;
        for (String qu : queueNameToApps.keySet()) {
            int apps = queueNameToApps.get(qu);
            if (apps != 0)
                isDone = false;
        }

        return isDone;
    }

    public static Scheduler.queue[] readJsonResponse(String clusterSchedulerResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonNode node = mapper.readTree(clusterSchedulerResponse);
        node = node.get("scheduler").get("schedulerInfo").get("queues").get("queue");
        TypeReference<Scheduler.queue[]> typeRef = new TypeReference<Scheduler.queue[]>() {};
        return mapper.readValue(node.traverse(), typeRef);
    }

    public static void writeClusterMetrics(BufferedWriter writer, String clusterMetricsResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ClusterMetrics metrics = mapper.readValue(clusterMetricsResponse, ClusterMetrics.class);
        writer.newLine();
        writer.write("Cluster metrics: ");
        writer.newLine();
        writer.write("Allocated MB: " + Long.toString(metrics.getClusterMetrics().getAllocatedMB()));
        writer.newLine();
        writer.write("Available MB: " + Long.toString(metrics.getClusterMetrics().getAvailableMB()));
        writer.newLine();
        writer.write("Total MB: " + Long.toString(metrics.getClusterMetrics().getTotalMB()));
        writer.newLine();
        writer.write("Apps Running: " + Long.toString(metrics.getClusterMetrics().getAppsRunning()));
        writer.newLine();
        writer.write("Containers Allocated: " + metrics.getClusterMetrics().getContainersAllocated());
    }

    public static void doStats(String dmem, String emem, String ... queues) throws Exception{
        System.out.println("HERE");

        queuesAsString = "";
        String filename = "data_" + dmem + "_" + emem;

        for (String str : queues) {
            filename += "_" + str;
            queuesAsString += str + " ";
        }
        BufferedWriter overallWriter = new BufferedWriter(new FileWriter(filename + ".txt", true));
        BufferedWriter schedulerWriter = new BufferedWriter(new FileWriter(filename + "_scheduler.txt", true));
        BufferedWriter metricsWriter = new BufferedWriter(new FileWriter(filename + "_metrics.txt", true));
        GetYarnMetrics http = new GetYarnMetrics();

        overallWriter.write("executer memory: " + emem);
        overallWriter.newLine();
        overallWriter.write("driver memory: " + dmem);
        overallWriter.newLine();
        overallWriter.write("number of queues: " + queues.length);
        overallWriter.newLine();
        overallWriter.write("queues: " + queuesAsString);
        overallWriter.newLine();

        overallWriter.write("Start time in millis: " + startTime);
        schedulerWriter.write("Start time in millis: " + startTime);
        metricsWriter.write("Start time in millis: " + startTime);
        overallWriter.newLine();
        schedulerWriter.newLine();
        metricsWriter.newLine();
        boolean hasStarted = false;

        while (true) {
            Thread.sleep(2000);
            overallWriter.newLine();

            String clusterMetricsResponse  = http.sendClusterMetricsGet();
            String clusterSchedulerResponse = http.sendClusterSchedulerGet();
            Scheduler.queue[] list = readJsonResponse(clusterSchedulerResponse);
            long currentTimeElapsed = System.currentTimeMillis() - startTime;
            overallWriter.write("current time elapsed in ms=" + currentTimeElapsed);
            schedulerWriter.write("current time elapsed in ms=" + currentTimeElapsed);
            metricsWriter.write("current time in elapsed ms=" + currentTimeElapsed);

            overallWriter.newLine();
            schedulerWriter.newLine();
            metricsWriter.newLine();

            int numApps = getTotalApplications(list);
            overallWriter.write("Number of applications=" + numApps);

            writeQueueInfoToFile(schedulerWriter, list);

            if (!hasStarted && numApps > 0)
                hasStarted = true;

            if (checkIfDone(hasStarted, list)) {
                System.out.println("GETTING EHRERERE");
                long endTime = System.currentTimeMillis();
                Thread.sleep(5000);
                list = readJsonResponse(http.sendClusterSchedulerGet());
                if (checkIfDone(hasStarted, list)) {
                    long totalTime = endTime - startTime;
                    overallWriter.newLine();
                    schedulerWriter.newLine();
                    metricsWriter.newLine();
                    overallWriter.write("Total Time for jobs in ms: " + totalTime);
                    overallWriter.newLine();
                    schedulerWriter.newLine();
                    metricsWriter.newLine();
                    overallWriter.flush();
                    overallWriter.close();
                    schedulerWriter.flush();
                    schedulerWriter.close();
                    metricsWriter.flush();
                    metricsWriter.close();
                    break;
                }
            }

            writeClusterMetrics(metricsWriter, clusterMetricsResponse);

            //System.out.println(clusterSchedulerResponse);

//            boolean anyActive = false;
//            for (String qu : queueNameToApps.keySet()) {
//                int apps = queueNameToApps.get(qu);
//                int active = queueNameToActiveApps.get(qu);
//                if (!start && active != 0) {
//                    start = true;
//                }
//                if (active != 0)
//                    anyActive = true;
//            }
//
//            if (start && !anyActive) {
//                long endTime = System.currentTimeMillis();
//                totalTimeInMillis += (endTime - startTime);
//                writer.write("Took: " + (endTime - startTime));
//                iterationToTimeMap.put(thisIteration, (endTime - startTime));
//                PrintWriter w = new PrintWriter(filename + "_map" + ".txt");
//                for (Integer integ : iterationToTimeMap.keySet()) {
//                    w.println(integ + " = " + iterationToTimeMap.get(integ));
//                }
//                w.close();
//                writer.newLine();
//                writer.write("Iteration number: " + iterationNumber);
//                writer.newLine();
//                writer.write("Total time: " + totalTimeInMillis);
//                writer.newLine();
//                writer.flush();
//                writer.close();
//                break;
//            }
//            node = node.get("queues").get("queue");
//
//            while (node != null) {
//                typeRef = new TypeReference<Scheduler.queue[]>() {};
//                list = mapper.readValue(node.traverse(), typeRef);
//                if (count == 0) {
//                    for (Scheduler.queue q : list) {
//                        queueNameToMaxMemory.put(q.getQueueName(), q.getMaxCapacity());
//                    }
//                }
//                for (Scheduler.queue q : list) {
//                    System.out.println("QUEUE: " + q.getQueueName() + " Resources Used: " + q.getResourcesUsed().getMemory());
//                    System.out.println("Num Containers: " + q.getNumContainers());
//                    System.out.println("Num applications: " + q.getNumApplications());
//                    System.out.println("Num active applications: " + q.getNumActiveApplications());
//                }
//                node = node.get("queues").get("queue");
//            }
            //Scheduler scheduler = mapper.readValue(clusterScheduerResponse, Scheduler.class);
            //System.out.println(clusterScheduerResponse);
            //System.out.println(scheduler.getScheduler().getSchedulerInfo().getQueues().getCapacity());

            //System.out.println(scheduler.getScheduler().getSchedulerInfo().getCapacity());
        }
    }
    // HTTP GET request
    private String sendClusterMetricsGet() throws Exception {
        String url = "http://localhost:8088/ws/v1/cluster/metrics";
        return sendGetToURL(url);
    }

    private String sendClusterSchedulerGet() throws Exception {
        String url = "http://localhost:8088/ws/v1/cluster/scheduler";
        return sendGetToURL(url);
    }

    private String sendGetToURL(String url) throws Exception {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);

        HttpResponse response = client.execute(request);

        //System.out.println("\nSending 'GET' request to URL : " + url);
        //System.out.println("Response Code : " +
        //      response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        //System.out.println(result.toString());
        return result.toString();
    }
}

class StatsThread implements Runnable {

    private String dmem;
    private String emem;
    private String[] queues;
    private int numIterations;

    public StatsThread(int numIterations, String dmem, String emem, String... queues) {
        this.numIterations = numIterations;
        this.dmem = dmem;
        this.emem = emem;
        this.queues = queues;
    }

    public void run() {
        try {
            if (numIterations <= 0)
                return;

            launchSparkJob(dmem,emem,queues);
            numIterations--;
            Random r = new Random();
            while (numIterations > 0) {
                int rand = r.nextInt(10);
                long curTime = System.currentTimeMillis();
                long startTime = curTime + rand * 1000;
                inner: while (true) {
                    if (System.currentTimeMillis() > startTime) {
                        launchSparkJob(dmem, emem, queues);
                        numIterations--;
                        break inner;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}