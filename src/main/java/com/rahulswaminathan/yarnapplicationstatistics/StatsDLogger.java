package com.rahulswaminathan.yarnapplicationstatistics;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

/**
 * Created by rahulswaminathan on 4/1/15.
 */
public class StatsDLogger {

    private static final String PREFIX = "my.prefix";
    private static final String SERVER_LOCATION = "localhost";
    private static final int PORT = 8125;
    StatsDClient statsd;

    /**
     * Logger for sending application statistics to MySQL. Create an instance of this in the application, and then the methods can be used to log various statistics for analysis.
     */
    public StatsDLogger() {
            statsd = new NonBlockingStatsDClient(PREFIX, SERVER_LOCATION, PORT);
    }

    /**
     * Logs a gauge value
     * @param gauge
     *          Name of gauge to be logged
     * @param value
     *          New value of gauge
     */
    public void logGauge(String gauge, Integer value) {
        statsd.recordGaugeValue(gauge, value);
    }

    /**
     * Increments a counter by one.
     * @param count
     *          Counter to be incremented.
     */
    public void incrementCount(String count) { statsd.incrementCounter(count);}

    /**
     * Increments a counter by the amount specified.
     * @param count
     *          Counter to be incremented.
     * @param value
     *          Value to increment the counter by.
     */
    public void incrementCount(String count, Integer value) { statsd.count(count, value);};

    /**
     * Decrements a counter by one.
     * @param count
     *          Counter to be decremented.
     */
    public void decrementCount(String count) {statsd.decrementCounter(count);}

    /**
     * Decrements a counter by the amount specified.
     * @param count
     *          Counter to be decremented.
     * @param value
     *          Value to decrement the counter by.
     */
    public void decrementCount(String count, Integer value) {statsd.count(count, -value);}
}
