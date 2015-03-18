package com.rahulswaminathan.yarnapplicationstatistics;

import com.rahulswaminathan.yarnapplicationstatistics.CountObject;
import com.timgroup.statsd.StatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;

public class TestStatsD {
	
	private static final String PREFIX = "my.prefix";
	private static final String SERVER_LOCATION = "localhost";
	private static final int PORT = 8125;

	public static final void main(String[] args) {

		StatsDClient statsd = new NonBlockingStatsDClient(PREFIX,
				SERVER_LOCATION, PORT);
		DummyStatsDServer server = new DummyStatsDServer(PORT, PREFIX);

		statsd.recordGaugeValue("test", 100);
        statsd.recordGaugeValue("test", 200);
        statsd.recordGaugeValue("rahul", 1000);
		statsd.incrementCounter("hi");
		statsd.incrementCounter("foo");
		statsd.incrementCounter("foo");
		statsd.incrementCounter("foo");
		statsd.incrementCounter("foo");
		statsd.incrementCounter("foo");
		statsd.incrementCounter("foo");
		statsd.incrementCounter("foo");
		statsd.incrementCounter("foo");
		statsd.incrementCounter("foo");
		statsd.incrementCounter("foo");
		statsd.incrementCounter("foo");
		statsd.incrementCounter("bar");
		statsd.incrementCounter("bar");
		statsd.incrementCounter("bar");
		statsd.incrementCounter("bar");
		statsd.recordExecutionTime("bag", 25);
		statsd.recordSetEvent("qux", "one");

		for (CountObject count : server.countMessages()) {
			System.out.println(count.getTag() + ": " + count.getCount());
		}

        for (CountObject count: server.gaugeMessages()) {
            System.out.println(count.getTag() + ": " + count.getCount());
        }

		server.stop();
	}

	
}