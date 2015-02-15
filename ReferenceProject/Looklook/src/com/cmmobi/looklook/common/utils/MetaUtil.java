package com.cmmobi.looklook.common.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.cmmobi.looklook.MainApplication;

public class MetaUtil {
    public static String getStringValue(String key) {
        Bundle metaData = null;
        String value = null;
        try {
            ApplicationInfo ai = MainApplication.getAppInstance().getPackageManager().getApplicationInfo(
            		MainApplication.getAppInstance().getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai)
                metaData = ai.metaData;
            if (null != metaData) {
                value = metaData.getString(key);
                return value;
            }
        } catch (NameNotFoundException e) {

        }
        return value;
    }
    
    public static int getIntValue(String key) {
        Bundle metaData = null;
        int value = 0;
        try {
            ApplicationInfo ai = MainApplication.getAppInstance().getPackageManager().getApplicationInfo(
            		MainApplication.getAppInstance().getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai)
                metaData = ai.metaData;
            if (null != metaData) {
                value = metaData.getInt(key);
                return value;
            }
        } catch (NameNotFoundException e) {

        }
        return value;
    }
}
