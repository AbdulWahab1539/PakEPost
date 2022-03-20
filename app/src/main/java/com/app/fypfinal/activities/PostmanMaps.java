package com.app.fypfinal.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.helper.ReadTask;
import com.app.fypfinal.mvvm.mvvmutils.MVVMUtils;
import com.app.fypfinal.mvvm.pojo.ParcelPojo;
import com.app.fypfinal.mvvm.response.GenericResponse;
import com.app.fypfinal.utils.DialogUtils;
import com.app.fypfinal.utils.Utils;
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
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
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
    public static List<Polyline> polyline;
    public static int lineColor;
    private GoogleMap mMap;
    boolean zoomUpdated = false;
    LocationRequest locationRequest;
    FusedLocationProviderClient mFusedLocationClient;
    LatLng latLng;
    LatLng selectedRoute;
    Button btnNavigate, btnDraw;
    Marker previousMarker;
    List<LatLng> latLngList;
    Dialog dialog;
    public static PubNub pubNub;

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplication() != null) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    placeMarkerWithAddress();
                    if (!zoomUpdated) {
                        float zoomLevel = 17.0f;
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
        btnDraw = findViewById(R.id.btn_draw);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000); // 5 second delay between each request
        locationRequest.setFastestInterval(3000); // 5 seconds fastest time in between each request
        locationRequest.setSmallestDisplacement(5); // 10 meters minimum displacement for new location request
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // enables GPS high accuracy location requests

        latLngList = new ArrayList<>();
        polyline = new ArrayList<>();

        dialog = new Dialog(this);
        DialogUtils.initLoadingDialog(dialog);

        initParcels();

        initPubnub();
    }

    public void initPubnub() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(PUBNUB_SUBSCRIBE_KEY);
        pnConfiguration.setPublishKey(PUBNUB_PUBLISH_KEY);
        pnConfiguration.setSecure(true);
        pubNub = new PubNub(pnConfiguration);
        sendUpdatedLocationMessage();
    }

    private void initParcels() {
        dialog.show();
        Log.i(TAG, "initParcels: ");
        MVVMUtils.getViewModelRepo(this)
                .getParcelsList(this)
                .observe(this, this::initParcelResponse);
    }

    private void initParcelResponse(GenericResponse<List<ParcelPojo>> response) {
        dialog.dismiss();
        if (response.isSuccessful()) {
            for (ParcelPojo parcelPojo : response.getResponse()) {
                if (!parcelPojo.getReceiver().getLatitude().isEmpty()
                        && !parcelPojo.getReceiver().getLongitude().isEmpty()
                        && latLngNonZero(new LatLng(
                        Double.parseDouble(parcelPojo.getReceiver().getLatitude()),
                        Double.parseDouble(parcelPojo.getReceiver().getLongitude())))) {
                    Log.i(TAG, "Already " + parcelPojo.getReceiver().getLatitude() +
                            parcelPojo.getReceiver().getLongitude());
                    Log.i(TAG, "initParcelResponse: " + parcelPojo.getTrackingId());
                    latLngList.add(new LatLng(Double.parseDouble(parcelPojo.getReceiver().getLatitude()),
                            Double.parseDouble(parcelPojo.getReceiver().getLongitude())));
                } else {
                    if (parcelPojo.getReceiver().getAddress() != null
                            && !parcelPojo.getReceiver().getAddress().isEmpty()) {
                        Log.i(TAG, "initParcelResponse: " + parcelPojo.getReceiver().getAddress());
                        LatLng latLng = getLocationFromAddress(this, parcelPojo.getReceiver().getAddress());
                        if (latLng == null) return;
                        latLngList.add(new LatLng(latLng.latitude, latLng.longitude));
                        Log.i(TAG, "Got from address " + latLng.latitude + latLng.longitude);
                    } else
                        Log.i(TAG, "initParcelResponse: Not Found");
                }
            }
        } else
            MVVMUtils.initErrMessages(this, response.getErrorMessages(), response.getResponseCode());
    }


    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) return null;
            Log.i(TAG, "getLocationFromAddress: " + address.get(0).getAddressLine(0));
            Address location = address.get(0);
            if (location == null) return null;
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    private void sendUpdatedLocationMessage() {
        Log.i(TAG, "sendUpdatedLocationMessage: ");
        try {
            mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    LinkedHashMap<String, String> message = getNewLocationMessage(location.getLatitude(), location.getLongitude());
                    pubNub.publish()
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

    private void drawAllRoute() {
        MarkerOptions options = new MarkerOptions();
        if (isLatLngAvailable()) {
            for (LatLng latLng : latLngList) {
                options.position(latLng);
                mMap.addMarker(new MarkerOptions().position(latLng)
                        .title(getLocationAddress(latLng.latitude, latLng.longitude)));
                Log.i(TAG, "drawAllRoute: " + latLng.longitude + latLng.latitude);
            }
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(options.getPosition().latitude
                    , options.getPosition().longitude), 13));
        } else Toast.makeText(this, "No Parcels to draw Routes!!!", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "drawAllRoute: " + latLngList.size());
        String url = null;
        if (latLngList.size() > 1) {
            url = getMultiDirections(false);
            Log.i(TAG, "drawAllRoute: " + url);
        } else for (LatLng latLng : latLngList) url = getSingleDirection(latLng);
        ReadTask downloadTask = new ReadTask(mMap);
        downloadTask.execute(url);
    }

    private boolean isLatLngAvailable() {
        return latLngList != null && !latLngList.isEmpty();
    }

    private boolean latLngNonZero(LatLng latLng) {
        return latLng.latitude != 0.0 && latLng.longitude != 0.0;
    }

    private String getMultiDirections(boolean isNavigation) {
        StringBuilder waypoints;
        if (isNavigation) waypoints = new StringBuilder("waypoints=");
        else waypoints = new StringBuilder("waypoints=optimize:true");
        Log.i(TAG, "getMultiDirections: " + latLngList.size());
        List<LatLng> latLngListNew = new ArrayList<>(latLngList);
        int index = latLngListNew.size() - 1;
        latLngListNew.remove(index);
        Log.i(TAG, "getMultiDirections: " + latLngListNew.size() + index + latLngList.size());
        if (isLatLngAvailable())
            for (LatLng latLng : latLngListNew)
                if (latLngNonZero(latLng)) {
                    Log.i(TAG, "getMultiDirections: " + latLng.latitude + latLng.longitude);
                    waypoints.append("|").append(latLng.latitude).append(",").append(latLng.longitude);
                }
        String OriDest = "";
        if (latLng != null) OriDest = "origin=" + latLng.latitude + "," + latLng.longitude;
        if (latLngList != null && !latLngList.isEmpty())
            OriDest = OriDest + "&destination=" + latLngList.get(index).latitude + ","
                    + latLngList.get(index).longitude;
        Log.i(TAG, "getMultiDirections: " + waypoints);
        if (!isNavigation) {
            String key = "key=" + getString(R.string.google_maps_key);
            String params = OriDest + "&" + waypoints + "&" + key;
            String output = "json";
            return "https://maps.googleapis.com/maps/api/directions/"
                    + output + "?" + params;
        } else
            return "https://www.google.com/maps/dir/?api=1&" + OriDest + "&" + waypoints + "&travelmode=driving";
    }

    private String getLocationAddress(double latitude, double longitude) {
        String street = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int j = 0; j <= returnedAddress.getMaxAddressLineIndex(); j++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(j)).append("");
                }
                street = strReturnedAddress.toString();
                Log.i(TAG, "getLocationAddress: " + street);
            }
        } catch (IOException e) {
            Log.i(TAG, "getLocationAddress: " + e.getMessage());
        }
        return street;
    }


    private boolean checkForLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            return true;
        } else {
            Utils.checkLocationPermission(this);
            return false;
        }
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
                    mMap.setOnMyLocationButtonClickListener(this);
                }
            } else Utils.initLocationPermissionDialog(this);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return placeMarkerWithAddress();
    }

    private boolean placeMarkerWithAddress() {
        Log.i(TAG, "placeMarkerWithAddress: ");
        checkForLocation();
        if (previousMarker != null) previousMarker.remove();
        MarkerOptions mOptions = new MarkerOptions();
        mOptions.icon(BitmapDescriptorFactory.fromBitmap(Utils.getMarkerBitmapFromView(this, false)));
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
        if (polyline != null && !polyline.isEmpty()) {
            for (Polyline line : polyline) line.remove();
            polyline.clear();
        }
        marker.showInfoWindow();
        selectedRoute = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        String url = getSingleDirection(new LatLng(marker.getPosition().latitude,
                marker.getPosition().longitude));
        Log.i(TAG, "onMarkerClick: " + url);
        ReadTask downloadTask = new ReadTask(mMap);
        downloadTask.execute(url);
        return true;
    }

    private String getSingleDirection(LatLng latLng) {
        String OriDest = "origin=" + this.latLng.latitude + "," + this.latLng.longitude
                + "&destination=" + latLng.latitude + "," + latLng.longitude;
        String key = "key=" + getString(R.string.google_maps_key);
        String params = OriDest + "&" + key;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
    }

    public void drawRoute(View view) {
        selectedRoute = null;
        if (checkForLocation()) drawAllRoute();
    }

    public void startNavigation(View view) {
        if (!checkForLocation()) return;
        if (selectedRoute != null) singleNavigation(selectedRoute);
        else if (latLngList.size() > 0)
            multipleNavigation();
        else Toast.makeText(this, "No Parcels to start Navigation", Toast.LENGTH_SHORT).show();
    }

    private void multipleNavigation() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getMultiDirections(true)));
        Log.i(TAG, "multipleNavigation: " + getMultiDirections(true));
        intent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getMultiDirections(true)));
                startActivity(unrestrictedIntent);
            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(this, "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void singleNavigation(LatLng latLng) {
        Uri navigation = Uri.parse("google.navigation:q=" + latLng.latitude + "," + latLng.longitude + "");
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
        navigationIntent.setPackage("com.google.android.apps.maps");
        startActivity(navigationIntent);
    }

    @Override
    protected void onDestroy() {
        if (pubNub != null) {
            pubNub.destroy();
            pubNub.removeChannelsFromChannelGroup();
            pubNub.removePushNotificationsFromChannels();
        }
        if (mMap != null) mMap.clear();
        super.onDestroy();
    }
}