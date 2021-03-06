package com.app.fypfinal.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.mvvm.mvvmutils.MVVMUtils;
import com.app.fypfinal.mvvm.pojo.ParcelPojo;
import com.app.fypfinal.mvvm.response.GenericResponse;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class Scanner extends AppCompatActivity implements Info {

    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private CameraSource cameraSource;
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    String intentData = "";
    boolean cameraSourceStarted;
    String previousId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        initViews();
    }

    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
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
                cameraSourceStarted = true;
                initBarCodeCamera();
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
                        Log.i(TAG, "receiveDetections: " + intentData);
                        txtBarcodeValue.setText(intentData);
                        if (previousId == null)
                            initPostmanParcel(intentData);
                        else if (!previousId.equals(intentData))
                            initPostmanParcel(intentData);
                        else txtBarcodeValue.setText(R.string.already_scanned);
                        previousId = intentData;
                    });
                }
            }
        });
    }

    //Get camera permissions
    private void initBarCodeCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    Scanner.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraSource.start(surfaceView.getHolder());
            } else ActivityCompat.requestPermissions(Scanner.this, new
                    String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    try {
                        //access the camera
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else
                Toast.makeText(this, "Camera Permission is required to use this feature", Toast.LENGTH_SHORT).show();
        }
    }

    private void initPostmanParcel(String intentData) {
        //Associates parcel with postman using barcode id
        MVVMUtils.getViewModelRepo(this)
                .getTrackingParcel(this, intentData)
                .observe(this, this::initPostmanParcelResponse);
    }

    private void initPostmanParcelResponse(GenericResponse<ParcelPojo> pojoGenericResponse) {
        if (pojoGenericResponse.isSuccessful()) {
            Log.i(TAG, "initPostmanParcelResponse: " + pojoGenericResponse.getResponse().getPostman());
            Toast.makeText(this, "Parcel added Successfully", Toast.LENGTH_SHORT).show();
        } else
            MVVMUtils.initErrMessages(this, pojoGenericResponse.getErrorMessages(), pojoGenericResponse.getResponseCode());
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
        cameraSourceStarted = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
        //Starts camera after delay of 500 milli seconds
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (!cameraSourceStarted)
                initBarCodeCamera();
        }, 500);
    }
}