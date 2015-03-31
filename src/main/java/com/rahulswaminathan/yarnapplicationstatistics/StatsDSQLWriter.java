package com.rahulswaminathan.yarnapplicationstatistics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahulswaminathan on 3/31/15.
 */
public class StatsDSQLWriter {

    private static final String PREFIX = "my.prefix";
    private static final String SERVER_LOCATION = "localhost";
    private static final int PORT = 8125;
    private static final String COUNT_FILE = "counts";
    private static final String GAUGE_FILE = "gauges";
    private static final String DATABASE_NAME = "test";
    private static final String MYSQL_USERNAME = "root";
    private static final String MYSQL_TABLE_NAME = "metrics";
    private List<String> countStrings;
    private List<String> gaugeStrings;
    private DummyStatsDServer server;
    private SQLWrapper mySQLWrapper;

    public StatsDSQLWriter() {
        initialize();
    }

    public void run() {
        Runnable run = new StatsDSQLWriterThread();
        new Thread(run).start();
    }

    private void initialize() {
        server = new DummyStatsDServer(PORT, PREFIX);
        countStrings = readFile(COUNT_FILE);
        gaugeStrings = readFile(GAUGE_FILE);
        mySQLWrapper = new SQLWrapper(DATABASE_NAME , SERVER_LOCATION, MYSQL_USERNAME);
        for (String gauge : gaugeStrings) {
            if (mySQLWrapper.removeRow(MYSQL_TABLE_NAME, gauge)) {
                mySQLWrapper.insertIntoTable(MYSQL_TABLE_NAME,gauge,0);
            }
        }
    }

    private List<String> readFile(String filename) {
        List<String> toReturn = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            for (String line; (line = br.readLine()) != null; ) {
                toReturn.add(line);
            }
            // line is not visible here.
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    class StatsDSQLWriterThread implements Runnable {

        public void run() {
            while (true) {
                server.waitForMessage();

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                /// instead of printing we need to write to mysql

                for (String gauge : gaugeStrings) {
                    //System.out.println(server.getLastGaugeValue(gauge));
                    mySQLWrapper.updateValue(MYSQL_TABLE_NAME , gauge, server.getLastGaugeValue(gauge));
                }

                for (String count : countStrings) {
                    System.out.println(server.getLastCountValue(count));
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mySQLWrapper.printTableInformation(MYSQL_TABLE_NAME);
            }
        }
    }
}
