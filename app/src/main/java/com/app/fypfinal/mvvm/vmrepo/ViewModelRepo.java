package com.app.fypfinal.mvvm.vmrepo;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.fypfinal.mvvm.api.ApiCalls;
import com.app.fypfinal.mvvm.api.ApiClient;
import com.app.fypfinal.mvvm.pojo.ParcelPojo;
import com.app.fypfinal.mvvm.pojo.PostProfilePojo;
import com.app.fypfinal.mvvm.pojo.ProfilePojo;
import com.app.fypfinal.mvvm.pojo.RegResponsePojo;
import com.app.fypfinal.mvvm.pojo.TrackingIdPojo;
import com.app.fypfinal.mvvm.response.GenericCall;
import com.app.fypfinal.mvvm.response.GenericResponse;
import com.app.fypfinal.utils.Utils;

import java.util.List;

public class ViewModelRepo extends AndroidViewModel {


    ApiCalls apiCalls;

    public ViewModelRepo(@NonNull Application application) {
        super(application);
        apiCalls = ApiClient.getRetrofit().create(ApiCalls.class);
    }

    public LiveData<GenericResponse<RegResponsePojo>> postUser(PostProfilePojo postProfilePojo) {
        return new GenericCall<>(apiCalls.postUser(postProfilePojo)).getMutableLiveData();
    }

    public LiveData<GenericResponse<ProfilePojo>> getProfile(Activity activity) {
        return new GenericCall<>(apiCalls.getProfile(Utils.token)).getMutableLiveData();
    }

    public LiveData<GenericResponse<List<ParcelPojo>>> getReceiverParcels(Activity activity) {
        return new GenericCall<>(apiCalls.getReceiverParcels(Utils.token)).getMutableLiveData();
    }

    public LiveData<GenericResponse<ParcelPojo>> getTrackingParcel(Activity activity, TrackingIdPojo taTrackingIdPojo) {
        return new GenericCall<>(apiCalls.getTrackingParcel(Utils.token, taTrackingIdPojo)).getMutableLiveData();
    }

    public LiveData<GenericResponse<List<ParcelPojo>>> getParcelsList(Activity activity) {
        return new GenericCall<>(apiCalls.getParcelsList(Utils.token)).getMutableLiveData();
    }


}
