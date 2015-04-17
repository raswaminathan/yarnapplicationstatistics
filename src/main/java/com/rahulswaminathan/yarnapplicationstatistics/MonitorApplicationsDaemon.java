package com.rahulswaminathan.yarnapplicationstatistics;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by rahulswaminathan on 1/14/15.
 */
public class MonitorApplicationsDaemon {

    private static final String TEMP_GAUGES_FILE = "gauges_temp.properties";
    private static final String QUEUE = "__queue";
    private static final String STATE = "__state";
    private static final String NUM_CONTAINERS = "__numContainers";
    private static final String TIMESTAMP = "__timeStamp";
    private static final String ELAPSED_TIME = "__elapsedTime";
    private static final String ALLOCATED_MB = "__allocatedMB";
    private static final String DATABASE_NAME = "test";
    private static final String MYSQL_USERNAME = "root";
    private static final String SERVER_LOCATION = "localhost";
    private SQLWrapper mySqlWrapper;

    public MonitorApplicationsDaemon() {
        mySqlWrapper = new SQLWrapper(DATABASE_NAME , SERVER_LOCATION, MYSQL_USERNAME);
    }

    public void run() {
        System.out.println("monitor applications daemon is running");

        ApplicationListener myAppListener = new ApplicationListener() {
            @Override
            public void onAppBegin(Apps.app app) {
                    mySqlWrapper.createAppsTable(app.getId());
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

    private void initializeSQLTable(Apps.app app) {
        mySqlWrapper.insertIntoTable(app.getId(),  app.getId() + QUEUE, app.getQueue());
        mySqlWrapper.insertIntoTable(app.getId(),  app.getId() + STATE, app.getState().toLowerCase());
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
        mySqlWrapper.updateValue(app.getId(),  app.getId() + QUEUE, app.getQueue());
        mySqlWrapper.updateValue(app.getId(),  app.getId() + STATE, app.getState().toLowerCase());
        mySqlWrapper.updateValue(app.getId(),  app.getId() + NUM_CONTAINERS,
                Integer.toString(app.getRunningContainers()));
        mySqlWrapper.updateValue(app.getId(),  app.getId() + TIMESTAMP,
                Long.toString(System.currentTimeMillis()));
        mySqlWrapper.updateValue(app.getId(),  app.getId() + ELAPSED_TIME,
                Long.toString(app.getElapsedTime()));
        mySqlWrapper.updateValue(app.getId(),  app.getId() + ALLOCATED_MB,
                Integer.toString(app.getAllocatedMB()));
    }
}
