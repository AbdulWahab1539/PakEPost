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

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.adapters.TypeRecyclerViewAdapter;
import com.app.fypfinal.mvvm.pojo.Super;

import java.util.List;

public class SentParcel extends Fragment implements Info {

    View fragSentParcel;
    RecyclerView rvParcel;
    LinearLayout layoutNoParcels;
    TypeRecyclerViewAdapter typeRecyclerViewAdapter;
    List<Super> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragSentParcel = inflater.inflate(R.layout.activity_recieved_parcel, container, false);
        initViews();
        return fragSentParcel;
    }

    private void initViews() {
        rvParcel = fragSentParcel.findViewById(R.id.rv_parcel);
        layoutNoParcels = fragSentParcel.findViewById(R.id.ll_no_parcel);
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
        ((UserParcelHistory) requireContext()).dialog.dismiss();
        typeRecyclerViewAdapter = new TypeRecyclerViewAdapter(requireContext(), list, TYPE_USER_PARCEL);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        rvParcel.setLayoutManager(linearLayoutManager);
        rvParcel.smoothScrollToPosition(0);
        rvParcel.setAdapter(typeRecyclerViewAdapter);
        typeRecyclerViewAdapter.notifyDataSetChanged();
    }

}