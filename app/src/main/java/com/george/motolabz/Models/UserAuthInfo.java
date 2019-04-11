package com.brainyapps.motolabz.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HappyBear on 8/21/2018.
 */

public class UserAuthInfo implements Parcelable{
    public String userId = "";
    public String userEmail = "";
    public String userName = "";
    public String photoUrl = "";

    public UserAuthInfo(){
        userId = "";
        userEmail = "";
        userName = "";
        photoUrl = "";
    }

    protected UserAuthInfo(Parcel in){
        userId = in.readString();
        userEmail = in.readString();
        userName = in.readString();
        photoUrl = in.readString();
    }

    public static final Creator<UserAuthInfo> CREATOR = new Creator<UserAuthInfo>() {
        @Override
        public UserAuthInfo createFromParcel(Parcel in) {
            return new UserAuthInfo(in);
        }

        @Override
        public UserAuthInfo[] newArray(int size) {
            return new UserAuthInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(userEmail);
        parcel.writeString(userName);
        parcel.writeString(photoUrl);
    }
}
