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
    private final String USER_AGENT = "Mozilla/5.0";
    private Set<Apps.app> appsSet = new HashSet<Apps.app>();
    private long startTime = 0;
    private Set<Apps.app> removedApps = new HashSet<Apps.app>();
    private Map<Apps.app, String> appToStateMap = new HashMap<Apps.app, String>();
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

    public Set<Apps.app> getAppsSet() {
        return appsSet;
    }

    private String sendAppsGet(long startTime) throws Exception {
        String url = "http://localhost:8088/ws/v1/cluster/apps?startedTimeBegin=" + startTime;
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
                    String appsResponse = sendAppsGet(startTime);
                    Apps.app[] apps = readAppsJsonResponse(appsResponse);

                    for (Apps.app app : apps) {
                        if (!appsSet.contains(app) && !removedApps.contains(app)) {
                            appsSet.add(app);
                            appToStateMap.put(app, app.getState());
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
                    }
                } catch (Exception e) {
                    // do nothing if appsResponse is empty
                }
            }
        }
    }
}