package com.app.fypfinal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.utils.Utils;
import com.pubnub.api.PubNub;

public class PostmanDashboard extends AppCompatActivity implements Info {

    public static PubNub pubNub;
    TextView tvWelcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postman_dashboard);

        tvWelcomeText = findViewById(R.id.text);
        tvWelcomeText.setText(String.format("Welcome %s\n%s", Utils.profilePojo.getFirstName(), Utils.profilePojo.getPhoneNumber()));
        pubNub = Utils.initPubnub();
    }

    public void scanQR(View view) {
        startActivity(new Intent(this, Scanner.class));
    }

    public void parcelHistory(View view) {
        startActivity(new Intent(this, DelievredParcels.class));
    }

    public void profile(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void navigate(View view) {
        startActivity(new Intent(this, PostmanMaps.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void scannedParcels(View view) {
        startActivity(new Intent(this, ScannedParcel.class));
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
}