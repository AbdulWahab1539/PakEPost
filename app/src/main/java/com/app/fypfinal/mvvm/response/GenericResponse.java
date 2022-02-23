package com.app.fypfinal.mvvm.response;


import java.util.List;

import retrofit2.Response;

public class GenericResponse<T> {

    public static int REQUEST_TIMED_OUT = 408;
    T anyResponse;
    int responseCode;
    List<String> errorMessages;
    Response<T> rawResponse;

    public GenericResponse() {
        responseCode = REQUEST_TIMED_OUT;
//        Log.i("TAG", "GenericResponse: ");
    }

    public GenericResponse(Response<T> response) {
//        Log.i("TAG", "initPackageResponse: " + response);
        rawResponse = response;
        this.responseCode = response.code();
        if (response.isSuccessful())
            anyResponse = response.body();
    }

    public Response<T> getRawResponse() {
        return rawResponse;
    }

    public T getResponse() {
        return anyResponse;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public int getResponseCode() {
        return responseCode;
    }


    public boolean isSuccessful() {
        if (getRawResponse() == null)
            return false;
        return getRawResponse().isSuccessful();
    }
}

