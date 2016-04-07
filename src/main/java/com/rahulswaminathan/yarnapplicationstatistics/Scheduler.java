package com.rahulswaminathan.yarnapplicationstatistics;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahulswaminathan on 10/29/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class Scheduler {

    private scheduler scheduler = null;

    public Scheduler() {
    }

    public scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public static class scheduler {
        private schedulerInfo schedulerInfo = null;

        public scheduler() {
        }

        public schedulerInfo getSchedulerInfo() {
            return schedulerInfo;
        }

        public void setSchedulerInfo(schedulerInfo schedulerInfo) {
            this.schedulerInfo = schedulerInfo;
        }
    }

    public static class schedulerInfo {
        private String type;
        private String capacity;
        private String usedCapacity;
        private String maxCapacity;
        private String queueName;
        private queue[] queues;

        public schedulerInfo() {
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCapacity() {
            return capacity;
        }

        public void setCapacity(String capacity) {
            this.capacity = capacity;
        }

        public String getUsedCapacity() {
            return usedCapacity;
        }

        public void setUsedCapacity(String usedCapacity) {
            this.usedCapacity = usedCapacity;
        }

        public String getMaxCapacity() {
            return maxCapacity;
        }

        public void setMaxCapacity(String maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        public String getQueueName() {
            return queueName;
        }

        public void setQueueName(String queueName) {
            this.queueName = queueName;
        }

        public queue[] getQueues() {
            return queues;
        }

        public void setQueues(queue[] queues) {
            this.queues = queues;
        }
    }

    public static class queue {
        private float capacity;
        private float usedCapacity;
        private float maxCapacity;
        private float absoluteCapacity;
        private float absoluteMaxCapacity;
        private float absoluteUsedCapacity;
        private int numApplications;
        private String usedResources;
        private String queueName;
        private String state;
        private queue[] queues;
        private resource resourcesUsed;
        private String type;
        private int numActiveApplications;
        private int numPendingApplications;
        private int numContainers;
        private int maxApplications;
        private int maxApplicationsPerUser;
        private int maxActiveApplications;
        private int maxActiveApplicationsPerUser;
        private int userLimit;
        private float userLimitFactor;
        private user users;

        public queue() {
        }

        public float getCapacity() {
            return capacity;
        }

        public void setCapacity(float capacity) {
            this.capacity = capacity;
        }

        public float getUsedCapacity() {
            return usedCapacity;
        }

        public void setUsedCapacity(float usedCapacity) {
            this.usedCapacity = usedCapacity;
        }

        public float getMaxCapacity() {
            return maxCapacity;
        }

        public void setMaxCapacity(float maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        public float getAbsoluteCapacity() {
            return absoluteCapacity;
        }

        public void setAbsoluteCapacity(float absoluteCapacity) {
            this.absoluteCapacity = absoluteCapacity;
        }

        public float getAbsoluteMaxCapacity() {
            return absoluteMaxCapacity;
        }

        public void setAbsoluteMaxCapacity(float absoluteMaxCapacity) {
            this.absoluteMaxCapacity = absoluteMaxCapacity;
        }

        public float getAbsoluteUsedCapacity() {
            return absoluteUsedCapacity;
        }

        public void setAbsoluteUsedCapacity(float absoluteUsedCapacity) {
            this.absoluteUsedCapacity = absoluteUsedCapacity;
        }

        public int getNumApplications() {
            return numApplications;
        }

        public void setNumApplications(int numApplications) {
            this.numApplications = numApplications;
        }

        public String getUsedResources() {
            return usedResources;
        }

        public void setUsedResources(String usedResources) {
            this.usedResources = usedResources;
        }

        public String getQueueName() {
            return queueName;
        }

        public void setQueueName(String queueName) {
            this.queueName = queueName;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public queue[] getQueues() {
            return queues;
        }

        public void setQueues(queue[] queues) {
            this.queues = queues;
        }

        public resource getResourcesUsed() {
            return resourcesUsed;
        }

        public void setResourcesUsed(resource resourcesUsed) {
            this.resourcesUsed = resourcesUsed;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getNumActiveApplications() {
            return numActiveApplications;
        }

        public void setNumActiveApplications(int numActiveApplications) {
            this.numActiveApplications = numActiveApplications;
        }

        public int getNumPendingApplications() {
            return numPendingApplications;
        }

        public void setNumPendingApplications(int numPendingApplications) {
            this.numPendingApplications = numPendingApplications;
        }

        public int getNumContainers() {
            return numContainers;
        }

        public void setNumContainers(int numContainers) {
            this.numContainers = numContainers;
        }

        public int getMaxApplications() {
            return maxApplications;
        }

        public void setMaxApplications(int maxApplications) {
            this.maxApplications = maxApplications;
        }

        public int getMaxApplicationsPerUser() {
            return maxApplicationsPerUser;
        }

        public void setMaxApplicationsPerUser(int maxApplicationsPerUser) {
            this.maxApplicationsPerUser = maxApplicationsPerUser;
        }

        public int getMaxActiveApplications() {
            return maxActiveApplications;
        }

        public void setMaxActiveApplications(int maxActiveApplications) {
            this.maxActiveApplications = maxActiveApplications;
        }

        public int getMaxActiveApplicationsPerUser() {
            return maxActiveApplicationsPerUser;
        }

        public void setMaxActiveApplicationsPerUser(int maxActiveApplicationsPerUser) {
            this.maxActiveApplicationsPerUser = maxActiveApplicationsPerUser;
        }

        public int getUserLimit() {
            return userLimit;
        }

        public void setUserLimit(int userLimit) {
            this.userLimit = userLimit;
        }

        public float getUserLimitFactor() {
            return userLimitFactor;
        }

        public void setUserLimitFactor(float userLimitFactor) {
            this.userLimitFactor = userLimitFactor;
        }

        public user getUsers() {
            return users;
        }

        public void setUsers(user users) {
            this.users = users;
        }
    }

    public static class user {
        private String username;
        private resource resourcesUsed;
        private int numActiveApplications;
        private int numPendingApplications;

        public user() {
        }

        public resource getResourcesUsed() {
            return resourcesUsed;
        }

        public void setResourcesUsed(resource resourcesUsed) {
            this.resourcesUsed = resourcesUsed;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getNumActiveApplications() {
            return numActiveApplications;
        }

        public void setNumActiveApplications(int numActiveApplications) {
            this.numActiveApplications = numActiveApplications;
        }

        public int getNumPendingApplications() {
            return numPendingApplications;
        }

        public void setNumPendingApplications(int numPendingApplications) {
            this.numPendingApplications = numPendingApplications;
        }
    }

    public static class resource {
        private int memory;
        private int vCores;

        public resource() {
        }

        public int getMemory() {
            return memory;
        }

        public void setMemory(int memory) {
            this.memory = memory;
        }

        public int getvCores() {
            return vCores;
        }

        public void setvCores(int vCores) {
            this.vCores = vCores;
        }
    }
}