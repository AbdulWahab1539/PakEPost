package com.app.fypfinal.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.helper.ReadTask;
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
import com.google.android.gms.maps.model.Polyline;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class PostmanMaps extends AppCompatActivity implements Info, OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationButtonClickListener {
    public static Polyline polyline;
    public static int lineColor;
    private GoogleMap mMap;
    boolean zoomUpdated = false;
    boolean isMapsAvailable = false;
    LocationRequest locationRequest;
    FusedLocationProviderClient mFusedLocationClient;
    LatLng latLng;
    Button btnNavigate;
    ImageButton btnScan;
    Marker previousMarker;
    TextView tvPlaceTo;
    private static final LatLng PANORAMA_MALL = new LatLng(31.4678, 74.2666);
    private static final LatLng EMPORIUM_MALL = new LatLng(31.5618, 74.3212);
    private static final LatLng LAHORE_THOKER = new LatLng(31.4914, 74.2385);
    public static List<String> trackingIdList;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplication() != null) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    placeMarkerWithAddress();
//                    drawRoute();
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
        setContentView(R.layout.postman_map_activity);

        initViews();
    }


    private void initViews() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
        lineColor = this.getResources().getColor(R.color.yellow);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnNavigate = findViewById(R.id.btn_my_location);
        tvPlaceTo = findViewById(R.id.place_to);
        btnScan = findViewById(R.id.btn_scan);

        trackingIdList = new ArrayList<>();
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 second delay between each request
        locationRequest.setFastestInterval(5000); // 5 seconds fastest time in between each request
        locationRequest.setSmallestDisplacement(10); // 10 meters minimum displacement for new location request
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // enables GPS high accuracy location requests

        if (PostmanDashboard.pubNub != null) sendUpdatedLocationMessage();

//        TODO: Coordinates to be changed here
        tvPlaceTo.setText(getLocationAddress(31.4914, 74.2385));
        btnNavigate.setOnClickListener(view -> {
            Log.i(TAG, "initViews: ");
//            TODO: Coordinates to be changed here
            loadNavigationView("31.4914", "74.2385");
        });
        btnScan.setOnClickListener(view -> {
            Log.i(TAG, "initViews: ");
            startActivity(new Intent(this, Scanner.class));
        });
    }

    private void sendUpdatedLocationMessage() {
        Log.i(TAG, "sendUpdatedLocationMessage: ");
        try {
            mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {

                    Location location = locationResult.getLastLocation();
                    LinkedHashMap<String, String> message = getNewLocationMessage(location.getLatitude(), location.getLongitude());
                    PostmanDashboard.pubNub.publish()
                            .message(message)
                            .channel(PUBNUB_CHANNEL_NAME)
                            .async(new PNCallback<PNPublishResult>() {
                                @Override
                                public void onResponse(PNPublishResult result, PNStatus status) {
                                    if (!status.isError())
                                        System.out.println("pub timetoken: " + result.getTimetoken());
                                    System.out.println("pub status code: " + status.getStatusCode());
                                }
                            });
                }
            }, Looper.myLooper());

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private LinkedHashMap<String, String> getNewLocationMessage(double lat, double lng) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("lat", String.valueOf(lat));
        map.put("lng", String.valueOf(lng));
        return map;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.i(TAG, "onMapReady: ");
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        checkForLocation();
    }

    private void drawRoute(boolean isOneWay) {
        Log.i(TAG, "drawRoute: ");
        mMap.clear();

//        placeMarkerWithAddress();
        MarkerOptions options = new MarkerOptions();
        if (isOneWay) {
            options.position(PANORAMA_MALL);
            mMap.addMarker(new MarkerOptions().position(PANORAMA_MALL)
                    .title("Second Point"));
        } else {
            options.position(EMPORIUM_MALL);
            mMap.addMarker(new MarkerOptions().position(EMPORIUM_MALL)
                    .title("First Point"));
        }
        mMap.addMarker(new MarkerOptions().position(LAHORE_THOKER).title("Destination"));
        isMapsAvailable = true;
        mMap.addMarker(options);
        String url = getMapsApiDirectionsUrl(latLng, LAHORE_THOKER, isOneWay);
        ReadTask downloadTask = new ReadTask(mMap);
        downloadTask.execute(url);
    }


    private String getMapsApiDirectionsUrl(LatLng Office, LatLng home, boolean oneWay) {
        String waypoints = "waypoints=optimize:true|";
        if (oneWay) waypoints = waypoints + PANORAMA_MALL.latitude + "," + PANORAMA_MALL.longitude;
        else waypoints = waypoints + EMPORIUM_MALL.latitude + "," + EMPORIUM_MALL.longitude;

        String OriDest = "origin=" + Office.latitude + "," + Office.longitude + "&destination=" + home.latitude + "," + home.longitude;
        String key = "key=" + getString(R.string.google_maps_key);
        String params = OriDest + "&" + waypoints + "&" + key;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
    }

    private String getLocationAddress(double latitude, double longitude) {
        String street = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
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
                                ActivityCompat.requestPermissions(PostmanMaps.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION}, 1)).create().show();
            } else ActivityCompat.requestPermissions(PostmanMaps.this,
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
        if (!isMapsAvailable) drawRoute(false);
        MarkerOptions mOptions = new MarkerOptions();
        mOptions.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.vip)));
        mOptions.title(getLocationAddress(latLng.latitude, latLng.longitude));
        mOptions.position(new LatLng(latLng.latitude, latLng.longitude));
        Marker myMarker = mMap.addMarker(mOptions);
        if (myMarker == null) return false;
        previousMarker = myMarker;
        myMarker.hideInfoWindow();
        myMarker.showInfoWindow();
        return true;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Log.i(TAG, "onMarkerClick: ");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 13));
        if (polyline != null) polyline.remove();
        drawRoute(true);
        return true;
    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.marker_image);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null) drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }
}