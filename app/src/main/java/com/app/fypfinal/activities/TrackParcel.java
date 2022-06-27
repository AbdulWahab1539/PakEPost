package com.app.fypfinal.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.mvvm.mvvmutils.MVVMUtils;
import com.app.fypfinal.mvvm.pojo.ParcelPojo;
import com.app.fypfinal.mvvm.response.GenericResponse;
import com.app.fypfinal.utils.DialogUtils;
import com.app.fypfinal.utils.Utils;

public class TrackParcel extends AppCompatActivity implements Info {

    String strEtTrackingId;
    EditText etTrackingId;
    Dialog dialog;
    ParcelPojo parcelPojo;
    LinearLayout layoutDispatched, layoutOnWay, layoutCompleted;
    TextView tvDispatch, tvOnWay, tvCompleted;
    LottieAnimationView lottieDispatch, lottieCompleted, lottieOnWay, lottieArrow1, lottieArrow2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_parcel);

        initViews();
    }

    private void initViews() {
        etTrackingId = findViewById(R.id.et_tracking_num);

        layoutCompleted = findViewById(R.id.l_completed);
        lottieCompleted = findViewById(R.id.lav_completed);
        tvCompleted = findViewById(R.id.tv_completed);

        tvDispatch = findViewById(R.id.tv_dispatch);
        lottieDispatch = findViewById(R.id.lav_dispatch);
        layoutDispatched = findViewById(R.id.l_dispatch);

        tvOnWay = findViewById(R.id.tv_on_way);
        lottieOnWay = findViewById(R.id.lav_on_way);
        layoutOnWay = findViewById(R.id.l_on_way);

        lottieArrow1 = findViewById(R.id.lottie_arrow1);
        lottieArrow2 = findViewById(R.id.lottie_arrow2);

        dialog = new Dialog(this);
        DialogUtils.initLoadingDialog(dialog);
    }


    public void track(View view) {
        strEtTrackingId = etTrackingId.getText().toString();
        if (!Utils.validEt(etTrackingId, strEtTrackingId)) return;
        initTracking();
    }

    private void initTracking() {
        dialog.show();
        //Api call to fetch information of parcel through id
        MVVMUtils.getViewModelRepo(this)
                .getTrackingParcel(this, strEtTrackingId)
                .observe(this, this::initTrackingResponse);
    }

    private void initTrackingResponse(GenericResponse<ParcelPojo> response) {
        if (response.isSuccessful()) {
            parcelPojo = response.getResponse();
            Log.i(TAG, "initTrackingResponse: " + response.getResponse().getCreatedOn());
            updateUI();
        } else
            MVVMUtils.initErrMessages(this, response.getErrorMessages(), response.getResponseCode());
        dialog.cancel();
    }

    private void updateUI() {
        if (parcelPojo.getIsActive() && parcelPojo.getPostman() != null) {
            Log.i(TAG, "back: Parcel is on the way");
            layoutDispatched.setVisibility(View.VISIBLE);
            lottieDispatch.playAnimation();
            tvDispatch.setText(R.string.dispatched_user);

            layoutOnWay.setVisibility(View.VISIBLE);
            lottieOnWay.playAnimation();
            tvOnWay.setText(R.string.on_way);

            lottieArrow1.setVisibility(View.VISIBLE);
            lottieArrow1.playAnimation();

            lottieArrow2.setVisibility(View.GONE);
            layoutCompleted.setVisibility(View.GONE);
        } else if (!parcelPojo.getIsActive()) {
            layoutDispatched.setVisibility(View.VISIBLE);
            lottieDispatch.playAnimation();
            tvDispatch.setText(R.string.dispatched_user);

            layoutOnWay.setVisibility(View.VISIBLE);
            lottieOnWay.playAnimation();
            tvOnWay.setText(R.string.on_way);

            layoutCompleted.setVisibility(View.VISIBLE);
            lottieCompleted.playAnimation();
            tvCompleted.setText(R.string.delivered_user);

            lottieArrow1.setVisibility(View.VISIBLE);
            lottieArrow2.setVisibility(View.VISIBLE);
            lottieArrow1.playAnimation();
            lottieArrow2.playAnimation();
            Log.i(TAG, "back: parcel has been delivered");
        } else if (parcelPojo.getPostman() == null) {
            layoutDispatched.setVisibility(View.VISIBLE);
            lottieDispatch.playAnimation();
            tvDispatch.setText(R.string.dispatched);

            layoutCompleted.setVisibility(View.GONE);
            layoutOnWay.setVisibility(View.GONE);

            lottieArrow1.setVisibility(View.GONE);
            lottieArrow2.setVisibility(View.GONE);
            Log.i(TAG, "back: Parcel is at dispatch location");
        }
    }


    public void back(View view) {
        onBackPressed();
    }
}