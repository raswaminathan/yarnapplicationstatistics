package com.rahulswaminathan.yarnapplicationstatistics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by rahulswaminathan on 1/14/15.
 */
public class MonitorApplicationsDaemon {

    private static final String TEMP_GAUGES_FILE = "gauges_temp.properties";
    private static final String QUEUE = "__queue";
    private static final String STATE = "__state";
    private static final String NAME = "__name";
    private static final String NUM_CONTAINERS = "__numContainers";
    private static final String TIMESTAMP = "__timeStamp";
    private static final String ELAPSED_TIME = "__elapsedTime";
    private static final String ALLOCATED_MB = "__allocatedMB";
    private static final String DATABASE_NAME = "test";
    private static final String MYSQL_USERNAME = "root";
    private static final String SERVER_LOCATION = "localhost";
    private static final String TIMESTAMP_FOR_TABLE = "Current Time Stamp";
    private static final String QUEUE_FOR_TABLE = "Application Queue";
    private static final String STATE_FOR_TABLE = "Current State";
    private static final String NAME_FOR_TABLE = "Application Name                                                   ";
    private static final String NUM_CONTAINERS_FOR_TABLE = "Number of Containers";
    private static final String ELAPSED_TIME_FOR_TABLE = "Elapsed Time";
    private static final String ALLOCATED_MB_FOR_TABLE = "Allocated MB";
    private static final String FINAL_STATUS_FOR_TABLE = "Final Status";

    private SQLWrapper mySqlWrapper;

    public MonitorApplicationsDaemon() {
        mySqlWrapper = new SQLWrapper(DATABASE_NAME , SERVER_LOCATION, MYSQL_USERNAME);
    }

    public void run() {
        System.out.println("monitor applications daemon is running");

        ApplicationListener myAppListener = new ApplicationListener() {
            @Override
            public void onAppBegin(Apps.app app) {
                    mySqlWrapper.createTagValueTable(app.getId());
                    initializeSQLTable(app);
                    updateSQLInfo(app);
            }

            @Override
            public void onAppFinish(Apps.app app) {
                updateSQLInfo(app);
            }

            @Override
            public void onAppChangeState(Apps.app app) {
                updateSQLInfo(app);
            }

            @Override
            public void onAppChangeContainers(Apps.app app) {
                updateSQLInfo(app);
            }
        };

        myAppListener.startListening();
    }

    private String generateSpaces(int length) {
        StringBuilder toReturn = new StringBuilder();

        for (int i = 0; i<length; i++) {
            toReturn.append(" ");
        }
        return toReturn.toString();
    }

    private void initializeSQLTable(Apps.app app) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(app.getId() + "_log.txt", true));
            writer.write(TIMESTAMP_FOR_TABLE + " " + QUEUE_FOR_TABLE + " " + STATE_FOR_TABLE + " " + NAME_FOR_TABLE +
                    " " + NUM_CONTAINERS_FOR_TABLE + " " + ELAPSED_TIME_FOR_TABLE + " " + ALLOCATED_MB_FOR_TABLE +
                    " " + FINAL_STATUS_FOR_TABLE);
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mySqlWrapper.insertIntoTable(app.getId(),  app.getId() + QUEUE, app.getQueue());
        mySqlWrapper.insertIntoTable(app.getId(),  app.getId() + STATE, app.getState().toLowerCase());
        mySqlWrapper.insertIntoTable(app.getId(),  app.getId() + NAME, app.getName());
        mySqlWrapper.insertIntoTable(app.getId(),  app.getId() + NUM_CONTAINERS,
                Integer.toString(app.getRunningContainers()));
        mySqlWrapper.insertIntoTable(app.getId(),  app.getId() + TIMESTAMP,
                Long.toString(System.currentTimeMillis()));
        mySqlWrapper.insertIntoTable(app.getId(),  app.getId() + ELAPSED_TIME,
                Long.toString(app.getElapsedTime()));
        mySqlWrapper.insertIntoTable(app.getId(),  app.getId() + ALLOCATED_MB,
                Integer.toString(app.getAllocatedMB()));
    }

    private void updateSQLInfo(Apps.app app) {
        String queue = app.getQueue();
        String state = app.getState().toLowerCase();
        String name = app.getName();
        String runningContainers = Integer.toString(app.getRunningContainers());
        String timeStamp = Long.toString(System.currentTimeMillis());
        String elapsedTime = Long.toString(app.getElapsedTime());
        String allocatedMB = Integer.toString(app.getAllocatedMB());
        String finalStatus = app.getFinalStatus().toLowerCase();

        String state_space = generateSpaces(STATE_FOR_TABLE.length() - state.length() + 1);
        String queue_space = generateSpaces(QUEUE_FOR_TABLE.length() - queue.length() + 1);
        String name_space = generateSpaces(NAME_FOR_TABLE.length() - name.length() + 1);
        String running_space = generateSpaces(NUM_CONTAINERS_FOR_TABLE.length() - runningContainers.length() + 1);
        String time_space = generateSpaces(TIMESTAMP_FOR_TABLE.length() - timeStamp.length() + 1);
        String elapsed_space = generateSpaces(ELAPSED_TIME_FOR_TABLE.length() - elapsedTime.length() + 1);
        String amb_space = generateSpaces(ALLOCATED_MB_FOR_TABLE.length() - allocatedMB.length() + 1);

        StringBuilder stringToWrite = new StringBuilder();
        stringToWrite.append(timeStamp + time_space + queue + queue_space + state + state_space +
                name + name_space + runningContainers + running_space + elapsedTime + elapsed_space +
                allocatedMB + amb_space + finalStatus);

        mySqlWrapper.updateValue(app.getId(),  app.getId() + QUEUE, app.getQueue());
        mySqlWrapper.updateValue(app.getId(),  app.getId() + STATE, app.getState().toLowerCase());
        mySqlWrapper.updateValue(app.getId(),  app.getId() + NAME, app.getName());
        mySqlWrapper.updateValue(app.getId(),  app.getId() + NUM_CONTAINERS,
                Integer.toString(app.getRunningContainers()));
        mySqlWrapper.updateValue(app.getId(),  app.getId() + TIMESTAMP,
                Long.toString(System.currentTimeMillis()));
        mySqlWrapper.updateValue(app.getId(),  app.getId() + ELAPSED_TIME,
                Long.toString(app.getElapsedTime()));
        mySqlWrapper.updateValue(app.getId(),  app.getId() + ALLOCATED_MB,
                Integer.toString(app.getAllocatedMB()));

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(app.getId() + "_log.txt", true));
            writer.write(stringToWrite.toString());
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
