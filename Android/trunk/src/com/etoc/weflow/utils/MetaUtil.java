package com.etoc.weflow.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.etoc.weflow.WeFlowApplication;

public class MetaUtil {
    public static String getStringValue(String key) {
        Bundle metaData = null;
        String value = null;
        try {
            ApplicationInfo ai = WeFlowApplication.getAppInstance().getPackageManager().getApplicationInfo(
            		WeFlowApplication.getAppInstance().getPackageName(), PackageManager.GET_META_DATA);
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
            ApplicationInfo ai = WeFlowApplication.getAppInstance().getPackageManager().getApplicationInfo(
            		WeFlowApplication.getAppInstance().getPackageName(), PackageManager.GET_META_DATA);
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
