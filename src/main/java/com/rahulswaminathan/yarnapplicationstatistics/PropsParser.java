package com.rahulswaminathan.yarnapplicationstatistics;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by rahulswaminathan on 1/30/15.
 *
 * Class that parses all of the properties in conf.properties. Additional properties should be added to this class for
 * proper parsing.
 */

public class PropsParser {
    private String[] queues;
    private String emem;
    private String dmem;
    // total number of jobs each queue performs
    private Integer numIterations;
    private String sparkMaster;
    private String yarnWEBUI;

    public PropsParser() {
        try {
            Properties props = new Properties();
            String propFileName = "conf.properties";

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            if (inputStream == null) {
                System.err.println("FILE NOT FOUND");
                //throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            props.load(inputStream);

            queues = props.getProperty("queues").split(",");
            emem = props.getProperty("emem");
            dmem = props.getProperty("dmem");
            numIterations = Integer.parseInt(props.getProperty("numIterations"));
            sparkMaster = props.getProperty("sparkMaster");
            yarnWEBUI = props.getProperty("yarnWEBUI");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getNumIterations() {
        return numIterations;
    }

    public String[] getQueues() {
        return queues;
    }

    public String getEmem() {
        return emem;
    }

    public String getDmem() {
        return dmem;
    }

    public String getSparkMaster() {
        return sparkMaster;
    }

    public String getYarnWEBUI() {
        return yarnWEBUI;
    }
}
