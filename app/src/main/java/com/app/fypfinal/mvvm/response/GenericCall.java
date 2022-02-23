package com.app.fypfinal.mvvm.response;


import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;

public class GenericCall<T> {
    MutableLiveData<GenericResponse<T>> mutableLiveData;
    Call<T> call;

    public GenericCall(Call<T> call) {
        mutableLiveData = new MutableLiveData<>();
        this.call = call;
    }

    public MutableLiveData<GenericResponse<T>> getMutableLiveData() {
        GenericCallBack<T> genericCallBack = new GenericCallBack<>(mutableLiveData);
        call.enqueue(genericCallBack.getCallback());
        return mutableLiveData;
    }

}
