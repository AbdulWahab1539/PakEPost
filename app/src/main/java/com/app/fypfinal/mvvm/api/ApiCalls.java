package com.app.fypfinal.mvvm.api;


import com.app.fypfinal.mvvm.pojo.ParcelPojo;
import com.app.fypfinal.mvvm.pojo.PostProfilePojo;
import com.app.fypfinal.mvvm.pojo.ProfilePojo;
import com.app.fypfinal.mvvm.pojo.RegResponsePojo;
import com.app.fypfinal.mvvm.pojo.TrackingIdPojo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiCalls {

    //API Url
    String BASE_URL = "http://7f7b-103-137-24-113.ngrok.io";

    String URL_REGISTRATION = "/auth/registration/";
    String URL_PARCEL = "/api/my/parcel/";
    String URL_RECEIVER_PARCEL = "/api/receiver/parcel/";
    String URL_TRACK = "/api/parcel/";
    String URL_PROFILE = "/api/my/profile/";

    @POST(URL_REGISTRATION)
    Call<RegResponsePojo> postUser(@Body PostProfilePojo postProfilePojo);

    @GET(URL_PROFILE)
    Call<ProfilePojo> getProfile(@Header("Authorization") String token);

    //For Receiver Parcels
    @GET(URL_RECEIVER_PARCEL)
    Call<List<ParcelPojo>> getReceiverParcels(@Header("Authorization") String token);

    //for sender parcels
    @GET(URL_PARCEL)
    Call<List<ParcelPojo>> getParcelsList(@Header("Authorization") String token);

    @GET(URL_TRACK)
    Call<ParcelPojo> getTrackingParcel(@Header("Authorization") String token, @Body TrackingIdPojo trackingIdPojo);
}
