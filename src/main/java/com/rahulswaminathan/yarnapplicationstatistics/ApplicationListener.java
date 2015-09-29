package com.rahulswaminathan.yarnapplicationstatistics;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by rahulswaminathan on 11/19/14.
 */
public abstract class ApplicationListener {
    private Set<Apps.app> appsSet = new HashSet<Apps.app>();
    private long startTime = 0;
    private Set<Apps.app> removedApps = new HashSet<Apps.app>();
    private Map<Apps.app, String> appToStateMap = new HashMap<Apps.app, String>();
    private Map<Apps.app, Integer> appToContainersMap = new HashMap<Apps.app, Integer>();
    private Thread thread;
    private AppThread runnable;

    public void startListening() {
        startTime = System.currentTimeMillis();
        appsSet = new HashSet<Apps.app>();
        runnable = new AppThread();
        thread = new Thread(runnable);
        thread.start();
    }

    public void stopListening() {
        if (thread != null) {
            runnable.terminate();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void onAppBegin(Apps.app app);

    public abstract void onAppFinish(Apps.app app);

    public abstract void onAppChangeState(Apps.app app);

    public abstract void onAppChangeContainers(Apps.app app);

    public Set<Apps.app> getAppsSet() {
        return appsSet;
    }

    private String sendAppsGet() throws Exception {
        PropsParser pp = new PropsParser();
        String url = "http://" + pp.getYarnWEBUI() + "/ws/v1/cluster/apps?startedTimeBegin=" + startTime;
        HttpGetHandler hgh = new HttpGetHandler(url);
        return hgh.sendGet();
    }

    private Apps.app[] readAppsJsonResponse(String appsJsonResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonNode node = mapper.readTree(appsJsonResponse);
        node = node.get("apps").get("app");
        TypeReference<Apps.app[]> typeRef = new TypeReference<Apps.app[]>() {};
        return mapper.readValue(node.traverse(), typeRef);
    }

    class AppThread implements Runnable {

        private volatile boolean running = true;

        public AppThread() {
        }

        public void terminate() {
            running = false;
        }

        public void run() {
            while(running) {
                try {
                    String appsResponse = sendAppsGet();
                    Apps.app[] apps = readAppsJsonResponse(appsResponse);

                    for (Apps.app app : apps) {
                        if (!appsSet.contains(app) && !removedApps.contains(app)) {
                            appsSet.add(app);
                            appToStateMap.put(app, app.getState());
                            appToContainersMap.put(app, app.getRunningContainers());
                            onAppBegin(app);
                        }
                        if (app.getState().equals("FINISHED") && !removedApps.contains(app)) {
                            removedApps.add(app);
                            appsSet.remove(app);
                            appToStateMap.put(app, app.getState());
                            onAppFinish(app);
                        }
                        if (!app.getState().equals(appToStateMap.get(app))) {
                            appToStateMap.put(app,app.getState());
                            onAppChangeState(app);
                        }
                        if (app.getRunningContainers() != appToContainersMap.get(app)) {
                            appToContainersMap.put(app,app.getRunningContainers());
                            onAppChangeContainers(app);
                        }
                    }
                } catch (Exception e) {
                    // do nothing if appsResponse is empty
                }
            }
        }
    }
}