package com.app.fypfinal.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.mvvm.mvvmutils.MVVMUtils;
import com.app.fypfinal.mvvm.pojo.Generic;
import com.app.fypfinal.mvvm.pojo.ProfilePojo;
import com.app.fypfinal.mvvm.response.GenericResponse;
import com.app.fypfinal.utils.DialogUtils;
import com.app.fypfinal.utils.SharedPerfUtils;
import com.app.fypfinal.utils.Utils;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class ProfileActivity extends AppCompatActivity implements Info {

    EditText etFirstName, etLastName, etEmail, etPhone, etAddress;
    String strEtFirstName;
    String strEtLastName;
    String strEtEmail;
    String strEtPhone, strEtAddress;
    Dialog dgLoading;
    TextView tvLocation, tvUsername, tvCnic;
    ImageView ivProfile;
    int LOCATION_REFRESH_TIME = 1000;
    int LOCATION_REFRESH_DISTANCE = 100;
    LocationManager mLocationManager;
    ProfilePojo profilePojo;

    private final LocationListener mLocationListener = location -> {
        Log.i(TAG, ": " + location.getLatitude());
        Log.i(TAG, ": " + location.getLongitude());
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Log.i(TAG, ": " + addresses.get(0).getAddressLine(0));
            tvLocation.setText(String.valueOf(addresses.get(0).getAddressLine(0)));
            profilePojo.setLatitude(String.format(Locale.US, "%.5f", location.getLatitude()));
            profilePojo.setLongitude(String.format(Locale.US, "%.5f", location.getLongitude()));
            dgLoading.dismiss();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
    }

    private void initViews() {
        etPhone = findViewById(R.id.et_phone);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        etAddress = findViewById(R.id.et_address);

        tvLocation = findViewById(R.id.tv_location);
        tvCnic = findViewById(R.id.tv_cnic);
        tvUsername = findViewById(R.id.tv_username);

        ivProfile = findViewById(R.id.profile_image);

        dgLoading = new Dialog(this);
        DialogUtils.initLoadingDialog(dgLoading);

        profilePojo = new ProfilePojo();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        initProfile();
    }

    private void initProfile() {
        Log.i(TAG, "initProfile: ");
        dgLoading.show();
        MVVMUtils.getViewModelRepo(this)
                .getProfile(this)
                .observe(this, this::initProfileResponse);
    }

    private void initProfileResponse(GenericResponse<ProfilePojo> response) {
        Log.i(TAG, "initProfileResponse: ");
        if (response.isSuccessful()) {
            Utils.profilePojo = response.getResponse();
            Log.i(TAG, "initProfileResponse: " + Utils.profilePojo.getCnic());
            initDefaultProfile();
        } else
            MVVMUtils.initErrMessages(this, response.getErrorMessages(), response.getResponseCode());
        dgLoading.cancel();
    }

    private void initDefaultProfile() {
        Log.i(TAG, "initDefaultProfile: ");
        if (Utils.profilePojo == null) return;
        etPhone.setText(Utils.profilePojo.getPhoneNumber());
        etFirstName.setText(Utils.profilePojo.getFirstName());
        etLastName.setText(Utils.profilePojo.getLastName());
        etEmail.setText(Utils.profilePojo.getEmail());
        etAddress.setText(Utils.profilePojo.getAddress());
        tvUsername.setText(String.format("%s %s", Utils.profilePojo.getFirstName(), Utils.profilePojo.getLastName()));
        tvCnic.setText(Utils.profilePojo.getCnic());
        if (Utils.profilePojo.getProfileImage() != null)
            Glide.with(this)
                    .load(Utils.profilePojo.getProfileImage())
                    .circleCrop()
                    .into(ivProfile);

        Log.i(TAG, "initDefaultProfile: " + Utils.profilePojo.getLatitude() + Utils.profilePojo.getLongitude());
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(Utils.profilePojo.getLatitude()),
                    Double.parseDouble(Utils.profilePojo.getLongitude()), 1);
            if (addresses.isEmpty()) return;
            Log.i(TAG, ": " + addresses.get(0).getAddressLine(0));
            tvLocation.setText(String.valueOf(addresses.get(0).getAddressLine(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void castStrings() {
        Log.i(TAG, "castStrings: ");
        strEtFirstName = etFirstName.getText().toString();
        strEtLastName = etLastName.getText().toString();
        strEtPhone = etPhone.getText().toString();
        strEtEmail = etEmail.getText().toString();
        strEtAddress = etAddress.getText().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) return;
            Uri uri = data.getData();
            File imageFile = new File(uri.getPath());
            Log.i(TAG, "onActivityResult: " + imageFile.getAbsolutePath());
            ivProfile.setImageURI(uri);
            MVVMUtils.getViewModelRepo(this)
                    .updatePhoto(this, MVVMUtils.fileRequest(imageFile))
                    .observe(this, this::initPhotoResponse);
        } else if (resultCode == ImagePicker.RESULT_ERROR)
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
    }

    private void initPhotoResponse(GenericResponse<Generic> response) {
        if (response.isSuccessful()) {
            Log.i(TAG, "initPhotoResponse: update Successful");
            initProfile();
        } else
            MVVMUtils.initErrMessages(this, response.getErrorMessages(), response.getResponseCode());
    }

    public void updateLocation(View view) {
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Utils.checkLocationPermission(this);
            return;
        }
        dgLoading.show();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) getCurrentLocation();
            } else Utils.initLocationPermissionDialog(this);
        }
    }

    public void updateProfile(View view) {

        castStrings();
        if (!Utils.validEt(etFirstName, strEtFirstName)) return;
        if (!Utils.validEt(etLastName, strEtLastName)) return;
        if (!Utils.validEt(etPhone, strEtPhone)) return;
        if (!Utils.validEt(etEmail, strEtEmail)) return;
        if (!Utils.validEt(etAddress, strEtAddress)) return;

        if (!Utils.isValidEmail(strEtEmail)) {
            etEmail.setError("Please check email");
            return;
        }
        if (!Utils.validatePhoneNumber(strEtPhone)) {
            etPhone.setError("Phone number is wrong");
            return;
        }
        profilePojo.setFirstName(strEtFirstName);
        profilePojo.setLastName(strEtLastName);
        profilePojo.setEmail(strEtEmail);
        profilePojo.setPhoneNumber(strEtPhone);
        profilePojo.setAddress(strEtAddress);

        dgLoading.show();
        MVVMUtils.getViewModelRepo(this)
                .updateProfile(this, profilePojo)
                .observe(this, this::initUpdateResponse);
    }

    private void initUpdateResponse(GenericResponse<ProfilePojo> response) {
        dgLoading.cancel();
        Log.i(TAG, "initUpdateResponse: ");
        if (response.isSuccessful()) {
            Utils.profilePojo = response.getResponse();
            Log.i(TAG, "initUpdateResponse: " + response.getResponse().getEmail());
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
        } else
            MVVMUtils.initErrMessages(this, response.getErrorMessages(), response.getResponseCode());
    }

    public void updateImage(View view) {
        Log.i(TAG, "updateImage: ");
        ImagePicker.with(this)
                .cropSquare()
                .maxResultSize(512, 512)
                .start();
    }

    public void back(View view) {
        onBackPressed();
    }

    public void signOut(View view) {
        SharedPerfUtils.putStringSharedPrefs(this, null, PREF_ACCESS_TOKEN);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}