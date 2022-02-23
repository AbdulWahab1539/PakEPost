package com.app.fypfinal.mvvm.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackingIdPojo {
    @SerializedName("tracking_id")
    @Expose
    String trackingId;

    public TrackingIdPojo(String trackingId) {
        this.trackingId = trackingId;
    }
}
