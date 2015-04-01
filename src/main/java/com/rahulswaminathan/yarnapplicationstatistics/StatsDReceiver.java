package com.rahulswaminathan.yarnapplicationstatistics;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class StatsDReceiver {

    private static final char COUNT_CHAR = 'c';
    private static final char GAUGE_CHAR = 'g';
    private final DatagramSocket server;
    private Map<String, Integer> countMap;
    private Map<String, Integer> gaugeMap;
    private List<String> messagesReceived = new ArrayList<String>();
    private String prefix;
    private boolean run;

    /**
     * This is a thread to receive StatsD messages on a given port. It constantly listens for new messages sent over StatsD, and will organize them into maps to handle all counters and gauges.
     *
     * @param port   Port to listen on
     * @param prefix Prefix tag of the running StatsD client.
     */
    public StatsDReceiver(int port, String prefix) {
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
                        String currentPacket = new String(packet.getData(), Charset.forName("UTF-8")).trim();

                        analyzePacket(currentPacket);

                        messagesReceived.add(currentPacket);
                    } catch (Exception e) {
                    }

                }
            }
        }).start();
    }

    /**
     * Stops the StatsD server.
     */
    public void stop() {
        run = false;
        server.close();
    }

    /**
     * Sleep until the server receives a message.
     */
    public void waitForMessage() {
        while (messagesReceived.isEmpty()) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Returns a list of the raw string packets received.
     * @return
     */
    public List<String> messagesReceived() {
        return new ArrayList<String>(messagesReceived);
    }

    /**
     * Returns the value of the given counter.
     * @param count
     *          Counter to return.
     * @return
     *          Value of the given counter.
     */
    public Integer getCountValue(String count) {
        if (!countMap.containsKey(count)) {
            return -1;
        }
        return countMap.get(count);
    }

    /**
     * Returns the value of the given gauge.
     * @param gauge
     *          Gauge to return.
     * @return
     *          Value of the given gauge.
     */
    public Integer getGaugeValue(String gauge) {
        if (!gaugeMap.containsKey(gauge)) {
            return -1;
        }
        return gaugeMap.get(gauge);
    }

    /**
     * Returns a list of the counters.
     * @return
     *          List of all counters as CountObjects.
     */
    public List<CountObject> countMessages() {
        return getMessages(countMap);
    }

    /**
     * Returns a list of the gauges.
     * @return
     *          List of all gauges as CountObjects.
     */
    public List<CountObject> gaugeMessages() {
        return getMessages(gaugeMap);
    }

    private List<CountObject> getMessages(Map<String, Integer> map) {
        List<CountObject> result = new ArrayList<CountObject>();
        for (String key : map.keySet()) {
            CountObject current = new CountObject(key, map.get(key));
            result.add(current);
        }

        return result;
    }

    private void analyzePacket(String packet) {
        packet = packet.substring(prefix.length() + 1);

        char messageType = getMessageType(packet);
        packet = packet.substring(0, packet.length() - 2);
        String[] info = packet.split(":");

        if (messageType == COUNT_CHAR) {
            if (countMap.containsKey(info[0])) {
                countMap.put(info[0], countMap.get(info[0]) + Integer.parseInt(info[1]));
            } else {
                countMap.put(info[0], 1);
            }
        } else if (messageType == GAUGE_CHAR) {
            gaugeMap.put(info[0], Integer.parseInt(info[1]));
        }
    }

    private char getMessageType(String packet) {
        return packet.charAt(packet.length() - 1);
    }
}