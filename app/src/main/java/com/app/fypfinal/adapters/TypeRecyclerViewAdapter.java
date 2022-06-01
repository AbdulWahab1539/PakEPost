package com.app.fypfinal.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.activities.ScannedParcel;
import com.app.fypfinal.mvvm.mvvmutils.MVVMUtils;
import com.app.fypfinal.mvvm.pojo.Generic;
import com.app.fypfinal.mvvm.pojo.ParcelPojo;
import com.app.fypfinal.mvvm.pojo.Super;
import com.app.fypfinal.mvvm.response.GenericResponse;
import com.app.fypfinal.utils.Utils;

import java.util.List;

public class TypeRecyclerViewAdapter extends RecyclerView.Adapter<TypeRecyclerViewHolder> implements Info {

    Context context;
    List<Super> listInstances;
    int type;
    ParcelPojo parcelPojo;
    boolean isActiveParcel;

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
        else if (type == TYPE_POSTMAN_SCANNED_PARCEL)
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
            initPostmanParcels(holder, position);
            return;
        }
        if (type == TYPE_POSTMAN_SCANNED_PARCEL) {
            initPostmanParcels(holder, position);
            return;
        }
        if (type == TYPE_USER_PARCEL) initParcels(holder, position);

    }

    private void initPostmanParcels(TypeRecyclerViewHolder holder, int position) {
        parcelPojo = (ParcelPojo) listInstances.get(position);
        holder.tvReceiver.setText(String.format("%s %s", parcelPojo.getReceiver().getFirstName(),
                parcelPojo.getReceiver().getLastName()));
        holder.tvLocation.setText(parcelPojo.getReceiver().getAddress());
        holder.tvDetails.setText(parcelPojo.getDetails());
        holder.tvServiceType.setText(parcelPojo.getServiceType());
        holder.tvCharges.setText(String.valueOf(parcelPojo.getPostalCharges()));
        holder.tvPhone.setText(parcelPojo.getReceiver().getPhoneNumber());
        if (type == TYPE_POSTMAN_SCANNED_PARCEL) {
            holder.btnDelievred.setVisibility(View.VISIBLE);
            holder.btnDelievred.setOnClickListener(view -> initParcelStatus(position));
        }
    }

    private void initParcelStatus(int position) {
        ParcelPojo parcelPojo = (ParcelPojo) listInstances.get(position);
        ((ScannedParcel) context).dialog.show();
        Log.i(TAG, "initParcelStatus: " + parcelPojo.getTrackingId());
        MVVMUtils.getViewModelRepo((Activity) context)
                .markParcelDelievred((Activity) context, parcelPojo.getTrackingId())
                .observe((LifecycleOwner) context, this::initMarkResponse);
    }

    private void initMarkResponse(GenericResponse<Generic> genericGenericResponse) {
        ((ScannedParcel) context).dialog.dismiss();
        if (genericGenericResponse.isSuccessful()) {
            if (Utils.profilePojo.isPostman())
                Toast.makeText(context, "Parcel Marked as Delievred Successfully", Toast.LENGTH_SHORT).show();
            else if (Utils.profilePojo.isCustomer() && !isActiveParcel)
                Toast.makeText(context, "Parcel Marked as Not Received Successfully", Toast.LENGTH_SHORT).show();
            else if (Utils.profilePojo.isCustomer() && isActiveParcel)
                Toast.makeText(context, "Parcel Marked as Received Successfully", Toast.LENGTH_SHORT).show();
            ((ScannedParcel) context).initParcels();
        } else
            MVVMUtils.initErrMessages(context, genericGenericResponse.getErrorMessages(), genericGenericResponse.getResponseCode());
    }

    private void initParcels(TypeRecyclerViewHolder holder, int position) {
        parcelPojo = (ParcelPojo) listInstances.get(position);
        if (type == TYPE_USER_PARCEL) {
            holder.tvReceiver.setText(String.format("%s %s", parcelPojo.getReceiver().getFirstName(),
                    parcelPojo.getReceiver().getLastName()));
            holder.tvLocation.setText(parcelPojo.getReceiver().getAddress());
        } else if (type == TYPE_USER_RECEIVED_PARCEL) {
            if (parcelPojo.getSender() != null && parcelPojo.getSender() != null)
                holder.tvReceiver.setText(String.format("%s %s", parcelPojo.getSender().getFirstName(),
                        parcelPojo.getSender().getLastName()));
            if (parcelPojo.getSender() != null)
                holder.tvLocation.setText(parcelPojo.getSender().getAddress());
        }
        holder.tvDetails.setText(parcelPojo.getDetails());
        holder.tvServiceType.setText(parcelPojo.getServiceType());
        holder.tvCharges.setText(String.valueOf(parcelPojo.getPostalCharges()));
        if (type == TYPE_USER_RECEIVED_PARCEL) {
            isActiveParcel = parcelPojo.getIsActive();
            if (parcelPojo.getIsActive()) holder.btnDelievred.setText(R.string.received);
            else holder.btnDelievred.setText(R.string.un_received);
            holder.btnDelievred.setVisibility(View.VISIBLE);
            if (parcelPojo.getIsActive() && parcelPojo.getPostman() == null)
                holder.btnDelievred.setVisibility(View.GONE);
            holder.btnDelievred.setOnClickListener(view -> initParcelStatus(position));
        }
    }

    @Override
    public int getItemCount() {
        if (listInstances != null)
            return listInstances.size();
        else
            return 0;
    }
}
