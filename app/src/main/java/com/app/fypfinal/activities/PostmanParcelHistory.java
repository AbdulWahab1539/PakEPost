package com.app.fypfinal.activities;

import android.annotation.SuppressLint;
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

import java.util.List;

public class PostmanParcelHistory extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, Info {

    RecyclerView rvParcel;
    LinearLayout layoutNoParcels;
    SwipeRefreshLayout swipeRefreshLayout;
    TypeRecyclerViewAdapter typeRecyclerViewAdapter;
    List<Super> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postman_parcel_history);

        initViews();
    }


    private void initViews() {
        rvParcel = findViewById(R.id.rv_parcel);
        layoutNoParcels = findViewById(R.id.ll_no_parcel);

        swipeRefreshLayout = findViewById(R.id.srl_parcel);
        swipeRefreshLayout.setOnRefreshListener(this);

        initParcels();
    }

    public void initParcels() {
//        dialog.show();
        MVVMUtils.getViewModelRepo(this)
                .getParcelsList(this)
                .observe(this, this::initParcelsListResponse);
    }

    private void initParcelsListResponse(GenericResponse<List<ParcelPojo>> listGenericResponse) {
        if (listGenericResponse.isSuccessful()) {
            list.addAll(listGenericResponse.getResponse());
            Log.i(TAG, "initParcelsListResponse: " + list.size());
            configureAdapter();
        } else
            MVVMUtils.initErrMessages(this, listGenericResponse.getErrorMessages(), listGenericResponse.getResponseCode());
    }

    public void configureAdapter() {
        initAdapter();
        if (this.list != null && !this.list.isEmpty())
            layoutNoParcels.setVisibility(View.GONE);
        else
            layoutNoParcels.setVisibility(View.VISIBLE);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initAdapter() {
        Log.i(TAG, "initAdapter: " + list.size());
        typeRecyclerViewAdapter = new TypeRecyclerViewAdapter(this, list, TYPE_POSTMAN_PARCEL);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvParcel.setLayoutManager(linearLayoutManager);
        rvParcel.smoothScrollToPosition(0);
        rvParcel.setAdapter(typeRecyclerViewAdapter);
        typeRecyclerViewAdapter.notifyDataSetChanged();
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }
}