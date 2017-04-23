package com.droidappsfactory.alertz.beans;



import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tcsmans on 2/14/2017.
 */

public class Alarm implements Parcelable {


    public int id;
    public String title;
    public String desc;
    public String imgLink;
    public String vidLink;
    public long time;
    public int enabled;


    public Alarm(int id,String title,String desc,long time,int enabled,String imgLink,String vidLink){
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.time = time;
        this.enabled = enabled;
        this.imgLink = imgLink;
        this.vidLink = vidLink;
    }


    protected Alarm(Parcel in) {
        id = in.readInt();
        title = in.readString();
        desc = in.readString();
        time = in.readLong();
        enabled = in.readInt();
        imgLink = in.readString();
        vidLink = in.readString();
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {

        out.writeInt(id);
        out.writeString(title);
        out.writeString(desc);
        out.writeLong(time);
        out.writeInt(enabled);
        out.writeString(imgLink);
        out.writeString(vidLink);

    }
}
