package com.yw.unlimitedproxy.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityUtils {

    private final List<AppCompatActivity> activities = new ArrayList<>();

    private ActivityUtils() {

    }

    private static final ActivityUtils activityUtils = new ActivityUtils();

    public static ActivityUtils getInstance(){
        return activityUtils;
    }

    public void addTask(AppCompatActivity activity){
        activities.add(activity);
    }

    public void removeTask(AppCompatActivity activity){
        activities.remove(activity);
    }

    public void exit(){
        for (AppCompatActivity activity : activities) {
            activity.finish();
        }
    }
}
