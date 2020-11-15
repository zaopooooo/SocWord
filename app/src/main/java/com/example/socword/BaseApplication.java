package com.example.socword;

import android.app.Activity;
import android.app.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseApplication extends Application {
    private static Map<String, Activity> destroyMap = new HashMap<>();

    public static void addDestroyActivity(Activity activity,String activityName){
        destroyMap.put(activityName,activity);
    }


    public static void destroyActivity(String activityName){
        Set<String> keySet = destroyMap.keySet();
        for (String key : keySet){
            destroyMap.get(key).finish();
        }
    }





}
