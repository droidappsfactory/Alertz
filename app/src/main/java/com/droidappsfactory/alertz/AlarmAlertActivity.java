package com.droidappsfactory.alertz;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.droidappsfactory.alertz.beans.Alarm;
import com.droidappsfactory.alertz.database.DBColumns;
import com.droidappsfactory.alertz.util.LogHelper;


public class AlarmAlertActivity extends AppCompatActivity{

    LogHelper logHelper  = new LogHelper(AlarmAlertActivity.class.getSimpleName());

    ImageView iv_display;
    Context context;
    TextView tv_title,tv_desc;
    Button bt_alarmoff;
    Alarm alarm;
    MediaController mediaPlayer;
    VideoView vv_play;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.displayalarm);
        init();





        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        final Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();



        bt_alarmoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ringtone.stop();
                Toast.makeText(context,"Alarm Stopped",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void init() {
        context = AlarmAlertActivity.this;
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_desc = (TextView)findViewById(R.id.tv_desc);
        bt_alarmoff = (Button)findViewById(R.id.bt_alarmoff);
        iv_display = (ImageView)findViewById(R.id.iv_display);
        vv_play = (VideoView)findViewById(R.id.vv_play);
        mediaPlayer = new MediaController(this);

        vv_play.setMediaController(mediaPlayer);
        Uri singleUri = getIntent().getData();
        String selection = DBColumns._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(singleUri.getLastPathSegment())};
        logHelper.printLog(singleUri.toString()+"");
        Cursor cursor = getContentResolver().query(singleUri,null,selection,selectionArgs,null);
        if(cursor!=null){
            cursor.moveToFirst();
            alarm = new Alarm(cursor.getInt(cursor.getColumnIndex(DBColumns._ID)), cursor.getString(cursor.getColumnIndex(DBColumns.TITLE)),
                    cursor.getString(cursor.getColumnIndex(DBColumns.DESC)), cursor.getLong(cursor.getColumnIndex(DBColumns.TIME))
                    , cursor.getInt(cursor.getColumnIndex(DBColumns.ENABLED)),cursor.getString(cursor.getColumnIndex(DBColumns.IMAGE_LINK))
            ,cursor.getString(cursor.getColumnIndex(DBColumns.VIDEO_LINK)));

                tv_title.setText(alarm.title);
            tv_desc.setText(alarm.desc);
        }


        assert cursor != null;
        cursor.close();

        if(alarm.imgLink!=null){
        iv_display.post(new Runnable() {
            @Override
            public void run() {

                  setPic(alarm.imgLink);

            }
        });
        }
        if(alarm.vidLink!=null){
            vv_play.setVisibility(View.VISIBLE);
            setVideo(alarm.vidLink);
        }

    }

    private void setVideo(String vidLink) {
        try {
            Uri uri = Uri.parse(vidLink);
            vv_play.setVideoURI(uri);
            vv_play.start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPic(String imgLink) {
        // Get the dimensions of the View
        int targetW = iv_display.getWidth();
        int targetH = iv_display.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgLink, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imgLink, bmOptions);
        iv_display.setImageBitmap(bitmap);
    }
}
