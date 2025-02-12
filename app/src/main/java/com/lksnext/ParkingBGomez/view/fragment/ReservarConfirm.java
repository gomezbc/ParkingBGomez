package com.lksnext.ParkingBGomez.view.fragment;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.databinding.FragmentReservarConfirmBinding;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.domain.Hora;
import com.lksnext.ParkingBGomez.domain.Plaza;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.enums.ReservarState;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.utils.TimeUtils;
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
    private Plaza plazaToReserve;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReservarConfirmBinding.inflate(inflater, container, false);

        // Disable the button until the data is loaded
        binding.buttonReservarConfirmar.setEnabled(false);

        DataRepository dataRepository = DataRepository.getInstance();

        setCardInfo(dataRepository);

        return binding.getRoot();
    }

    private void setCardInfo(DataRepository dataRepository) {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        TipoPlaza tipoPlaza = mainViewModel.getSelectedTipoPlaza().getValue();

        final Hora hora = setSelectedHourInterval();
        fetchAndSetPlazaLibre(dataRepository, tipoPlaza, hora);
        setTipoPlazaInfo();
        setSelectedDateInfo(hora);
    }

    private void fetchAndSetPlazaLibre(DataRepository dataRepository, TipoPlaza tipoPlaza, Hora hora) {
        LiveData<Plaza> plazaLiveData = dataRepository.getPlazaNotReservadaByTipoPlaza(getViewLifecycleOwner(), tipoPlaza, hora, new Callback() {
            @Override
            public void onSuccess() {
                binding.buttonReservarConfirmar.setEnabled(true);
                binding.progressBarConfirmReserva.hide();
            }

            @Override
            public void onFailure() {
                Snackbar.make(binding.getRoot(),
                        "La aplicación no ha podido cargar plazas libres. Intentalo de nuevo más tarde.",
                        BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
        plazaLiveData.observe(getViewLifecycleOwner(), plaza -> {
            if (plaza != null) {
                binding.chipParkingSlot.setText(String.valueOf(plaza.getId()));
                plazaToReserve = plaza;
            }else {
                Snackbar.make(binding.getRoot(),
                        "No hay plazas disponibles para reservar.",
                        BaseTransientBottomBar.LENGTH_LONG).show();
                binding.buttonReservarConfirmar.setEnabled(false);
            }
            plazaLiveData.removeObservers(getViewLifecycleOwner());
        });
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

            DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
            final Date horaInicioDate = new Date(hora.getHoraInicio() * 1000);
            final Date horaFinDate = new Date(hora.getHoraFin() * 1000);
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
        binding.toolbarReservarConfirm.setNavigationOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
        });

        binding.buttonReservarConfirmar.setOnClickListener(v -> {

            long horaInicioEpoch =
                    Objects.requireNonNull(mainViewModel.getSelectedHour().getValue()).getHoraInicio();

            LocalDateTime fechaHoraInicio = TimeUtils.convertEpochTolocalDateTime(horaInicioEpoch);

            Reserva reserva = new Reserva(TimeUtils.convertLocalDateTimeToTimestamp(fechaHoraInicio),
                    DataRepository.getInstance().getCurrentUser().getUid(),
                    UUID.randomUUID().toString(),
                    plazaToReserve,
                    mainViewModel.getSelectedHour().getValue());

            DataRepository.getInstance().saveReserva(reserva, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d("addReserva","Reserva added: " + reserva);
                    mainViewModel.setReservarState(ReservarState.RESERVADO);
                    mainViewModel.setSelectedHour(null);
                    NavController navController = Navigation.findNavController(v);
                    navController.navigateUp();
                }

                @Override
                public void onFailure() {
                    Snackbar.make(v,
                            "La aplicación no ha podido guardar la reserva. Intentalo de nuevo más tarde.",
                            BaseTransientBottomBar.LENGTH_LONG).show();
                }
            });

        });
    }
}