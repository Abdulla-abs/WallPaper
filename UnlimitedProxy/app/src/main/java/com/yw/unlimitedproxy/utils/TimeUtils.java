package com.yw.unlimitedproxy.utils;

import com.yw.unlimitedproxy.App;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static final int sec = 1000;
    public static final int min = 1000 * 60;
    public static final int hour = 1000 * 60 * 60;

    public static final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public static String convertRunningTimeToRemainingTime(String runTime, String duration, String maxTime) {
        return convertRunningTimeToRemainingTime(culAllRunTime(runTime, duration), maxTime);
    }

    public static String convertRunningTimeToRemainingTime(String allRunTime, String maxTime) {
        return convertRunningTimeToRemainingTime(allRunTime, convertStringTimeToLong(maxTime));
    }

    public static String convertRunningTimeToRemainingTime(int allRunTime, String maxTime) {
        return convertRunningTimeToRemainingTime(allRunTime, convertStringTimeToLong(maxTime));
    }

    public static String convertRunningTimeToRemainingTime(String allRunTime, int maxTime) {
        return convertRunningTimeToRemainingTime(convertStringTimeToLong(allRunTime), maxTime);
    }

    public static String convertRunningTimeToRemainingTime(int allRunTime, int maxTime) {
        int ram = maxTime - allRunTime;
        if (ram<=0) ram = 0;
        return "" +
                convertTwoDigit((int) ((ram / hour) % 24)) +
                ":" +
                convertTwoDigit((int) ((ram / min) % 60)) +
                ":" +
                convertTwoDigit((int) ((ram / sec) % 60));
    }

    public static String convertIntTimeToStringTime(int allRunTime) {
        return "" +
                convertTwoDigit((int) ((allRunTime / hour) % 24)) +
                ":" +
                convertTwoDigit((int) ((allRunTime / min) % 60)) +
                ":" +
                convertTwoDigit((int) ((allRunTime / sec) % 60));
    }


//    public static String convertRunningTimeToRemainingTime(String runTime,String duration,int maxTime){
//        return convertTwoDigit(maxTime - culAllRunTime(runTime,duration));
//    }

//    public static String convertRunningTimeToRemainingTime(int allTime,int maxTime){
//        return convertTwoDigit(maxTime - allTime);
//    }

//    public static String convertRunningTimeToRemainingTime(String allTime,int maxTime){
//        return convertTwoDigit(maxTime - convertStringTimeToLong(allTime));
//    }

    public static boolean culWarmingTime(int allRunTime,int maxTime){
        return maxTime - allRunTime < hour/2;
    }

    public static int culAllRunTime(String runTime, String duration) {
        return convertStringTimeToLong(runTime) + convertStringTimeToLong(duration);
    }

    public static int culAllRunTime(int runTime, String duration) {
        return runTime + convertStringTimeToLong(duration);
    }

    public static String convertTwoDigit(int value) {
        if (value < 10) return "0" + value;
        else return value + "";
    }

    public static int convertStringTimeToLong(String time) {
        String[] split = time.split(":");
        int h = Integer.parseInt(split[0]) * hour;
        int m = Integer.parseInt(split[1]) * min;
        int s = Integer.parseInt(split[2]) * sec;
        return h + m + s;
    }
}
