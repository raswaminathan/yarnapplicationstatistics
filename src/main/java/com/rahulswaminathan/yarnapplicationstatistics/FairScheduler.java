package com.rahulswaminathan.yarnapplicationstatistics;

/**
 * Created by rahulswaminathan on 11/12/15.
 */
public class FairScheduler {

    private fairscheduler scheduler = null;

    public FairScheduler() {

    }

    public static class fairscheduler {

        private FairSchedulerInfo schedulerInfo = null;

        public fairscheduler() {

        }
    }

    public static class FairSchedulerInfo {

        private String type;

        public FairSchedulerInfo() {

        }
    }


    public static class RootQueue {

        private int maxApps;
        private String queueName;
        private String schedulingPolicy;
        private MinResources minResources;
        private MaxResources maxResources;
        private UsedResources usedResources;
        private SteadyFairResources steadyFairResources;
        private FairResources fairResources;
        private ClusterResources clusterResources;
        private ChildQueue[] childQueues;

        public RootQueue() {

        }
    }

    public static class MinResources {

        private int memory;
        private int vCores;

        public MinResources() {

        }
    }

    public static class MaxResources {

        private int memory;
        private int vCores;

        public MaxResources() {

        }
    }

    public static class UsedResources {

        private int memory;
        private int vCores;

        public UsedResources() {

        }
    }

    public static class SteadyFairResources {

        private int memory;
        private int vCores;

        public SteadyFairResources() {

        }
    }

    public static class FairResources {

        private int memory;
        private int vCores;

        public FairResources() {

        }
    }

    public static class ClusterResources {

        private int memory;
        private int vCores;

        public ClusterResources() {

        }
    }

    public static class ChildQueue {
        private MinResources minResources;
        private MaxResources maxResources;
        private UsedResources usedResources;
        private SteadyFairResources steadyFairResources;
        private FairResources fairResources;
        private ClusterResources clusterResources;
        private String type;
        private int maxApps;
        private String queueName;
        private String schedulingPolicy;
        private int numPendingApps;
        private int numActiveApps;

        public ChildQueue() {

        }
    }
}
