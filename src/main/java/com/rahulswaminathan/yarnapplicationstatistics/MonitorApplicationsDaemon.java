package com.rahulswaminathan.yarnapplicationstatistics;

/**
 * Created by rahulswaminathan on 1/14/15.
 */
public class MonitorApplicationsDaemon {

    public MonitorApplicationsDaemon() {

    }

    public void run() {
        System.out.println("monitor applications daemon is running");

        ApplicationListener myAppListener = new ApplicationListener() {
            @Override
            public void onAppBegin(Apps.app app) {

            }

            @Override
            public void onAppFinish(Apps.app app) {

            }

            @Override
            public void onAppChangeState(Apps.app app) {

            }

            @Override
            public void onAppChangeContainers(Apps.app app) {

            }
        };

        myAppListener.startListening();
    }
}
