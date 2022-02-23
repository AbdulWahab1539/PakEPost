package com.app.fypfinal.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.adapters.TypeRecyclerViewAdapter;
import com.app.fypfinal.mvvm.pojo.Super;

import java.util.List;

public class ReceivedParcel extends Fragment implements Info, SwipeRefreshLayout.OnRefreshListener {

    View fragReceivedParcel;

    RecyclerView rvParcel;
    LinearLayout layoutNoParcels;
    SwipeRefreshLayout swipeRefreshLayout;
    TypeRecyclerViewAdapter typeRecyclerViewAdapter;
    List<Super> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragReceivedParcel = inflater.inflate(R.layout.activity_recieved_parcel, container, false);

        initViews();

        return fragReceivedParcel;
    }

    private void initViews() {
        rvParcel = fragReceivedParcel.findViewById(R.id.rv_parcel);
        layoutNoParcels = fragReceivedParcel.findViewById(R.id.ll_no_parcel);

        swipeRefreshLayout = fragReceivedParcel.findViewById(R.id.srl_parcel);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    public void configureAdapter(List<Super> list) {
        this.list = list;
        initAdapter();
        if (this.list != null && !this.list.isEmpty())
            layoutNoParcels.setVisibility(View.GONE);
        else
            layoutNoParcels.setVisibility(View.VISIBLE);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initAdapter() {
        Log.i(TAG, "initAdapter: " + list.size());
        typeRecyclerViewAdapter = new TypeRecyclerViewAdapter(requireContext(), list, TYPE_USER_RECEIVED_PARCEL);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        rvParcel.setLayoutManager(linearLayoutManager);
        rvParcel.smoothScrollToPosition(0);
        rvParcel.setAdapter(typeRecyclerViewAdapter);
        typeRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        configureAdapter(UserParcelHistory.listRec);
        super.onResume();
    }
}