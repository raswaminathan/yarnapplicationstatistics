package com.rahulswaminathan.yarnapplicationstatistics;

/**
 * Created by rahulswaminathan on 3/18/15.
 */
public class StatsDIntegrationTest {

    private static final String PREFIX = "my.prefix";
    private static final String SERVER_LOCATION = "localhost";
    private static final int PORT = 8125;

    public static void main(String[] args) {

        DummyStatsDServer server = new DummyStatsDServer(PORT, PREFIX);

        ClusterMetricsDaemon d = new ClusterMetricsDaemon();
        d.run();

        while (true) {
            server.waitForMessage();

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(server.getGaugeValue("allocatedMB"));

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        //server.stop();
    }
}
