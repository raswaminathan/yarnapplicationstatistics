package com.rahulswaminathan.yarnapplicationstatistics;

/**
 * Created by rahulswaminathan on 10/21/14.
 */
import java.io.*;
import java.util.*;

import org.apache.hadoop.yarn.client.api.YarnClient;
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
    private String queuesAsString = "";
    private long startTime = 0;
    private int iterationNumber = 0;
    private float totalAllocatedMB = 0;
    private float averageAllocatdMB = 0;

    public static void main(String[] args) throws Exception {
        GetYarnMetrics m = new GetYarnMetrics();

        m.start();
    }

    public void start() throws Exception{
        Properties props = new Properties();
        String propFileName = "conf.properties";

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        props.load(inputStream);
        if (inputStream == null) {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        String[] queues = props.getProperty("queues").split(",");
        String emem = props.getProperty("emem");
        String dmem = props.getProperty("dmem");
        Integer numIterations = Integer.parseInt(props.getProperty("numIterations"));

        Runnable run = new StatsThread(numIterations, dmem, emem, queues);
        new Thread(run).start();

        doStats(dmem, emem, queues);
    }

    private void doStats(String dmem, String emem, String ... queues) throws Exception{
        startTime = System.currentTimeMillis();

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

        Date dateStartTime = new Date(startTime);

        writeMessage("Date started: " + dateStartTime, overallWriter, schedulerWriter, metricsWriter);
        makeNewLines(overallWriter, schedulerWriter, metricsWriter);
        boolean hasStarted = false;

        iterationNumber = 0;
        while (true) {
            Thread.sleep(2000);
            iterationNumber++;
            overallWriter.newLine();

            String clusterMetricsResponse  = http.sendClusterMetricsGet();
            String clusterSchedulerResponse = http.sendClusterSchedulerGet();
            Scheduler.queue[] list = readJsonResponse(clusterSchedulerResponse);
            long currentTimeElapsed = System.currentTimeMillis() - startTime;
            writeMessage("current time elapsed in ms=" + currentTimeElapsed, overallWriter, schedulerWriter, metricsWriter);
            makeNewLines(overallWriter, schedulerWriter, metricsWriter);

            int numApps = getTotalApplications(list);
            writeMessage("Number of applications=" + numApps, overallWriter);

            writeQueueInfoToFile(schedulerWriter, list);

            if (!hasStarted && numApps > 0)
                hasStarted = true;

            if (checkIfDone(hasStarted, list)) {
                long endTime = System.currentTimeMillis();
                Thread.sleep(5000);
                list = readJsonResponse(http.sendClusterSchedulerGet());
                if (checkIfDone(hasStarted, list)) {
                    long totalTime = endTime - startTime;
                    makeNewLines(overallWriter, schedulerWriter, metricsWriter);
                    overallWriter.write("Total Time for jobs in ms: " + totalTime);
                    makeNewLines(overallWriter, schedulerWriter, metricsWriter);
                    flushAndCloseAllWriters(overallWriter, schedulerWriter, metricsWriter);
                    break;
                }
            }

            writeClusterMetrics(metricsWriter, clusterMetricsResponse);
        }
    }

    private void writeMessage(String message, BufferedWriter ... writers) throws Exception {
        for (BufferedWriter writer : writers) {
            writer.write(message);
        }
    }

    private void flushAndCloseAllWriters(BufferedWriter ... writers) throws IOException {
        for (BufferedWriter writer : writers) {
            writer.flush();
            writer.close();
        }
    }

    private void makeNewLines(BufferedWriter ... writers) throws IOException {
        for (BufferedWriter writer : writers) {
            writer.newLine();
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

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    private void writeQueueInfoToFile(BufferedWriter writer, Scheduler.queue[] list) throws Exception {
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

    private Map<String, Integer> populateQueueNameToApps(Scheduler.queue[] list) {
        Map<String, Integer> toRet = new HashMap<String, Integer>();
        for (Scheduler.queue q : list) {
            toRet.put(q.getQueueName(), q.getNumApplications());
        }
        return toRet;
    }

    private boolean checkIfDone(boolean hasStarted, Scheduler.queue[] list) throws Exception{
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

    private Scheduler.queue[] readJsonResponse(String clusterSchedulerResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonNode node = mapper.readTree(clusterSchedulerResponse);
        node = node.get("scheduler").get("schedulerInfo").get("queues").get("queue");
        TypeReference<Scheduler.queue[]> typeRef = new TypeReference<Scheduler.queue[]>() {};
        return mapper.readValue(node.traverse(), typeRef);
    }

    private void writeClusterMetrics(BufferedWriter writer, String clusterMetricsResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ClusterMetrics metrics = mapper.readValue(clusterMetricsResponse, ClusterMetrics.class);
        writer.newLine();
        writer.write("Cluster metrics: ");
        writer.newLine();
        writer.write("Allocated MB: " + Long.toString(metrics.getClusterMetrics().getAllocatedMB()));
        totalAllocatedMB += metrics.getClusterMetrics().getAllocatedMB();
        averageAllocatdMB = totalAllocatedMB / iterationNumber;
        writer.newLine();
        writer.write("Available MB: " + Long.toString(metrics.getClusterMetrics().getAvailableMB()));
        writer.newLine();
        writer.write("Total MB: " + Long.toString(metrics.getClusterMetrics().getTotalMB()));
        writer.newLine();
        writer.write("Apps Running: " + Long.toString(metrics.getClusterMetrics().getAppsRunning()));
        writer.newLine();
        writer.write("Containers Allocated: " + metrics.getClusterMetrics().getContainersAllocated());
        writer.newLine();
        writer.write("Average Allocated MB: " + averageAllocatdMB);
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

    public void launchSparkJob(String dmem, String emem, String ... queues) throws Exception{
        for (String queue : queues) {
            new ProcessBuilder("/bin/bash", "/Users/rahulswaminathan/" +
                    "IdeaProjects/yarn-application-statistics/run_spark_pi.sh", dmem, emem, queue).start();
        }
    }
}