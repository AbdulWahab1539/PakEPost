package com.app.fypfinal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.utils.SharedPerfUtils;
import com.app.fypfinal.utils.Utils;
import com.pubnub.api.PubNub;

public class UserDashboard extends AppCompatActivity implements Info {
    public static PubNub pubNub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        pubNub = Utils.initPubnub();

        Utils.createChannel(this);

        if (SharedPerfUtils.getBooleanSharedPrefs(this, PREF_FIRST_LAUNCH))
            Utils.initProfileUpdateDialog(this);
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
        startActivity(new Intent(this, ParcelHistory.class));
    }

    @Override
    protected void onDestroy() {
        if (pubNub != null) {
            pubNub.destroy();
            pubNub.removeChannelsFromChannelGroup();
            pubNub.removePushNotificationsFromChannels();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}