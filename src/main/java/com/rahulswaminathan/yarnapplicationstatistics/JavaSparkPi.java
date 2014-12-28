package com.rahulswaminathan.yarnapplicationstatistics;

/**
 * Created by rahulswaminathan on 12/1/14.
 */
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Computes an approximation to pi
 * Usage: JavaSparkPi [slices]
 */
public final class JavaSparkPi implements Serializable {

    private static final Pattern SPACE = Pattern.compile(" ");

    private int slices;
    public JavaSparkPi(int slices) {
        this.slices = slices;
    }

    public void doJob(JavaSparkContext jsc) {
        int n = 100000 * slices;
        List<Integer> l = new ArrayList<Integer>(n);
        for (int i = 0; i < n; i++) {
            l.add(i);
        }

        JavaRDD<Integer> dataSet = jsc.parallelize(l, slices);

        int count = dataSet.map(new Function<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                double x = Math.random() * 2 - 1;
                double y = Math.random() * 2 - 1;
                return (x * x + y * y < 1) ? 1 : 0;
            }
        }).reduce(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) {
                return integer + integer2;
            }
        });

        System.out.println("Pi is roughly " + 4.0 * count / n);

        jsc.stop();
//        String file = "~/BigData/README.md";
//        JavaRDD<String> lines = jsc.textFile(file, 1);
//
//        JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
//            @Override
//            public Iterable<String> call(String s) {
//                return Arrays.asList(SPACE.split(s));
//            }
//        });
//
//        JavaPairRDD<String, Integer> ones = words.mapToPair(new PairFunction<String, String, Integer>() {
//            @Override
//            public Tuple2<String, Integer> call(String s) {
//                return new Tuple2<String, Integer>(s, 1);
//            }
//        });
//
//        JavaPairRDD<String, Integer> counts = ones.reduceByKey(new Function2<Integer, Integer, Integer>() {
//            @Override
//            public Integer call(Integer i1, Integer i2) {
//                return i1 + i2;
//            }
//        });
//
//        List<Tuple2<String, Integer>> output = counts.collect();
//        for (Tuple2<?,?> tuple : output) {
//            System.out.println(tuple._1() + ": " + tuple._2());
//        }
//        jsc.stop();

//
//                String logFile = System.getenv("SPARK_HOME") + "/README.md";
//        JavaRDD<String> logData = jsc.textFile(logFile).cache();
//
//        System.out.println(logData.name());
//        long numAs = logData.filter(new Function<String, Boolean>() {
//            public Boolean call(String s) { return s.contains("a"); }
//        }).count();
//
//        long numBs = logData.filter(new Function<String, Boolean>() {
//            public Boolean call(String s) { return s.contains("b"); }
//        }).count();
//
//        System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);
//        jsc.stop();

    }
}