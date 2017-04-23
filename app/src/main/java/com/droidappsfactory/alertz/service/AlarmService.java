package com.droidappsfactory.alertz.service;


import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;

import com.droidappsfactory.alertz.AlarmAlertActivity;
import com.droidappsfactory.alertz.R;
import com.droidappsfactory.alertz.beans.Alarm;
import com.droidappsfactory.alertz.database.DBColumns;

public class AlarmService extends IntentService {
    private NotificationManager alarmNotificationManager;

    public AlarmService() {
        super("AlarmService");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onHandleIntent(Intent intent) {


        Uri singleUri = intent.getData();
        String selection = DBColumns._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(singleUri.getLastPathSegment())};

        Cursor cursor = getContentResolver().query(singleUri,null,selection,selectionArgs,null);
        if(cursor!=null){
            cursor.moveToFirst();
            final Alarm alarm = new Alarm(cursor.getInt(cursor.getColumnIndex(DBColumns._ID)), cursor.getString(cursor.getColumnIndex(DBColumns.TITLE)),
                    cursor.getString(cursor.getColumnIndex(DBColumns.DESC)), cursor.getLong(cursor.getColumnIndex(DBColumns.TIME))
                    , cursor.getInt(cursor.getColumnIndex(DBColumns.ENABLED)),cursor.getString(cursor.getColumnIndex(DBColumns.IMAGE_LINK))
            ,cursor.getString(cursor.getColumnIndex(DBColumns.VIDEO_LINK)));

            sendNotification(alarm,singleUri);
        }



    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(Alarm alarm,Uri uri) {

        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(this, AlarmAlertActivity.class);
        resultIntent.setData(uri);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(resultIntent);

      /*  TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(AlarmAlertActivity.class);*/

       // stackBuilder.addNextIntent(resultIntent);
    //    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder alamNotificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(
                this).setContentTitle(alarm.title).setSmallIcon(R.drawable.alarm)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(alarm.desc))
                .setContentText(alarm.desc);

   //     alamNotificationBuilder.setContentIntent(resultPendingIntent);
        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
    }
}
