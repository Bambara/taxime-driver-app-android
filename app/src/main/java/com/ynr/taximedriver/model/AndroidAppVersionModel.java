package com.ynr.taximedriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AndroidAppVersionModel {
    @Expose
    @SerializedName("androidAppVersion")
    private int androidAppVersion;

    public int getAndroidAppVersion() {
        return androidAppVersion;
    }

    public void setAndroidAppVersion(int androidAppVersion) {
        this.androidAppVersion = androidAppVersion;
    }
}
