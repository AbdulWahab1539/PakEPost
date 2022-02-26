package com.app.fypfinal.mvvm.vmrepo;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.fypfinal.mvvm.api.ApiCalls;
import com.app.fypfinal.mvvm.api.ApiClient;
import com.app.fypfinal.mvvm.pojo.Generic;
import com.app.fypfinal.mvvm.pojo.LoginPojo;
import com.app.fypfinal.mvvm.pojo.ParcelPojo;
import com.app.fypfinal.mvvm.pojo.PostProfilePojo;
import com.app.fypfinal.mvvm.pojo.ProfilePojo;
import com.app.fypfinal.mvvm.pojo.RegResponsePojo;
import com.app.fypfinal.mvvm.response.GenericCall;
import com.app.fypfinal.mvvm.response.GenericResponse;
import com.app.fypfinal.utils.Utils;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;

public class ViewModelRepo extends AndroidViewModel {


    ApiCalls apiCalls;

    public ViewModelRepo(@NonNull Application application) {
        super(application);
        apiCalls = ApiClient.getRetrofit().create(ApiCalls.class);
    }

    public LiveData<GenericResponse<RegResponsePojo>> postUser(PostProfilePojo postProfilePojo) {
        return new GenericCall<>(apiCalls.postUser(postProfilePojo)).getMutableLiveData();
    }

    public LiveData<GenericResponse<RegResponsePojo>> loginUser(LoginPojo loginPojo) {
        return new GenericCall<>(apiCalls.loginUser(loginPojo)).getMutableLiveData();
    }

    public LiveData<GenericResponse<ProfilePojo>> getProfile(Activity activity) {
        return new GenericCall<>(apiCalls.getProfile(Utils.token)).getMutableLiveData();
    }

    public LiveData<GenericResponse<ProfilePojo>> updateProfile(Activity activity, ProfilePojo profilePojo) {
        return new GenericCall<>(apiCalls.updateProfile(Utils.token, profilePojo)).getMutableLiveData();
    }

    public LiveData<GenericResponse<Generic>> updatePhoto(Activity activity, MultipartBody.Part image) {
        return new GenericCall<>(apiCalls.putPhoto(Utils.token, image)).getMutableLiveData();
    }

    public LiveData<GenericResponse<List<ParcelPojo>>> getReceiverParcels(Activity activity) {
        return new GenericCall<>(apiCalls.getReceiverParcels(Utils.token)).getMutableLiveData();
    }

    public LiveData<GenericResponse<ParcelPojo>> getTrackingParcel(Activity activity, String trackingId) {
        return new GenericCall<>(apiCalls.getTrackingParcel(Utils.token, trackingId)).getMutableLiveData();
    }

    public LiveData<GenericResponse<List<ParcelPojo>>> getParcelsList(Activity activity) {
        return new GenericCall<>(apiCalls.getParcelsList(Utils.token)).getMutableLiveData();
    }


}
