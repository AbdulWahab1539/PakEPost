package com.app.fypfinal.mvvm.response;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.mvvm.mvvmutils.MVVMUtils;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenericCallBack<T> implements Info {

    public MutableLiveData<GenericResponse<T>> mutableLiveData;
    Callback<T> callback;

    public GenericCallBack(MutableLiveData<GenericResponse<T>> mutableLiveData) {
        this.mutableLiveData = mutableLiveData;
//        Log.i(TAG, "initPackageResponse: " + mutableLiveData.getValue());
        callback = new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
//                Log.i(TAG, "initPackageResponse: initial : ----------------------------------");
//                Log.i(TAG, "initPackageResponse: initial : " + response);
//                Log.i(TAG, "initPackageResponse: initial : " + response.message());
//                Log.i(TAG, "initPackageResponse: initial : " + response.raw());
//                Log.i(TAG, "initPackageResponse: initial : " + response.code());
                List<String> errors = null;
                try {
                    if (response.errorBody() != null) {
                        String str = response.errorBody().string();
//                        Log.i(TAG, "parseError: " + str);
                        errors = MVVMUtils.parseError(str);
//                        for (String strError : errors)
//                            Log.i(TAG, "parseError: " + strError);
                    }
                } catch (IOException e) {
//                    e.printStackTrace();
                }
//                Log.i(TAG, "initPackageResponse:  Second :  " + response);
                try {
                    GenericResponse<T> genericResponse = new GenericResponse<>(response);
                    if (errors != null)
                        genericResponse.setErrorMessages(errors);
                    mutableLiveData.setValue(genericResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                    mutableLiveData.setValue(new GenericResponse<>());
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
//                Log.i(TAG, "initPackageResponse: failure" + t.getMessage());
                mutableLiveData.setValue(new GenericResponse<>());
            }
        };
    }

    public Callback<T> getCallback() {
        return callback;
    }

}
