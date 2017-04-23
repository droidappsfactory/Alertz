package com.droidappsfactory.alertz.broadcasts;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.droidappsfactory.alertz.service.AlarmService;
import com.droidappsfactory.alertz.util.LogHelper;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

public class AlarmReceiver extends BroadcastReceiver {

    LogHelper logHelper = new LogHelper(AlarmReceiver.class.getSimpleName());




    @Override
    public void onReceive(Context context, Intent intent) {
        logHelper.printLog("Alarm Service "+intent.getData().toString());
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }




}
