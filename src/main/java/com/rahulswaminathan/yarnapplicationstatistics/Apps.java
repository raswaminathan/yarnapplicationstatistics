package com.rahulswaminathan.yarnapplicationstatistics;

/**
 * Created by rahulswaminathan on 11/19/14.
 */
public class Apps {
    private apps apps;

    public Apps() {
    }

    public Apps.apps getApps() {
        return apps;
    }

    public void setApps(Apps.apps apps) {
        this.apps = apps;
    }

    public static class apps {
        private app[] app;

        public apps() {
        }

        public Apps.app[] getApp() {
            return app;
        }

        public void setApp(Apps.app[] app) {
            this.app = app;
        }
    }

    public static class app {
        private long finishedTime;
        private String amContainerLogs;
        private String trackingUI;
        private String state;
        private String user;
        private String id;
        private long clusterId;
        private String finalStatus;
        private String amHostHttpAddress;
        private float progress;
        private String name;
        private long startedTime;
        private long elapsedTime;
        private String diagnostics;
        private String trackingURL;
        private String queue;
        private int allocatedMB;
        private int allocatedVCores;
        private int runningContainers;

        public app(){

        }

        public int hashCode() {
            return this.id.hashCode();
        }

        public boolean equals(Object other) {
            if (! (other instanceof Apps.app))
                return false;
            Apps.app otherApp = (Apps.app) other;
            return this.hashCode() == otherApp.hashCode();
        }

        public long getFinishedTime() {
            return finishedTime;
        }

        public void setFinishedTime(long finishedTime) {
            this.finishedTime = finishedTime;
        }

        public String getAmContainerLogs() {
            return amContainerLogs;
        }

        public void setAmContainerLogs(String amContainerLogs) {
            this.amContainerLogs = amContainerLogs;
        }

        public String getTrackingUI() {
            return trackingUI;
        }

        public void setTrackingUI(String trackingUI) {
            this.trackingUI = trackingUI;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public long getClusterId() {
            return clusterId;
        }

        public void setClusterId(long clusterId) {
            this.clusterId = clusterId;
        }

        public String getFinalStatus() {
            return finalStatus;
        }

        public void setFinalStatus(String finalStatus) {
            this.finalStatus = finalStatus;
        }

        public String getAmHostHttpAddress() {
            return amHostHttpAddress;
        }

        public void setAmHostHttpAddress(String amHostHttpAddress) {
            this.amHostHttpAddress = amHostHttpAddress;
        }

        public float getProgress() {
            return progress;
        }

        public void setProgress(float progress) {
            this.progress = progress;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getStartedTime() {
            return startedTime;
        }

        public void setStartedTime(long startedTime) {
            this.startedTime = startedTime;
        }

        public long getElapsedTime() {
            return elapsedTime;
        }

        public void setElapsedTime(long elapsedTime) {
            this.elapsedTime = elapsedTime;
        }

        public String getDiagnostics() {
            return diagnostics;
        }

        public void setDiagnostics(String diagnostics) {
            this.diagnostics = diagnostics;
        }

        public String getTrackingURL() {
            return trackingURL;
        }

        public void setTrackingURL(String trackingURL) {
            this.trackingURL = trackingURL;
        }

        public String getQueue() {
            return queue;
        }

        public void setQueue(String queue) {
            this.queue = queue;
        }

        public int getAllocatedMB() {
            return allocatedMB;
        }

        public void setAllocatedMB(int allocatedMB) {
            this.allocatedMB = allocatedMB;
        }

        public int getAllocatedVCores() {
            return allocatedVCores;
        }

        public void setAllocatedVCores(int allocatedVCores) {
            this.allocatedVCores = allocatedVCores;
        }

        public int getRunningContainers() {
            return runningContainers;
        }

        public void setRunningContainers(int runningContainers) {
            this.runningContainers = runningContainers;
        }
    }
}
