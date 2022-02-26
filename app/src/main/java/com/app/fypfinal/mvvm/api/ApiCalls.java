package com.app.fypfinal.mvvm.api;


import com.app.fypfinal.mvvm.pojo.Generic;
import com.app.fypfinal.mvvm.pojo.LoginPojo;
import com.app.fypfinal.mvvm.pojo.ParcelPojo;
import com.app.fypfinal.mvvm.pojo.PostProfilePojo;
import com.app.fypfinal.mvvm.pojo.ProfilePojo;
import com.app.fypfinal.mvvm.pojo.RegResponsePojo;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiCalls {

    //API Url
    String BASE_URL = "https://jhonydev901.pythonanywhere.com";

    String URL_REGISTRATION = "/auth/registration/";
    String URL_LOGIN = "/auth/login/";

    String URL_PARCEL = "/api/my/parcel/";
    String URL_RECEIVER_PARCEL = "/api/receiver/parcel/";
    String URL_TRACK = "/api/parcel/{tracking_id}";
    String URL_PROFILE = "/api/my/profile/";
    String URL_UPDATE_IMAGE = "/api/my/image/";

    @POST(URL_REGISTRATION)
    Call<RegResponsePojo> postUser(@Body PostProfilePojo postProfilePojo);

    @POST(URL_LOGIN)
    Call<RegResponsePojo> loginUser(@Body LoginPojo loginPojo);

    @GET(URL_PROFILE)
    Call<ProfilePojo> getProfile(@Header("Authorization") String token);

    @PUT(URL_PROFILE)
    Call<ProfilePojo> updateProfile(@Header("Authorization") String token, @Body ProfilePojo profilePojo);

    @Multipart
    @PUT(URL_UPDATE_IMAGE)
    Call<Generic> putPhoto(
            @Header("Authorization") String token,
            @Part MultipartBody.Part profileImage);

    //For Receiver Parcels
    @GET(URL_RECEIVER_PARCEL)
    Call<List<ParcelPojo>> getReceiverParcels(@Header("Authorization") String token);

    //for sender parcels
    @GET(URL_PARCEL)
    Call<List<ParcelPojo>> getParcelsList(@Header("Authorization") String token);

    @GET(URL_TRACK)
    Call<ParcelPojo> getTrackingParcel(@Header("Authorization") String token, @Path("tracking_id") String tracking_id);
}
