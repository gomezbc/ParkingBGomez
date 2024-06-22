package com.lksnext.ParkingBGomez.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.databinding.ReservaListBottomSheetBinding;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.domain.ReservationsRefreshListener;

public class ReservaListBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "ReservaListBottomSheet";
    private ReservaListBottomSheetBinding binding;
    private final Reserva reserva;
    private final ReservationsRefreshListener refreshListener;


    public ReservaListBottomSheet(Reserva reserva, ReservationsRefreshListener refreshListener) {
        this.reserva = reserva;
        this.refreshListener = refreshListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ReservaListBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.reservaDeleteButton.setOnClickListener(v -> {
            DataRepository.getInstance().deleteReserva(reserva, new Callback() {
                @Override
                public void onSuccess() {
                    Snackbar.make(view, "Reserva eliminada", BaseTransientBottomBar.LENGTH_SHORT).show();
                    refreshListener.onReservationsRefreshRequested();
                }

                @Override
                public void onFailure() {
                    Snackbar.make(view, "Error al eliminar la reserva", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            });
            dismiss();
        });
    }
}
