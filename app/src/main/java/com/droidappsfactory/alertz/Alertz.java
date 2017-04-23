package com.droidappsfactory.alertz;

import android.app.Application;

/**
 * Created by tcsmans on 3/21/2017.
 */

public class Alertz extends Application {

    public String getInternalFilePath(){
        return getApplicationContext().getFilesDir().getAbsolutePath()+"/imagesStore";
    }
}
