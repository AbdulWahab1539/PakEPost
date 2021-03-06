package com.app.fypfinal.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.adapters.TypeRecyclerViewAdapter;
import com.app.fypfinal.mvvm.mvvmutils.MVVMUtils;
import com.app.fypfinal.mvvm.pojo.ParcelPojo;
import com.app.fypfinal.mvvm.pojo.Super;
import com.app.fypfinal.mvvm.response.GenericResponse;
import com.app.fypfinal.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

public class DelievredParcels extends AppCompatActivity implements Info, SwipeRefreshLayout.OnRefreshListener {


    RecyclerView rvParcel;
    LinearLayout layoutNoParcels;
    TypeRecyclerViewAdapter typeRecyclerViewAdapter;
    List<Super> list;
    Dialog dialog;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delievred_parcels);

        initViews();
    }


    private void initViews() {
        dialog = new Dialog(this);
        DialogUtils.initLoadingDialog(dialog);
        rvParcel = findViewById(R.id.rv_parcel);
        layoutNoParcels = findViewById(R.id.ll_no_parcel);
        swipeRefreshLayout = findViewById(R.id.srl_scanned);
        swipeRefreshLayout.setOnRefreshListener(this);
        list = new ArrayList<>();
        initParcels();
    }

    public void initParcels() {
        dialog.show();
        list.clear();
        MVVMUtils.getViewModelRepo(this)
                .getParcelsList(this)
                .observe(this, this::initParcelsListResponse);
    }

    private void initParcelsListResponse(GenericResponse<List<ParcelPojo>> listGenericResponse) {
        if (listGenericResponse.isSuccessful()) {
            for (ParcelPojo parcelPojo : listGenericResponse.getResponse())
                if (!parcelPojo.getIsActive())
                    list.add(parcelPojo);
            configureAdapter();
        } else {
            MVVMUtils.initErrMessages(this, listGenericResponse.getErrorMessages(), listGenericResponse.getResponseCode());
            dialog.dismiss();
        }
    }

    public void configureAdapter() {
        initAdapter();
        if (this.list != null && !this.list.isEmpty()) layoutNoParcels.setVisibility(View.GONE);
        else layoutNoParcels.setVisibility(View.VISIBLE);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initAdapter() {
        Log.i(TAG, "initAdapter: " + list.size());
        dialog.dismiss();
        typeRecyclerViewAdapter = new TypeRecyclerViewAdapter(this, list, TYPE_POSTMAN_PARCEL);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvParcel.setLayoutManager(linearLayoutManager);
        rvParcel.smoothScrollToPosition(0);
        rvParcel.setAdapter(typeRecyclerViewAdapter);
        typeRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onRefresh() {
        initParcels();
        swipeRefreshLayout.setRefreshing(false);
    }
}