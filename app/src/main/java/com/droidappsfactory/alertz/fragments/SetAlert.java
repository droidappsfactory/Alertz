package com.droidappsfactory.alertz.fragments;


import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.droidappsfactory.alertz.broadcasts.AlarmReceiver;
import com.droidappsfactory.alertz.R;
import com.droidappsfactory.alertz.database.DBColumns;
import com.droidappsfactory.alertz.provider.AlertProvider;
import com.droidappsfactory.alertz.util.LogHelper;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ALARM_SERVICE;

/**
 * Created by tcsmans on 3/21/2017.
 */

public class SetAlert extends Fragment {


    LogHelper logHelper = new LogHelper(SetAlert.class.getSimpleName());

    Button bt_save,bt_cancel;
    ImageView ib_settime, ib_setdate ,iv_camera,iv_display,iv_video;
    TextView tv_date, tv_time;
    private int mYear, mMonth, mDay, mHour, mMinute;
    int select_hour, select_minute, select_day, select_month, select_year;
    boolean flag=false;

    TextInputEditText tv_title,tv_desc;

    private static final int REQEUST_PICTURE_CAPTURE= 100;
    private static final int REQUEST_VIDEO_CAPTURE = 101;
    OnAlarmSetListner listner;
    String mCurrentPhotoPath;
    String videoUriString;
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_set_alarm,container,false);

        bt_save = (Button)rootView.findViewById(R.id.bt_save);
        bt_cancel = (Button)rootView.findViewById(R.id.bt_cancel);
        ib_setdate = (ImageView)rootView.findViewById(R.id.ib_date);
        ib_settime = (ImageView)rootView.findViewById(R.id.ib_time);
        tv_date = (TextView)rootView.findViewById(R.id.tv_date);
        tv_time = (TextView)rootView.findViewById(R.id.tv_time);
        tv_title = (TextInputEditText)rootView.findViewById(R.id.et_title);
        tv_desc = (TextInputEditText)rootView.findViewById(R.id.et_desc);
        iv_camera = (ImageView)rootView.findViewById(R.id.iv_camera);
        iv_display = (ImageView)rootView.findViewById(R.id.iv_display);
        iv_video = (ImageView)rootView.findViewById(R.id.iv_video);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            iv_camera.setEnabled(false);
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }



        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        iv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakeVideoIntent();
            }
        });

        ib_setdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                Calendar current = Calendar.getInstance();
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR,year);
                                calendar.set(Calendar.MONTH,monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                                if(calendar.compareTo(current) < 0){
                                    //The set Date/Time already passed
                                    Toast.makeText(getActivity(),
                                            "Invalid Date",
                                            Toast.LENGTH_LONG).show();
                                    flag = false;
                                    calendar = null;
                                    return;
                                }

                                SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
                                String formatted = format.format(calendar.getTime());
                                Log.d("date format", formatted);
                                tv_date.setText(formatted);
                                select_day=dayOfMonth;
                                select_month=monthOfYear;
                                select_year=year;
                                flag=true;

                            }
                        }, mYear, mMonth, mDay);


                datePickerDialog.show();

            }
        });


        ib_settime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                String formatted = dateFormat.format(calendar.getTime());
                                tv_time.setText(formatted);
                                select_hour=hourOfDay;
                                select_minute=minute;

                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();

            }
        });


        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();

                calendar.set(select_year,
                        select_month,
                        select_day,
                        select_hour,
                        select_minute,
                        00);


                try{
                    ContentValues values = new ContentValues();
                    values.put(DBColumns.TITLE,tv_title.getText().toString());
                    values.put(DBColumns.DESC,tv_desc.getText().toString());
                    values.put(DBColumns.TIME,calendar.getTimeInMillis());
                    values.put(DBColumns.ENABLED,0);
                    values.put(DBColumns.IMAGE_LINK,mCurrentPhotoPath);
                    values.put(DBColumns.VIDEO_LINK,videoUriString);


                    Uri uri = getActivity().getContentResolver().insert(AlertProvider.CONTENT_ALERTZ_URI,values);
                    assert uri != null;
                    logHelper.printLog("Insertion "+ uri+ "Last path segment"+uri.getLastPathSegment());
                    Intent myIntent = new Intent(getActivity(), AlarmReceiver.class);
                    myIntent.setData(uri);
                    pendingIntent = PendingIntent.getBroadcast(getActivity(), Integer.parseInt(uri.getLastPathSegment()), myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    listner.onAlarmSet(calendar.getTimeInMillis()+"");
                }catch (Exception e){
                    e.printStackTrace();
                }



                getActivity().getFragmentManager().popBackStack();


            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentPhotoPath==null){
                    getActivity().getFragmentManager().popBackStack();
                }else {
                    File file = new File(mCurrentPhotoPath);
                    boolean status = file.delete();

                    File videoFile = new File(videoUriString);
                    boolean videoStatus = videoFile.delete();
                    logHelper.printLog("deletion status "+ status + videoStatus);
                    getActivity().getFragmentManager().popBackStack();
                }

            }
        });
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                iv_camera.setEnabled(true);
            }
        }
    }


    void setListner(OnAlarmSetListner listner){
        this.listner = listner;
    }

    public static SetAlert newInstance(OnAlarmSetListner listner) {

        Bundle args = new Bundle();

        SetAlert fragment = new SetAlert();
        fragment.setListner(listner);
        fragment.setArguments(args);
        return fragment;
    }

    public void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.droidappsfactory.alertz.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent,  REQEUST_PICTURE_CAPTURE);
            }
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==  REQEUST_PICTURE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                setPic();

            }
        }

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoUriString = data.getData().toString();
            logHelper.printLog("Video uri String "+ videoUriString+ data.getData().toString());
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = iv_display.getWidth();
        int targetH = iv_display.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        iv_display.setImageBitmap(bitmap);
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private File createVideoFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "VID_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File videofile = File.createTempFile(
                videoFileName,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        videoUriString = videofile.getAbsolutePath();

        return videofile;
    }


    private void dispatchTakeVideoIntent() {
        // create new Intentwith with Standard Intent action that can be
        // sent to have the camera application capture an video and return it.
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        // create a file to save the video
        File fileUri = null;
        try {
            fileUri = createVideoFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri videoUri = FileProvider.getUriForFile(getActivity(),
                "com.droidappsfactory.alertz.fileprovider",
                fileUri);
        // set the image file name
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        // set the video image quality to high
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        // start the Video Capture Intent
        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
    }

    public interface OnAlarmSetListner{
        void onAlarmSet(String time);
        void onAlarmOff(String txt);
    }

}
