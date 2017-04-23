package com.droidappsfactory.alertz.util;

import android.util.Log;

/**
 * Created by tcsmans on 8/5/2016.
 */
public class LogHelper {

    private String tag;
    private boolean printLogs = true;

    public LogHelper(String tag){
        this.tag  =tag;
    }

    public void printLog(String value){
        if(printLogs){
            Log.d(tag,value);
        }
    }



}
