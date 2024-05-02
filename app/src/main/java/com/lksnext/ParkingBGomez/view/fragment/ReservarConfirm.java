package com.lksnext.ParkingBGomez.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.databinding.FragmentReservarConfirmBinding;
import com.lksnext.ParkingBGomez.domain.Hora;
import com.lksnext.ParkingBGomez.enums.ReservarState;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.viewmodel.MainViewModel;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.TextStyle;


public class ReservarConfirm extends Fragment {

    private FragmentReservarConfirmBinding binding;
    private MainViewModel mainViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReservarConfirmBinding.inflate(inflater, container, false);

        setCardInfo();

        return binding.getRoot();
    }

    private void setCardInfo() {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        final Hora hora = setSelectedHourInterval();
        setTipoPlazaInfo();
        setSelectedDateInfo(hora);
    }

    @Nullable
    private Hora setSelectedHourInterval() {
        Hora hora = mainViewModel.getSelectedHour().getValue();
        if (hora != null) {
            final Duration duration = Duration.between(hora.horaInicio(), hora.horaFin());
            final long minutesToHour = duration.toMinutes() % 60;
            if (minutesToHour == 0L) {
                binding.chipSelectedDuration.setText(String.format("%s h", duration.toHours()));
            } else {
                binding.chipSelectedDuration.setText(
                        String.format("%s,%s h", duration.toHours(), minutesToHour));
            }
        }
        return hora;
    }

    private void setSelectedDateInfo(Hora hora) {
        LocalDate fecha = mainViewModel.getSelectedDate().getValue();
        if (fecha != null && hora != null) {
            final String dia = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, java.util.Locale.getDefault());
            final String mes = fecha.getMonth().getDisplayName(TextStyle.FULL, java.util.Locale.getDefault());
            binding.confirmHourInterval.setText(String.format("%s %s %s %s - %s",
                    dia, fecha.getDayOfMonth(), mes, hora.horaInicio(), hora.horaFin()));
        }
    }

    private void setTipoPlazaInfo() {
        TipoPlaza tipoPlaza = mainViewModel.getSelectedTipoPlaza().getValue();
        if (tipoPlaza != null) {
            switch (tipoPlaza){
                case ESTANDAR:
                    binding.chipCar.setVisibility(View.VISIBLE);
                    break;
                case ELECTRICO:
                    binding.chipElectricCar.setVisibility(View.VISIBLE);
                    break;
                case MOTO:
                    binding.chipMotorcycle.setVisibility(View.VISIBLE);
                    break;
                case DISCAPACITADO:
                    binding.chipAccessibleCar.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbarReservarConfirm.setNavigationOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_reservarConfirm_to_reservarMainFragment));

        binding.buttonReservarContinue.setOnClickListener(v -> {

            mainViewModel.setReservarState(ReservarState.RESERVADO);

            Navigation.findNavController(v)
                    .navigate(R.id.action_reservarConfirm_to_reservarMainFragment_confirmed);
        });
    }
}