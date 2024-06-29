package com.lksnext.ParkingBGomez.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.databinding.ReservaListBottomSheetBinding;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.domain.ReservationsRefreshListener;
import com.lksnext.ParkingBGomez.utils.TimeUtils;

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

        long nowEpoch = TimeUtils.getNowEpoch();
        long reservaEpoch = reserva.getFecha().getSeconds();

        if (nowEpoch >= reservaEpoch) {
            binding.reservaInfoText.setVisibility(View.VISIBLE);
            binding.reservaEditButton.setEnabled(false);

            binding.reservaDeleteButton.setVisibility(View.GONE);
            binding.reservaDeleteButtonDisabled.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.reservaEditButton.setOnClickListener(v -> {
            ReservasMainFragmentDirections.ActionReservasMainFragmentToModifyReservaMainFragment action =
                    ReservasMainFragmentDirections.actionReservasMainFragmentToModifyReservaMainFragment(reserva.getUuid());
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(action);
            dismiss();
        });

        binding.reservaDeleteButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.cancel_reserva)
                    .setMessage(R.string.cancel_reserva_text)
                    .setIcon(R.drawable.baseline_delete_forever_24)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
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
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss()).show();

            dismiss();
        });
    }
}
