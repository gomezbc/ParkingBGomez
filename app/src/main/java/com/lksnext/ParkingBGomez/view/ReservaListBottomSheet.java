package com.lksnext.ParkingBGomez.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.lksnext.ParkingBGomez.databinding.ReservaListBottomSheetBinding;
import com.lksnext.ParkingBGomez.domain.Reserva;

public class ReservaListBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "ReservaListBottomSheet";
    private ReservaListBottomSheetBinding binding;
    private Reserva reserva;


    public ReservaListBottomSheet(Reserva reserva) {
        this.reserva = reserva;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ReservaListBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

}
