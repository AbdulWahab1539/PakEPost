package com.app.fypfinal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;

public class PostmanDashboard extends AppCompatActivity implements Info {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postman_dashboard);
    }


    public void scanQR(View view) {
        startActivity(new Intent(this, Scanner.class));
    }

    public void parcelHistory(View view) {
        startActivity(new Intent(this, PostmanParcelHistory.class));
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
}