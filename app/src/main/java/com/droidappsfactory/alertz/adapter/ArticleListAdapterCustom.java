package com.droidappsfactory.alertz.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.droidappsfactory.alertz.broadcasts.AlarmReceiver;
import com.droidappsfactory.alertz.R;
import com.droidappsfactory.alertz.beans.Alarm;
import com.droidappsfactory.alertz.database.DBColumns;
import com.droidappsfactory.alertz.fragments.SetAlert;
import com.droidappsfactory.alertz.provider.AlertProvider;
import com.droidappsfactory.alertz.util.LogHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;


/**
 * Created by tcsmans on 8/10/2016.
 */
public class ArticleListAdapterCustom extends CustomRecyclerViewAdapter<ArticleListAdapterCustom.ViewHolder> {

    private LogHelper logHelper = new LogHelper(ArticleListAdapterCustom.class.getSimpleName());

    private Context context;
    private Cursor cursor;

    AlarmManager alarmManager;


    AlarmReceiver alarmReceiver;
    SetAlert.OnAlarmSetListner listener;


    public ArticleListAdapterCustom(Context context, Cursor cursor, SetAlert.OnAlarmSetListner listener) {
        super(context, cursor);

        this.context = context;
        this.cursor = cursor;
        this.listener = listener;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
        int position = holder.getAdapterPosition();

        cursor.moveToPosition(position);
        final Alarm alarm = new Alarm(cursor.getInt(cursor.getColumnIndex(DBColumns._ID)), cursor.getString(cursor.getColumnIndex(DBColumns.TITLE)),
                cursor.getString(cursor.getColumnIndex(DBColumns.DESC)), cursor.getLong(cursor.getColumnIndex(DBColumns.TIME))
                , cursor.getInt(cursor.getColumnIndex(DBColumns.ENABLED)),cursor.getString(cursor.getColumnIndex(DBColumns.IMAGE_LINK))
                ,cursor.getString(cursor.getColumnIndex(DBColumns.VIDEO_LINK)));


        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(alarm.time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String dateformatted = dateFormat.format(calendar.getTime());
        holder.tv_title.setText(alarm.title);
        holder.tv_desc.setText(alarm.desc);
        holder.tv_date.setText(dateformatted);
        if (alarm.enabled == 0) {
            holder.switchCompat.setChecked(true);
        } else {
            holder.switchCompat.setChecked(false);
        }
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String timeFormatted = timeFormat.format(calendar.getTime());
        holder.tv_time.setText(timeFormatted);

        holder.switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logHelper.printLog("switch value" + alarm.enabled);
                int updateValue = (alarm.enabled == 0) ? 1 : 0;

                logHelper.printLog("int value " + updateValue);

                Uri singleUri = Uri.withAppendedPath(AlertProvider.CONTENT_ALERTZ_URI, String.valueOf(alarm.id));
                ContentValues values = new ContentValues();
                values.put(DBColumns.ENABLED, updateValue);
                String selection = DBColumns._ID + " LIKE ?";
                String[] selectionArgs = {String.valueOf(alarm.id)};
                logHelper.printLog(singleUri+"");
                int status = context.getContentResolver().update(singleUri, values, selection, selectionArgs);
                if (updateValue == 1) {
                    cancelAlarm(alarm.id);
                } else {
                    setAlarm(alarm.id, alarm.time,singleUri);
                }

            }
        });
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri singleUri = Uri.withAppendedPath(AlertProvider.CONTENT_ALERTZ_URI, String.valueOf(alarm.id));
                String selection = DBColumns._ID + " LIKE ?";
                String[] selectionArgs = {String.valueOf(alarm.id)};
                int status = context.getContentResolver().delete(singleUri, selection, selectionArgs);
                cancelAlarm(alarm.id);
            }
        });

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View cardView = LayoutInflater.from(context).inflate(R.layout.row_alertz, parent, false);
        return new ViewHolder(cardView);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title, tv_desc, tv_time, tv_date;
        SwitchCompat switchCompat;
        ImageView iv_delete;
        RelativeLayout lt_alertz;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_desc = (TextView) itemView.findViewById(R.id.tv_desc);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            switchCompat = (SwitchCompat) itemView.findViewById(R.id.sw_enable);
            iv_delete = (ImageView) itemView.findViewById(R.id.iv_delete);
            lt_alertz = (RelativeLayout) itemView.findViewById(R.id.lt_alert);
        }
    }


    void cancelAlarm(int tag) {
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, tag, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        listener.onAlarmOff("Disabled");
    }

    void setAlarm(int tag, long time,Uri uri) {
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        myIntent.setData(uri);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, tag, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        listener.onAlarmSet("Enabled");
    }

}
