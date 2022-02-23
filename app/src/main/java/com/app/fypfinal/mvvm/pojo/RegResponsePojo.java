package com.app.fypfinal.mvvm.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegResponsePojo extends Generic{
    @SerializedName("key")
    @Expose
    String key;

    public RegResponsePojo() {
    }

    public String getKey() {
        return key;
    }
}
