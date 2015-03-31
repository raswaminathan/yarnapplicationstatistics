package com.rahulswaminathan.yarnapplicationstatistics;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DummyStatsDServer {

    private static final char COUNT_CHAR = 'c';
    private static final char GAUGE_CHAR = 'g';
    private Map<String, Integer> countMap;
    private Map<String, Integer> gaugeMap;
    private List<String> messagesReceived = new ArrayList<String>();
    private String prefix;
    private final DatagramSocket server;
    private boolean run;

    public DummyStatsDServer(int port, String prefix) {
        this.prefix = prefix;
        run = true;
        countMap = new HashMap<String, Integer>();
        gaugeMap = new HashMap<String, Integer>();
        try {
            server = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (run) {
                    try {
                        final DatagramPacket packet = new DatagramPacket(
                                new byte[256], 256);
                        server.receive(packet);
                        String currentPacket = new String(packet.getData(),Charset.forName("UTF-8")).trim();

                        analyzePacket(currentPacket);

                        messagesReceived.add(currentPacket);
                    } catch (Exception e) {
                    }

                }
            }
        }).start();
    }

    public void stop() {
        run = false;
        server.close();
    }

    public void waitForMessage() {
        while (messagesReceived.isEmpty()) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
            }
        }
    }

    public List<String> messagesReceived() {
        return new ArrayList<String>(messagesReceived);
    }

    public Integer getLastCountValue(String count) {
        if (!countMap.containsKey(count)) {
            return -1;
        }
        return countMap.get(count);
    }

    public Integer getLastGaugeValue(String gauge) {
        if (!gaugeMap.containsKey(gauge)) {
            return -1;
        }
        return gaugeMap.get(gauge);
    }

    public List<CountObject> countMessages() {
        return getMessages(countMap);
    }

    public List<CountObject> gaugeMessages() {
        return getMessages(gaugeMap);
    }

    private List<CountObject> getMessages(Map<String, Integer> map) {
        List<CountObject> result = new ArrayList<CountObject>();
        for (String key: map.keySet()) {
            CountObject current = new CountObject(key, map.get(key));
            result.add(current);
        }

        return result;
    }

    private void analyzePacket(String packet) {
        packet = packet.substring(prefix.length() + 1);

        char messageType = getMessageType(packet);
        packet = packet.substring(0, packet.length()-2);
        String[] info = packet.split(":");

        if (messageType == COUNT_CHAR) {
            if (countMap.containsKey(info[0])) {
                countMap.put(info[0], countMap.get(info[0])+1);
            }
            else {
                countMap.put(info[0], 1);
            }
        }

        else if (messageType == GAUGE_CHAR) {
            gaugeMap.put(info[0], Integer.parseInt(info[1]));
        }
    }

    private char getMessageType(String packet) {
        return packet.charAt(packet.length()-1);
    }
}