package com.lksnext.ParkingBGomez.view.fragment;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.databinding.FragmentModifyReservarConfirmBinding;
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


public class ModifyReservarConfirm extends Fragment {

    private static final String TAG = "ModifyReservarConfirm";
    private FragmentModifyReservarConfirmBinding binding;
    private MainViewModel mainViewModel;
    private Plaza plazaToReserve;
    private String reservaUuid;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentModifyReservarConfirmBinding.inflate(inflater, container, false);

        // Disable the button until the data is loaded
        binding.buttonReservarConfirmar.setEnabled(false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        reservaUuid = ModifyReservarConfirmArgs.fromBundle(getArguments()).getReservaToModifyUuid();

        setPreviousCardInfo(reservaUuid);
        setNewCardInfo();

        return binding.getRoot();
    }

    private void setPreviousCardInfo(String reservaUuid) {
        LiveData<Reserva> reservaLiveData = DataRepository.getInstance().getReservaByUuid(reservaUuid, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "fetchAndSetPreviousReserva: onSuccess");
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "fetchAndSetPreviousReserva: onFailure");
                Snackbar.make(binding.getRoot(),
                        "La aplicación no ha podido cargar la reserva. Intentalo de nuevo más tarde.",
                        BaseTransientBottomBar.LENGTH_LONG).show();
                binding.buttonReservarConfirmar.setEnabled(false);
            }
        });
        reservaLiveData.observe(getViewLifecycleOwner(), reserva -> {
            LocalDate fecha = TimeUtils.convertTimestampToLocalDateTime(reserva.getFecha()).toLocalDate();

            setSelectedHourInterval(reserva.getHora(), binding.chipSelectedDuration);
            setTipoPlazaInfo(reserva.getPlaza().getTipoPlaza(), false);
            setSelectedDateInfo(reserva.getHora(), fecha, binding.confirmHourInterval);
            binding.chipParkingSlot.setText(String.valueOf(reserva.getPlaza().getId()));

            reservaLiveData.removeObservers(getViewLifecycleOwner());
        });
    }

    private void setNewCardInfo() {
        TipoPlaza tipoPlaza = mainViewModel.getNewSelectedTipoPlaza().getValue();
        Hora hora = mainViewModel.getSelectedHour().getValue();

        setSelectedHourInterval(hora, binding.chipNewSelectedDuration);
        setTipoPlazaInfo(tipoPlaza, true);
        setSelectedDateInfo(hora, mainViewModel.getNewSelectedDate().getValue(), binding.confirmNewHourInterval);
        fetchAndSetNewPlazaLibre(tipoPlaza, hora);
    }

    private void fetchAndSetNewPlazaLibre(TipoPlaza tipoPlaza, Hora hora) {
        LiveData<Plaza> plazaLiveData = DataRepository.getInstance().getPlazaNotReservadaByTipoPlaza(getViewLifecycleOwner(), tipoPlaza, hora, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "fetchAndSetNewPlazaLibre: onSuccess: Plaza libre fetched.");
            }

            @Override
            public void onFailure() {
                Snackbar.make(binding.getRoot(),
                        "La aplicación no ha podido cargar plazas libres. Intentalo de nuevo más tarde.",
                        BaseTransientBottomBar.LENGTH_LONG).show();
                binding.buttonReservarConfirmar.setEnabled(false);
            }
        });
        plazaLiveData.observe(getViewLifecycleOwner(), plaza -> {
            if (plaza != null) {
                binding.buttonReservarConfirmar.setEnabled(true);
                binding.progressBarConfirmReserva.hide();
                binding.chipNewParkingSlot.setText(String.valueOf(plaza.getId()));
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

    private void setSelectedHourInterval(Hora hora, TextView selectedDurationTextView) {
        if (hora != null) {
            final Duration duration = Duration.between(
                    TimeUtils.convertEpochTolocalDateTime(hora.getHoraInicio()),
                    TimeUtils.convertEpochTolocalDateTime(hora.getHoraFin()));
            final long minutesToHour = duration.toMinutes() % 60;
            if (minutesToHour == 0L) {
                selectedDurationTextView.setText(String.format("%s h", duration.toHours()));
            } else {
                selectedDurationTextView.setText(
                        String.format("%s,%s h", duration.toHours(), minutesToHour));
            }
        }
    }

    private void setSelectedDateInfo(Hora hora, LocalDate fecha, TextView hourIntervalTextView) {
        if (fecha != null && hora != null) {
            final String dia = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
            final String mes = fecha.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());

            DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
            final Date horaInicioDate = new Date(hora.getHoraInicio() * 1000);
            final Date horaFinDate = new Date(hora.getHoraFin() * 1000);
            final String horaInicioString = df.format(horaInicioDate);
            final String horaFinString = df.format(horaFinDate);

            hourIntervalTextView.setText(String.format("%s %s %s %s - %s",
                    dia, fecha.getDayOfMonth(), mes, horaInicioString, horaFinString));
        }
    }

    private void setTipoPlazaInfo(TipoPlaza tipoPlaza, boolean isNewReserva) {
        if (tipoPlaza != null) {
            if (isNewReserva){
                switch (tipoPlaza){
                    case ESTANDAR:
                        binding.chipNewCar.setVisibility(View.VISIBLE);
                        break;
                    case ELECTRICO:
                        binding.chipNewElectricCar.setVisibility(View.VISIBLE);
                        break;
                    case MOTO:
                        binding.chipNewMotorcycle.setVisibility(View.VISIBLE);
                        break;
                    case DISCAPACITADO:
                        binding.chipNewAccessibleCar.setVisibility(View.VISIBLE);
                        break;
                }
            }else{
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
                    reservaUuid,
                    plazaToReserve,
                    mainViewModel.getSelectedHour().getValue());

            DataRepository.getInstance().updateReserva(reserva, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG,"Reserva modified: " + reserva);
                    mainViewModel.setReservarState(ReservarState.MODIFICADO);
                    mainViewModel.setSelectedHour(null);
                    NavController navController = Navigation.findNavController(v);
                    navController.navigateUp();
                    navController.navigateUp();
                }

                @Override
                public void onFailure() {
                    Snackbar.make(v,
                            "La aplicación no ha podido modificar la reserva. Intentalo de nuevo más tarde.",
                            BaseTransientBottomBar.LENGTH_LONG).show();
                }
            });

        });
    }
}