package com.lksnext.ParkingBGomez.view.fragment;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.databinding.FragmentReservarConfirmBinding;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.domain.Hora;
import com.lksnext.ParkingBGomez.domain.Plaza;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.enums.ReservarState;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.utils.TimeUtils;
import com.lksnext.ParkingBGomez.view.activity.MainActivity;
import com.lksnext.ParkingBGomez.viewmodel.MainViewModel;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;


public class ReservarConfirm extends Fragment {

    private FragmentReservarConfirmBinding binding;
    private MainViewModel mainViewModel;

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
            final Duration duration = Duration.between(
                    TimeUtils.convertEpochTolocalDateTime(hora.getHoraInicio()),
                    TimeUtils.convertEpochTolocalDateTime(hora.getHoraFin()));
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
            // TODO: fix how its shown
            DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
            final Date horaInicioDate = new Date(hora.getHoraInicio());
            final Date horaFinDate = new Date(hora.getHoraFin());
            final String horaInicioString = df.format(horaInicioDate);
            final String horaFinString = df.format(horaFinDate);
            binding.confirmHourInterval.setText(String.format("%s %s %s %s - %s",
                    dia, fecha.getDayOfMonth(), mes, horaInicioString, horaFinString));
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

            LocalDate selectedDate = mainViewModel.getSelectedDate().getValue();
            long horaInicioEpoch =
                    Objects.requireNonNull(mainViewModel.getSelectedHour().getValue()).getHoraInicio();

            LocalDateTime fechaHoraInicio = TimeUtils.convertEpochTolocalDateTime(horaInicioEpoch);

            Reserva reserva = new Reserva(fechaHoraInicio.toString(),
                    "usuario",
                    UUID.randomUUID().toString(),
                    new Plaza(1L, mainViewModel.getSelectedTipoPlaza().getValue()),
                    mainViewModel.getSelectedHour().getValue());

            MainActivity activity = (MainActivity) getActivity();

            DataRepository dataRepository = DataRepository.getInstance();

            if (activity != null) {
                dataRepository.saveReserva(reserva, activity, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("addReserva","DocumentSnapshot added: ");
                        Navigation.findNavController(v)
                                .navigate(R.id.action_reservarConfirm_to_reservarMainFragment_confirmed);
                    }

                    @Override
                    public void onFailure() {
                        Log.d("addReserva","Error adding document: ");
                    }
                });
            }

        });
    }
}