package com.rahulswaminathan.yarnapplicationstatistics;

/**
 * Created by rahulswaminathan on 3/31/15.
 */
public class SQLTest {

    public static void main(String[] args) {

        StatsDSQLWriter writer = new StatsDSQLWriter();

        ClusterMetricsDaemon d = new ClusterMetricsDaemon();
        d.run();

        writer.run();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            writer.printTable("cluster_metrics");
            writer.printTable("application_logging");
            writer.printTable("scheduler_metrics");
        }



    }
}
