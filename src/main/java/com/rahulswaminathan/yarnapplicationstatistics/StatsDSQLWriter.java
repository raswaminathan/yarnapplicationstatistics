package com.rahulswaminathan.yarnapplicationstatistics;

import java.io.InputStream;
import java.util.*;

/**
 * Created by rahulswaminathan on 3/31/15.
 */
public class StatsDSQLWriter {

    private static final String PREFIX = "my.prefix";
    private static final String SERVER_LOCATION = "localhost";
    private static final int PORT = 8125;
    private static final String COUNT_FILE = "counts.properties";
    private static final String GAUGE_FILE = "gauges.properties";
    private static final String DATABASE_NAME = "test";
    private static final String MYSQL_USERNAME = "root";
    private static final String MYSQL_TABLE_NAME = "metrics";
    private Map<String, List<String>> tableNameToGaugesMap;
    private Map<String, List<String>> tableNameToCountsMap;
    private StatsDReceiver server;
    private SQLWrapper mySQLWrapper;

    /**
     * Writes the data collected from the StatsDReceiver to SQL.
     */
    public StatsDSQLWriter() {
        initialize();
    }

    /**
     * Runs the StatsDSqlWriter thread.
     */
    public void run() {
        Runnable run = new StatsDSQLWriterThread();
        new Thread(run).start();
    }

    private void initialize() {
        server = new StatsDReceiver(PORT, PREFIX);

        tableNameToCountsMap = readProperties(COUNT_FILE);
        tableNameToGaugesMap = readProperties(GAUGE_FILE);
        mySQLWrapper = new SQLWrapper(DATABASE_NAME , SERVER_LOCATION, MYSQL_USERNAME);
        initializeTable(tableNameToCountsMap);
        initializeTable(tableNameToGaugesMap);
    }

    private void initializeTable(Map<String, List<String>> tableToItemsMap) {
        for (String table : tableToItemsMap.keySet()) {
            List<String> items = tableToItemsMap.get(table);
            for (String item : items) {
                mySQLWrapper.removeRow(table, item);
                mySQLWrapper.insertIntoTable(table, item, 0);
            }
        }
    }

    private Map<String, List<String>> readProperties(String filename) {
        Map<String, List<String>> toReturn = new HashMap<String, List<String>>();
        try {
            Properties props = new Properties();
            String propFileName = filename;

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            if (inputStream == null) {
                System.err.println("FILE NOT FOUND");
                //throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            props.load(inputStream);

            for (String prop : props.stringPropertyNames()) {
                toReturn.put(prop, Arrays.asList(props.getProperty(prop).split(",")));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    /**
     * Prints the specified SQL table to the console.
     * @param tableName
     *          Table to be printed.
     */
    public void printTable(String tableName) {
        mySQLWrapper.printTableInformation(tableName);
    }

    /**
     * Thread that runs consistently to pull data from the StatsDReceiver and saves it into MySQL using the SQLWrapper.
     */
    class StatsDSQLWriterThread implements Runnable {

        public void run() {
            while (true) {
                server.waitForMessage();

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                updateGaugeValues();
                updateCountValues();

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        private void updateGaugeValues() {
            for (String table : tableNameToGaugesMap.keySet()) {
                List<String> gauges = tableNameToGaugesMap.get(table);
                for (String gauge : gauges) {
                    mySQLWrapper.updateValue(table, gauge, server.getGaugeValue(gauge));
                }
            }
        }
        private void updateCountValues() {
            for (String table : tableNameToCountsMap.keySet()) {
                List<String> counts = tableNameToCountsMap.get(table);
                for (String count : counts) {
                    mySQLWrapper.updateValue(table, count, server.getCountValue(count));
                }
            }
        }
    }
}