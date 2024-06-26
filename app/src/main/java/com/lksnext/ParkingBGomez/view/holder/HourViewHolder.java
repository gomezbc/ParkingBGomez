package com.lksnext.ParkingBGomez.view.holder;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.ParkingBGomez.R;


public class HourViewHolder extends RecyclerView.ViewHolder {
    private Button button;

    public HourViewHolder(@NonNull View itemView) {
        super(itemView);
        button = itemView.findViewById(R.id.btnTimeSlot);
    }

    public Button getButton() {
        return button;
    }
}

