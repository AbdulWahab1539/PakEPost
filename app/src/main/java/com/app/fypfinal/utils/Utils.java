package com.app.fypfinal.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.activities.ProfileActivity;
import com.app.fypfinal.mvvm.pojo.ProfilePojo;
import com.bumptech.glide.Glide;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils implements Info {
    public static ProfilePojo profilePojo;

    public static boolean validEt(EditText etUserName, String strEtUserName) {
        if (strEtUserName.isEmpty()) {
            etUserName.setError("Field Empty");
            return false;
        }
        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean validatePhoneNumber(String phNumber) {
        Pattern mobNO = Pattern.compile(NUMBER_REGEX);
        Matcher matcher = mobNO.matcher(phNumber);
        return matcher.find();
    }

    public static void checkLocationPermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                new android.app.AlertDialog.Builder(context)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", (dialogInterface, i) ->
                                ActivityCompat.requestPermissions(context,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION}, 1)).create().show();
            } else ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    public static void initProfileUpdateDialog(Context context) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Profile Update")
                .setMessage("Please update your address and location coordinates in profile settings. Other wise Postman will not be" +
                        " able to reach your desired destination.")
                .setCancelable(false)
                .setPositiveButton("Update", (dialogInterface, i) -> {
                    context.startActivity(new Intent(context, ProfileActivity.class));
                    dialogInterface.dismiss();
                }).create().show();
    }

    public static void initLocationPermissionDialog(Context context) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Permission Denied")
                .setMessage("You have denied location permission previously please enable it from settings in order to " +
                        "use this feature.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
    }


    public static Bitmap getMarkerBitmapFromView(Context context) {
        if (context == null) return null;
        Log.i(TAG, "getMarkerBitmapFromView: ");
        @SuppressLint("InflateParams") View customMarkerView = ((LayoutInflater) Objects.requireNonNull(context).getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_marker, null);
        ImageView ivImage = customMarkerView.findViewById(R.id.marker_image);
        if (Utils.profilePojo != null
                && Utils.profilePojo.getProfileImage() != null
                && !Utils.profilePojo.getProfileImage().isEmpty())
            Glide.with(context.getApplicationContext())
                    .load(Utils.profilePojo.getProfileImage())
                    .circleCrop()
                    .into(ivImage);
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

    private static boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;
    }
}
