package com.rahulswaminathan.yarnapplicationstatistics;

/**
 * Created by rahulswaminathan on 3/31/15.
 */
public class LaunchDaemons {

    public static void main(String[] args) {

        new ClusterMetricsDaemon().run();
        new SchedulerDaemon().run();
        new MonitorApplicationsDaemon().run();

    }
}
