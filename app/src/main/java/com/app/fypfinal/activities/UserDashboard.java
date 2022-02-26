package com.app.fypfinal.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.mvvm.mvvmutils.MVVMUtils;
import com.app.fypfinal.mvvm.pojo.ProfilePojo;
import com.app.fypfinal.mvvm.response.GenericResponse;
import com.app.fypfinal.utils.DialogUtils;
import com.app.fypfinal.utils.Utils;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

public class UserDashboard extends AppCompatActivity implements Info {

    public static PubNub pubNub;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new Dialog(this);
        DialogUtils.initLoadingDialog(dialog);

        initProfile();
    }

    private void initProfile() {
        Log.i(TAG, "initProfile: ");
        dialog.show();
        MVVMUtils.getViewModelRepo(this)
                .getProfile(this)
                .observe(this, this::initProfileResponse);
    }

    private void initProfileResponse(GenericResponse<ProfilePojo> response) {
        initPubnub();
        setContentView(R.layout.activity_user_dashboard);
        dialog.dismiss();
        Log.i(TAG, "initProfileResponse: ");
        if (response.isSuccessful()) Utils.profilePojo = response.getResponse();
        else
            MVVMUtils.initErrMessages(this, response.getErrorMessages(), response.getResponseCode());
    }

    public void initPubnub() {
        Log.i(TAG, "initPubnub: ");
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(PUBNUB_SUBSCRIBE_KEY);
        pnConfiguration.setPublishKey(PUBNUB_PUBLISH_KEY);
        pnConfiguration.setSecure(true);
        pubNub = new PubNub(pnConfiguration);
    }

    public void trackPostman(View view) {
        startActivity(new Intent(this, UserMaps.class));
    }

    public void trackParcel(View view) {
        startActivity(new Intent(this, TrackParcel.class));
    }

    public void profile(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void parcelHistory(View view) {
        startActivity(new Intent(this, UserParcelHistory.class));
    }

}