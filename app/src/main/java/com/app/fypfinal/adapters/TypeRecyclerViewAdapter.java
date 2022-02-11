package com.app.fypfinal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.models.Super;

import java.util.List;

public class TypeRecyclerViewAdapter extends RecyclerView.Adapter<TypeRecyclerViewHolder> implements Info {

    Context context;
    List<Super> listInstances;
    int type;

    public TypeRecyclerViewAdapter(Context context, List<Super> listInstances, int type) {
        this.context = context;
        this.listInstances = listInstances;
        this.type = type;
    }

    @NonNull
    @Override
    public TypeRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
//        if (type == TYPE_PARTICIPANTS)
//            view = layoutInflater.inflate(R.layout.participated_users, parent, false);
//        else if (type == TYPE_ORDERS)
//            view = layoutInflater.inflate(R.layout.layout_custom_order, parent, false);
//        else
//            view = layoutInflater.inflate(R.layout.campaign_started, parent, false);
        return new TypeRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeRecyclerViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        if (listInstances != null)
            return listInstances.size();
        else
            return 0;
    }
}
