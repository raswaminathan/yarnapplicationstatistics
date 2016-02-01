package com.rahulswaminathan.yarnapplicationstatistics;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by rahulswaminathan on 11/12/15.
 */
public class TestGson {


    public static void main(String[] args) {
        PropsParser pp = new PropsParser();
        String url = "http://" + pp.getYarnWEBUI() + "/ws/v1/cluster/scheduler";
        HttpGetHandler hgh = new HttpGetHandler(url);
        String schedulerResponse = hgh.sendGet();

        Gson gson = new Gson();

        JsonElement je = gson.fromJson(schedulerResponse, JsonElement.class);
        JsonObject jo = je.getAsJsonObject();

        JsonObject rootQueue = jo.get("scheduler").getAsJsonObject().get("schedulerInfo").
                getAsJsonObject().get("rootQueue").getAsJsonObject();

        JsonElement childQueues = rootQueue.get("childQueues");

        if (childQueues.isJsonArray()) {
            JsonArray cqs = childQueues.getAsJsonArray();

            for (int i = 0; i<cqs.size(); i++) {
                JsonElement queue = cqs.get(i);
                System.out.println(queue + "\n\n");

                if (queue.getAsJsonObject().has("childQueues")) {
                    JsonElement subChildQueues = queue.getAsJsonObject().get("childQueues");
                    System.out.println("queue " + queue.getAsJsonObject().get("queueName") + " has children");
                }
            }


        }

    }
}
