package com.rahulswaminathan.yarnapplicationstatistics;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

/**
 * Created by rahulswaminathan on 4/1/15.
 */
public class ApplicationLogger {

    private static final String PREFIX = "my.prefix";
    private static final String SERVER_LOCATION = "localhost";
    private static final int PORT = 8125;
    StatsDClient statsd;

    public ApplicationLogger() {
            statsd = new NonBlockingStatsDClient(PREFIX, SERVER_LOCATION, PORT);
    }

    public void logGauge(String gauge, Integer value) {
        statsd.recordGaugeValue(gauge, value);
    }
}
