package com.ft.mapp.home.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fun.vbox.remote.vloc.VLocation;

public class LocationData implements Parcelable {
    public String packageName;
    public int userId;
    public int mode;
    public VLocation location;

    public LocationData() {
    }

    public LocationData(String pkgName, int userId) {
        this.packageName = pkgName;
        this.userId = userId;
    }

    protected LocationData(Parcel in) {
        packageName = in.readString();
        userId = in.readInt();
        mode = in.readInt();
        location = in.readParcelable(VLocation.class.getClassLoader());
    }

    public static final Creator<LocationData> CREATOR = new Creator<LocationData>() {
        @Override
        public LocationData createFromParcel(Parcel in) {
            return new LocationData(in);
        }

        @Override
        public LocationData[] newArray(int size) {
            return new LocationData[size];
        }
    };

    @Override
    public String toString() {
        return "LocationData{" +
                "packageName='" + packageName + '\'' +
                ", userId=" + userId +
                ", location=" + location +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(packageName);
        parcel.writeInt(userId);
        parcel.writeInt(mode);
        parcel.writeParcelable(location, i);
    }
}
