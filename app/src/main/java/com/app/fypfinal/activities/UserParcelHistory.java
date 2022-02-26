package com.app.fypfinal.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.adapters.FragmentPagerAdapter;
import com.app.fypfinal.mvvm.mvvmutils.MVVMUtils;
import com.app.fypfinal.mvvm.pojo.ParcelPojo;
import com.app.fypfinal.mvvm.pojo.Super;
import com.app.fypfinal.mvvm.response.GenericResponse;
import com.app.fypfinal.utils.DialogUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserParcelHistory extends AppCompatActivity implements Info {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    FragmentPagerAdapter fragmentPagerAdapter;
    List<Super> listSent;
    public static List<Super> listRec;
    public Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel_history);

        initViews();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Parcel History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = findViewById(R.id.mTabLayout);
        viewPager = findViewById(R.id.viewPager);

        listSent = new ArrayList<>();
        listRec = new ArrayList<>();

        dialog = new Dialog(this);
        DialogUtils.initLoadingDialog(dialog);

        initTabLayout();
    }

    private void initTabLayout() {
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.addTab(tabLayout.newTab().setText("Sent Parcels"));
        tabLayout.addTab(tabLayout.newTab().setText("Received Parcels"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected: " + position);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        initParcels();
        initReceivedParcel();
    }

    public void initParcels() {
        dialog.show();
        MVVMUtils.getViewModelRepo(this)
                .getParcelsList(this)
                .observe(this, this::initParcelsListResponse);
    }

    private void initParcelsListResponse(GenericResponse<List<ParcelPojo>> listGenericResponse) {
        if (listGenericResponse.isSuccessful()) {
            listSent.addAll(listGenericResponse.getResponse());
            Log.i(TAG, "initParcelsListResponse: " + listSent.size());
            if (FragmentPagerAdapter.selectedFragment instanceof SentParcel)
                ((SentParcel) FragmentPagerAdapter.selectedFragment).configureAdapter(listSent);
            else
                dialog.dismiss();
        } else {
            MVVMUtils.initErrMessages(this, listGenericResponse.getErrorMessages(), listGenericResponse.getResponseCode());
            dialog.dismiss();
        }
    }

    public void initReceivedParcel() {
        dialog.show();
        MVVMUtils.getViewModelRepo(this)
                .getReceiverParcels(this)
                .observe(this, this::initReceivedParcelResponse);
    }

    private void initReceivedParcelResponse(GenericResponse<List<ParcelPojo>> genericResponse) {
        if (genericResponse.isSuccessful()) {
            listRec.addAll(genericResponse.getResponse());
            Log.i(TAG, "initReceivedParcelResponse: " + listRec.size());
            if (FragmentPagerAdapter.selectedFragment instanceof ReceivedParcel)
                ((ReceivedParcel) FragmentPagerAdapter.selectedFragment).configureAdapter(listRec);
            else
                dialog.dismiss();
        } else {
            dialog.dismiss();
            MVVMUtils.initErrMessages(this, genericResponse.getErrorMessages(), genericResponse.getResponseCode());
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        listSent = null;
        super.onDestroy();
    }

    public void back(View view) {
        onBackPressed();
    }
}