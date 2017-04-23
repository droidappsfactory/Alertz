package com.droidappsfactory.alertz;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.droidappsfactory.alertz.broadcasts.AlarmReceiver;
import com.droidappsfactory.alertz.fragments.AlertzList;
import com.droidappsfactory.alertz.fragments.SetAlert;
import com.droidappsfactory.alertz.util.Const;

public class AlertzActivity extends AppCompatActivity  implements AlertzList.OnAlertAddListener ,SetAlert.OnAlarmSetListner {


    Context context;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    FragmentManager fragmentManager;
    CoordinatorLayout coordinatorLayout;

   // long setTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();






    }




    void cancelAlarm(){
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
    private void init() {
        context = AlertzActivity.this;
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorlt);
        AlertzList alertzList = AlertzList.newInstance(this,this);
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container,alertzList, Const.LIST_TAG).commit();
    }

    @Override
    public void onAddClickListener() {
        SetAlert setAlert = SetAlert.newInstance(this);
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container,setAlert, Const.SETLAERT_TAG).addToBackStack(Const.SETLAERT_TAG).commit();
    }


    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount()>0){
            getFragmentManager().popBackStack();

        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onAlarmSet(String time) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout,"Alert is enabled"+time,Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onAlarmOff(String txt) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout,"Alert is disabled.",Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
