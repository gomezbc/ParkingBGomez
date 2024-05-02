package com.lksnext.ParkingBGomez.view.holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.lksnext.ParkingBGomez.R;


public class ReservasViewHolder extends RecyclerView.ViewHolder {
    MaterialCardView materialCardView;

    public ReservasViewHolder(@NonNull View itemView) {
        super(itemView);
        materialCardView = itemView.findViewById(R.id.reservaItemCardView);
    }
}

