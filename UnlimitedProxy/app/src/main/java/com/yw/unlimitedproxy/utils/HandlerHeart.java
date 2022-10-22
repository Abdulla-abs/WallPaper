package com.yw.unlimitedproxy.utils;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.Date;

public class HandlerHeart extends Handler implements OpenVPNUtils.OnVpnListener {

    //修改上限时间？ 修改此值
    public static int MAX_TIME = TimeUtils.hour * 2;

    private int duration = 1000;
    private int runningTime = 0;
    private String remainingTime = TimeUtils.convertIntTimeToStringTime(MAX_TIME);
    private boolean toggle = false;
    private OnTimeChangedListener listener;

    private int saveStepTime = 60;
    private int tempCounter = 0;

    private boolean isNewDay = false;
    private final SPUtils spUtils = new SPUtils();
    private boolean isWaringTime = false;    //是否是警告时间
    private boolean isTimeOver = false;  //是否是结束时间

    private static final int START = 1;

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case START:
                if (toggle) {
                    runningTime += 1000;
                    tempCounter += 1;
                    if (tempCounter >= saveStepTime){
                        saveTimeAndStatus();
                        tempCounter = 0;
                        LogUtils.e("Heart","保存数据");
                    }
                    if (listener != null) {
                        listener.onTimeChang(TimeUtils.convertRunningTimeToRemainingTime(runningTime, MAX_TIME));
                        LogUtils.e("Heart",runningTime);
                        if (isWaringTime || TimeUtils.culWarmingTime(runningTime, MAX_TIME)) {
                            if (!isWaringTime) {
                                isWaringTime = true;
                                spUtils.putBoolean(SPUtils.WARMING_TIME, true);
                                listener.onRemainingTimeWaring();
                                LogUtils.e("isWaringTime", "向下分发预警时间");
                            }
                        }
                        if (isTimeOver || runningTime - MAX_TIME >= 0) {
                            if (!isTimeOver) {
                                isTimeOver = true;
                                spUtils.putBoolean(SPUtils.OVER_TIME, true);
                                listener.onRemainingOver();
                                LogUtils.e("isWaringTime", "向下分发结束时间");
                            }
                        }
                    }
                    start();
                } else {
                    LogUtils.e("stop", "heart");
                }
                break;
        }
    }

    public HandlerHeart init() {
        //是否新的一天
        if (spUtils.isNewDay()) {
            isNewDay = true;
            spUtils.putString(SPUtils.NEW_DAY, TimeUtils.ymd.format(new Date()));
            spUtils.putInt(SPUtils.RUNNING_TIME, runningTime);
            spUtils.putBoolean(SPUtils.WARMING_TIME, false);
            spUtils.putBoolean(SPUtils.OVER_TIME, false);
        } else {
            runningTime = spUtils.getInt(SPUtils.RUNNING_TIME,MAX_TIME);
            isWaringTime = MAX_TIME - runningTime < 30 * TimeUtils.min;
            isTimeOver = runningTime - MAX_TIME >= 0;
            isWaringTime = spUtils.getBoolean(SPUtils.WARMING_TIME, true);
            isTimeOver = spUtils.getBoolean(SPUtils.OVER_TIME, true);
            remainingTime = TimeUtils.convertRunningTimeToRemainingTime(
                    runningTime,
                    MAX_TIME
            );
        }
        OpenVPNUtils.getInstance().registerVpnListener(this);
        return this;
    }

    private void start() {
        toggle = true;
        sendEmptyMessageDelayed(START, duration);
    }

    private void stop() {
        toggle = false;
        saveTimeAndStatus();
    }

    public void saveTimeAndStatus() {
        spUtils.putInt(SPUtils.RUNNING_TIME, runningTime);
        spUtils.putBoolean(SPUtils.WARMING_TIME, isWaringTime);
        spUtils.putBoolean(SPUtils.OVER_TIME, isWaringTime);
    }

    public void test(int maxTime,int runTime) {
        if (runTime != 0) {
            runningTime = runTime * TimeUtils.min;
        }
        if (maxTime != 0) {
            MAX_TIME = maxTime * TimeUtils.min;
        }
        isWaringTime = false;
        isTimeOver = false;
        saveTimeAndStatus();
    }

//    @Override
    public void onNoServerFile() {

    }

//    @Override
    public void onVpnPermissionRequest(Intent intent) {

    }

    @Override
    public void onVpnConnecting() {

    }

    @Override
    public void onVpnStarted() {
        start();
    }


    @Override
    public void onVpnDisconnected() {
        stop();
    }


    public interface OnTimeChangedListener {
        void onTimeChang(String remainingTime);

        void onRemainingTimeWaring();

        void onRemainingOver();
    }

    public boolean isNewDay() {
        return isNewDay;
    }

    public int getDuration() {
        return duration;
    }

    public HandlerHeart setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public int getRunningTime() {
        return runningTime;
    }

    public HandlerHeart setRunningTime(int runningTime) {
        this.runningTime = runningTime;
        return this;
    }

    public HandlerHeart setSaveStepTime(int saveStepTime) {
        this.saveStepTime = saveStepTime;
        return this;
    }

    public boolean isToggle() {
        return toggle;
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }

    public OnTimeChangedListener getListener() {
        return listener;
    }

    public HandlerHeart setListener(OnTimeChangedListener listener) {
        this.listener = listener;
        return this;
    }

    public boolean isIsWaringTime() {
        return isWaringTime;
    }

    public boolean isIsTimeOver() {
        return isTimeOver;
    }

    public String getRemainingTime() {
        return remainingTime;
    }
}
