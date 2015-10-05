package com.rahulswaminathan.yarnapplicationstatistics;

/**
 * Created by rahulswaminathan on 10/21/14.
 */
public class ClusterMetrics {

    private clusterMetrics clusterMetrics = null;

    public clusterMetrics getClusterMetrics() {
        return clusterMetrics;
    }

    public void setClusterMetrics(clusterMetrics clusterMetrics) {
        this.clusterMetrics = clusterMetrics;
    }

    public class clusterMetrics {
        private int appsSubmitted = 0;
        private int appsCompleted = 0;
        private int appsPending = 0;
        private int appsRunning = 0;
        private int appsFailed = 0;
        private int appsKilled = 0;
        private long reservedMB = 0;
        private long availableMB = 0;
        private long allocatedMB = 0;
        private long totalMB = 0;
        private long reservedVirtualCores = 0;
        private long availableVirtualCores = 0;
        private long allocatedVirtualCores = 0;
        private long totalVirtualCores = 0;
        private int containersAllocated = 0;
        private int containersReserved = 0;
        private int containersPending = 0;
        private int totalNodes = 0;
        private int activeNodes = 0;
        private int lostNodes = 0;
        private int unhealthyNodes = 0;
        private int decommissionedNodes = 0;
        private int rebootedNodes = 0;

        public int getAppsSubmitted() {
            return appsSubmitted;
        }

        public void setAppsSubmitted(int appsSubmitted) {
            this.appsSubmitted = appsSubmitted;
        }

        public long getAllocatedVirtualCores() {
            return allocatedVirtualCores;
        }

        public void setAllocatedVirtualCores(long allocatedVirtualCores) {
            this.allocatedVirtualCores = allocatedVirtualCores;
        }

        public int getAppsCompleted() {
            return appsCompleted;
        }

        public void setAppsCompleted(int appsCompleted) {
            this.appsCompleted = appsCompleted;
        }

        public int getAppsPending() {
            return appsPending;
        }

        public void setAppsPending(int appsPending) {
            this.appsPending = appsPending;
        }

        public int getAppsRunning() {
            return appsRunning;
        }

        public void setAppsRunning(int appsRunning) {
            this.appsRunning = appsRunning;
        }

        public int getAppsFailed() {
            return appsFailed;
        }

        public void setAppsFailed(int appsFailed) {
            this.appsFailed = appsFailed;
        }

        public int getAppsKilled() {
            return appsKilled;
        }

        public void setAppsKilled(int appsKilled) {
            this.appsKilled = appsKilled;
        }

        public long getReservedMB() {
            return reservedMB;
        }

        public void setReservedMB(long reservedMB) {
            this.reservedMB = reservedMB;
        }

        public long getAvailableMB() {
            return availableMB;
        }

        public void setAvailableMB(long availableMB) {
            this.availableMB = availableMB;
        }

        public long getAllocatedMB() {
            return allocatedMB;
        }

        public void setAllocatedMB(long allocatedMB) {
            this.allocatedMB = allocatedMB;
        }

        public long getTotalMB() {
            return totalMB;
        }

        public void setTotalMB(long totalMB) {
            this.totalMB = totalMB;
        }

        public long getReservedVirtualCores() {
            return reservedVirtualCores;
        }

        public void setReservedVirtualCores(long reservedVirtualCores) {
            this.reservedVirtualCores = reservedVirtualCores;
        }

        public long getAvailableVirtualCores() {
            return availableVirtualCores;
        }

        public void setAvailableVirtualCores(long availableVirtualCores) {
            this.availableVirtualCores = availableVirtualCores;
        }

        public long getTotalVirtualCores() {
            return totalVirtualCores;
        }

        public void setTotalVirtualCores(long totalVirtualCores) {
            this.totalVirtualCores = totalVirtualCores;
        }

        public int getContainersAllocated() {
            return containersAllocated;
        }

        public void setContainersAllocated(int containersAllocated) {
            this.containersAllocated = containersAllocated;
        }

        public int getContainersReserved() {
            return containersReserved;
        }

        public void setContainersReserved(int containersReserved) {
            this.containersReserved = containersReserved;
        }

        public int getContainersPending() {
            return containersPending;
        }

        public void setContainersPending(int containersPending) {
            this.containersPending = containersPending;
        }

        public int getTotalNodes() {
            return totalNodes;
        }

        public void setTotalNodes(int totalNodes) {
            this.totalNodes = totalNodes;
        }

        public int getActiveNodes() {
            return activeNodes;
        }

        public void setActiveNodes(int activeNodes) {
            this.activeNodes = activeNodes;
        }

        public int getLostNodes() {
            return lostNodes;
        }

        public void setLostNodes(int lostNodes) {
            this.lostNodes = lostNodes;
        }

        public int getUnhealthyNodes() {
            return unhealthyNodes;
        }

        public void setUnhealthyNodes(int unhealthyNodes) {
            this.unhealthyNodes = unhealthyNodes;
        }

        public int getDecommissionedNodes() {
            return decommissionedNodes;
        }

        public void setDecommissionedNodes(int decommissionedNodes) {
            this.decommissionedNodes = decommissionedNodes;
        }

        public int getRebootedNodes() {
            return rebootedNodes;
        }

        public void setRebootedNodes(int rebootedNodes) {
            this.rebootedNodes = rebootedNodes;
        }
    }
 }