package com.app.fypfinal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

public class UserDashboard extends AppCompatActivity implements Info {

    public static PubNub pubNub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        initPubnub();
    }

    public void initPubnub() {
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