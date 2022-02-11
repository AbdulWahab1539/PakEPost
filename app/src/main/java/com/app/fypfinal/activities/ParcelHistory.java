package com.app.fypfinal.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.adapters.TypeRecyclerViewAdapter;
import com.app.fypfinal.models.Super;

import java.util.List;

public class ParcelHistory extends AppCompatActivity implements Info, SwipeRefreshLayout.OnRefreshListener {

    RecyclerView rvParcel;
    LinearLayout layoutNoParcels;
    SwipeRefreshLayout swipeRefreshLayout;
    TypeRecyclerViewAdapter typeRecyclerViewAdapter;
    List<Super> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel_history);

        initViews();
    }

    private void initViews() {
        rvParcel = findViewById(R.id.rv_parcel);
        layoutNoParcels = findViewById(R.id.ll_no_parcel);

        swipeRefreshLayout = findViewById(R.id.srl_parcel);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    public void configureAdapter() {
        initAdapter();
        if (list != null && !list.isEmpty())
            layoutNoParcels.setVisibility(View.GONE);
        else
            layoutNoParcels.setVisibility(View.VISIBLE);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initAdapter() {
        typeRecyclerViewAdapter = new TypeRecyclerViewAdapter(getApplicationContext(), list, TYPE_POSTMAN_PARCEL);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
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