package com.app.fypfinal.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.utils.JsonUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class UserMaps extends AppCompatActivity implements Info, OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener {
    private GoogleMap mMap;
    boolean zoomUpdated = false;
    LocationRequest locationRequest;
    FusedLocationProviderClient mFusedLocationClient;
    LatLng latLng;
    Marker previousMarker, driverMarker;
    SupportMapFragment mapFragment;

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplication() != null) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    placeMarkerWithAddress();
                    if (!zoomUpdated) {
                        float zoomLevel = 17.0f; //This goes up to 21
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
                        zoomUpdated = true;
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_maps_activity);

        initViews();
    }

    private void initViews() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.i(TAG, "onMapReady: ");
        mMap = googleMap;

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 second delay between each request
        locationRequest.setFastestInterval(5000); // 5 seconds fastest time in between each request
        locationRequest.setSmallestDisplacement(10); // 10 meters minimum displacement for new location request
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // enables GPS high accuracy location requests
        checkForLocation();

        // This code adds the listener and subscribes passenger to channel with driver's location.
        UserDashboard.pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pub, PNStatus status) {
                Log.i(TAG, "status: " + status.getStatusCode());
            }

            @Override
            public void message(PubNub pub, final PNMessageResult message) {
                runOnUiThread(() -> {
                    try {
                        Map<String, String> newLocation =
                                JsonUtils.fromJson(message.getMessage().toString(), LinkedHashMap.class);
                        updateUI(newLocation);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void presence(PubNub pub, PNPresenceEventResult presence) {

            }
        });
        UserDashboard.pubNub.subscribe()
                .channels(Collections.singletonList(PUBNUB_CHANNEL_NAME)) // subscribe to channels
                .execute();
    }

    private String getLocationAddress() {
        String street = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int j = 0; j <= returnedAddress.getMaxAddressLineIndex(); j++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(j)).append("");

                }
                street = strReturnedAddress.toString();
            }
        } catch (IOException e) {
            Log.i(TAG, "getLocationAddress: " + e.getMessage());
        }
        return street;
    }


    private void checkForLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
        } else checkLocationPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
                    mMap.setMyLocationEnabled(true);
                }
            } else
                Toast.makeText(getApplication(), "Please provide the permission", Toast.LENGTH_LONG).show();
        }
    }


    public void loadNavigationView(String lat, String lng) {
        Uri navigation = Uri.parse("google.navigation:q=" + lat + "," + lng + "");
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
        navigationIntent.setPackage("com.google.android.apps.maps");
        startActivity(navigationIntent);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", (dialogInterface, i) ->
                                ActivityCompat.requestPermissions(UserMaps.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION}, 1)).create().show();
            } else ActivityCompat.requestPermissions(UserMaps.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return placeMarkerWithAddress();
    }

    private boolean placeMarkerWithAddress() {
        if (previousMarker != null) previousMarker.remove();
        MarkerOptions mOptions = new MarkerOptions();
        mOptions.title(getLocationAddress());
        mOptions.position(new LatLng(latLng.latitude, latLng.longitude));
        Marker myMarker = mMap.addMarker(mOptions);
        if (myMarker == null) return false;
        previousMarker = myMarker;
        myMarker.hideInfoWindow();
        myMarker.showInfoWindow();
        return true;
    }

    private void updateUI(Map<String, String> newLoc) {
        Log.i(TAG, "updateUI: ");
        LatLng newLocation = new LatLng(Double.parseDouble(Objects.requireNonNull(newLoc.get("lat"))),
                Double.parseDouble(Objects.requireNonNull(newLoc.get("lng"))));
        if (driverMarker != null) {
            animateCar(newLocation);
            boolean contains = mMap.getProjection()
                    .getVisibleRegion()
                    .latLngBounds
                    .contains(newLocation);
            if (!contains) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
            }
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    newLocation, 15.5f));
            driverMarker = mMap.addMarker(new MarkerOptions().position(newLocation).
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.vip)));
        }
    }

    private void animateCar(final LatLng destination) {
        final LatLng startPosition = driverMarker.getPosition();
        final LatLng endPosition = new LatLng(destination.latitude, destination.longitude);
        final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(5000); // duration 5 seconds
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            try {
                float v = animation.getAnimatedFraction();
                LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                driverMarker.setPosition(newPosition);
            } catch (Exception ex) {
                Log.i(TAG, "onAnimationUpdate: " + ex);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        valueAnimator.start();
    }

    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }
}