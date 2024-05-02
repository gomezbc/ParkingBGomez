package com.lksnext.ParkingBGomez.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.view.holder.ReservasViewHolder;

public class ReservasAdapter extends ListAdapter<Reserva, ReservasViewHolder> {

    public ReservasAdapter() {
        super(Reserva.DIFF_CALLBACK);
    }


    @NonNull
    @Override
    public ReservasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserva, parent, false);
        return new ReservasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservasViewHolder holder, int position) {
        //holder.bindTo(getItem(position));
    }
}