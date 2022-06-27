package com.app.fypfinal.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.adapters.FragmentPagerAdapter;
import com.app.fypfinal.mvvm.mvvmutils.MVVMUtils;
import com.app.fypfinal.mvvm.pojo.ParcelPojo;
import com.app.fypfinal.mvvm.pojo.Super;
import com.app.fypfinal.mvvm.response.GenericResponse;
import com.app.fypfinal.utils.DialogUtils;
import com.app.fypfinal.utils.Utils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParcelHistory extends AppCompatActivity implements Info, SwipeRefreshLayout.OnRefreshListener {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    FragmentPagerAdapter fragmentPagerAdapter;
    public static List<Super> listSent, deliveredParcel, scannedParcel, listRec;
    public Dialog dialog;
    SwipeRefreshLayout swipeRefreshLayout;

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
        scannedParcel = new ArrayList<>();
        deliveredParcel = new ArrayList<>();

        dialog = new Dialog(this);
        DialogUtils.initLoadingDialog(dialog);

        swipeRefreshLayout = findViewById(R.id.srl_parcels);
        swipeRefreshLayout.setOnRefreshListener(this);
        initTabLayout();
    }

    //Whatsapp like Table format layout
    private void initTabLayout() {
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), getLifecycle());
        //Set Fragment from adapter
        viewPager.setAdapter(fragmentPagerAdapter);
        if (Utils.profilePojo.isCustomer()) {
            tabLayout.addTab(tabLayout.newTab().setText("Sent Parcels"));
            tabLayout.addTab(tabLayout.newTab().setText("Received Parcels"));
        } else if (Utils.profilePojo.isPostman()) {
            tabLayout.addTab(tabLayout.newTab().setText("Delivered Parcels"));
            tabLayout.addTab(tabLayout.newTab().setText("Scanned Parcels"));
        }

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
        initParcelsInfo();
    }

    public void initParcelsInfo() {
        if (listRec != null && !listRec.isEmpty()) listRec.clear();
        if (listSent != null && !listSent.isEmpty()) listSent.clear();
        if (deliveredParcel != null && !deliveredParcel.isEmpty()) deliveredParcel.clear();
        if (scannedParcel != null && !scannedParcel.isEmpty()) scannedParcel.clear();
        if (Utils.profilePojo.isCustomer()) {
            initParcels();
            initReceivedParcel();
        } else if (Utils.profilePojo.isPostman()) initParcels();
    }

    public void initParcels() {
        dialog.show();
        //Sent Parcel API
        MVVMUtils.getViewModelRepo(this)
                .getParcelsList(this)
                .observe(this, this::initParcelsListResponse);
    }

    private void initParcelsListResponse(GenericResponse<List<ParcelPojo>> listGenericResponse) {
        if (listGenericResponse.isSuccessful()) {
            listSent.addAll(listGenericResponse.getResponse());
            Log.i(TAG, "initParcelsListResponse: " + listSent.size());
            for (ParcelPojo parcelPojo : listGenericResponse.getResponse()) {
                if (!parcelPojo.getIsActive())
                    deliveredParcel.add(parcelPojo);
                else if (parcelPojo.getPostman() != null)
                    scannedParcel.add(parcelPojo);
            }
            if (Utils.profilePojo.isCustomer() && FragmentPagerAdapter.selectedFragment instanceof SentParcel)
                ((SentParcel) FragmentPagerAdapter.selectedFragment).configureAdapter(listSent);
            else if (Utils.profilePojo.isPostman() && FragmentPagerAdapter.selectedFragment instanceof SentParcel)
                ((SentParcel) FragmentPagerAdapter.selectedFragment).configureAdapter(deliveredParcel);
            else if (Utils.profilePojo.isPostman() && FragmentPagerAdapter.selectedFragment instanceof ReceivedParcel)
                ((ReceivedParcel) FragmentPagerAdapter.selectedFragment).configureAdapter(scannedParcel);
            else
                dialog.dismiss();
        } else {
            MVVMUtils.initErrMessages(this, listGenericResponse.getErrorMessages(), listGenericResponse.getResponseCode());
            dialog.dismiss();
        }
    }

    public void initReceivedParcel() {
        dialog.show();
        //Get User's recieved parcels
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
        listRec = null;
        deliveredParcel = null;
        scannedParcel = null;
        super.onDestroy();
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        initParcelsInfo();
    }
}