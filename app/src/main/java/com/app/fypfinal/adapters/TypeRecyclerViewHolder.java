package com.app.fypfinal.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fypfinal.R;

public class TypeRecyclerViewHolder extends RecyclerView.ViewHolder {

    TextView tvServiceType,
            tvCharges,
            tvLocation,
            tvReceiver,
            tvDetails;
    Button btnDelievred;


    public TypeRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        tvLocation = itemView.findViewById(R.id.tvv_address);
        tvCharges = itemView.findViewById(R.id.tvv_charges);
        tvReceiver = itemView.findViewById(R.id.tvv_receiver);
        tvServiceType = itemView.findViewById(R.id.tvv_service_type);
        tvDetails = itemView.findViewById(R.id.tvv_details);

        btnDelievred = itemView.findViewById(R.id.btn_delievred);
    }


}
