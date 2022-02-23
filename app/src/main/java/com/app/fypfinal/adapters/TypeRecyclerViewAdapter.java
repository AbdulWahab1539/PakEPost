package com.app.fypfinal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.mvvm.pojo.ParcelPojo;
import com.app.fypfinal.mvvm.pojo.Super;

import java.util.List;

public class TypeRecyclerViewAdapter extends RecyclerView.Adapter<TypeRecyclerViewHolder> implements Info {

    Context context;
    List<Super> listInstances;
    int type;
    ParcelPojo parcelPojo;

    public TypeRecyclerViewAdapter(Context context, List<Super> listInstances, int type) {
        this.context = context;
        this.listInstances = listInstances;
        this.type = type;
    }

    @NonNull
    @Override
    public TypeRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (type == TYPE_POSTMAN_PARCEL)
            view = layoutInflater.inflate(R.layout.layout_parcels, parent, false);
        else if (type == TYPE_USER_RECEIVED_PARCEL)
            view = layoutInflater.inflate(R.layout.layout_parcels, parent, false);
        else
            view = layoutInflater.inflate(R.layout.layout_parcels, parent, false);
        return new TypeRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeRecyclerViewHolder holder, int position) {
        if (type == TYPE_USER_RECEIVED_PARCEL) {
            initParcels(holder, position);
            return;
        }
        if (type == TYPE_POSTMAN_PARCEL) {
            initParcels(holder, position);
            return;
        }
        if (type == TYPE_USER_PARCEL) initParcels(holder, position);

    }

    private void initParcels(TypeRecyclerViewHolder holder, int position) {
        parcelPojo = (ParcelPojo) listInstances.get(position);
        holder.tvServiceType.setText(parcelPojo.getServiceType());
        holder.tvReceiver.setText(String.valueOf(parcelPojo.getReceiver()));
        holder.tvLocation.setText(parcelPojo.getDispatchLocation());
        holder.tvCharges.setText(String.valueOf(parcelPojo.getPostalCharges()));
    }


    @Override
    public int getItemCount() {
        if (listInstances != null)
            return listInstances.size();
        else
            return 0;
    }
}
