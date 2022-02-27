package com.app.fypfinal.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.mvvm.mvvmutils.MVVMUtils;
import com.app.fypfinal.mvvm.pojo.ParcelPojo;
import com.app.fypfinal.mvvm.response.GenericResponse;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.ArrayList;
import java.util.List;

public class Scanner extends AppCompatActivity implements Info {

    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private CameraSource cameraSource;
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    String intentData = "";
    List<String> trackingIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        initViews();
    }

    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        trackingIdList = new ArrayList<>();
    }

    private void initialiseDetectorsAndSources() {
        Log.i(TAG, "initialiseDetectorsAndSources: Barcode scanner started");
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            Scanner.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(Scanner.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Log.i(TAG, "release: To prevent memory leaks barcode scanner has been stopped");
            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(() -> {
                        intentData = barcodes.valueAt(0).displayValue;
                        txtBarcodeValue.setText(intentData);
//                        Toast.makeText(Scanner.this, "Parcel Scanned Successfully", Toast.LENGTH_SHORT).show();
                        if (intentData != null && !intentData.isEmpty())
                            excludeParcel(intentData);
                        else
                            Toast.makeText(Scanner.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void initPostmanParcel(String intentData) {
        MVVMUtils.getViewModelRepo(this)
                .getTrackingParcel(this, intentData)
                .observe(this, this::initPostmanParcelResponse);
    }

    private void initPostmanParcelResponse(GenericResponse<ParcelPojo> pojoGenericResponse) {
        if (pojoGenericResponse.isSuccessful()) {
            Log.i(TAG, "initPostmanParcelResponse: " + pojoGenericResponse.getResponse().getPostman());
            Toast.makeText(this, "Parcel added Successfully", Toast.LENGTH_SHORT).show();
            excludeParcel(intentData);
        } else
            MVVMUtils.initErrMessages(this, pojoGenericResponse.getErrorMessages(), pojoGenericResponse.getResponseCode());
    }

    private void excludeParcel(String intentData) {
        if (trackingIdList.isEmpty())
            trackingIdList.add(intentData);
        else {
            Log.i(TAG, "initViews: inside Loop");
            for (String trackingId : trackingIdList) {
                if (!trackingId.equals(intentData)) {
                    Log.i(TAG, "initViews: " + intentData);
                    trackingIdList.add(intentData);
                    initPostmanParcel(intentData);
                } else txtBarcodeValue.setText(R.string.already_scanned);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
}