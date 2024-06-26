package com.lksnext.ParkingBGomez.view.adapter;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ListAdapter;

import com.lksnext.ParkingBGomez.databinding.ItemReservaBinding;
import com.lksnext.ParkingBGomez.domain.Hora;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.domain.ReservationsRefreshListener;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.utils.TimeUtils;
import com.lksnext.ParkingBGomez.view.fragment.ReservaListBottomSheet;
import com.lksnext.ParkingBGomez.view.holder.ReservasViewHolder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

public class ReservasAdapter extends ListAdapter<Reserva, ReservasViewHolder> {

    private ItemReservaBinding binding;
    private final FragmentManager fragmentManager;
    private final ReservationsRefreshListener refreshListener;

    public ReservasAdapter(@NonNull FragmentManager fragmentManager, ReservationsRefreshListener refreshListener) {
        super(Reserva.DIFF_CALLBACK);
        this.fragmentManager = fragmentManager;
        this.refreshListener = refreshListener;
    }


    @NonNull
    @Override
    public ReservasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemReservaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReservasViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ReservasViewHolder holder, int position) {
        final Reserva reserva = getItem(position);

        setOptionsModal(reserva);

        setPlazaIdInfo(reserva);
        setSelectedHourInterval(reserva);
        setTipoPlazaInfo(reserva);
        setTimeInfo(reserva);
    }

    private void setOptionsModal(Reserva reserva) {
        var modalBottomSheet = new ReservaListBottomSheet(reserva, refreshListener);
        binding.reservaItemCardView.setOnClickListener(v ->
                modalBottomSheet.show(this.fragmentManager, ReservaListBottomSheet.TAG));
    }

    private void setPlazaIdInfo(Reserva reserva) {
        long plaza = reserva.getPlaza().getId();
        binding.chipParkingSlot.setText(String.valueOf(plaza));
    }

    private void setTimeInfo(Reserva reserva) {
        final Hora hora = reserva.getHora();
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        final Date horaInicioDate = new Date(hora.getHoraInicio() * 1000);
        final Date horaFinDate = new Date(hora.getHoraFin() * 1000);
        final String horaInicioString = df.format(horaInicioDate);
        final String horaFinString = df.format(horaFinDate);

        LocalDateTime localDateTime = TimeUtils.convertTimestampToLocalDateTime(reserva.getFecha());
        LocalDate localDate = localDateTime.toLocalDate();

        final String dia = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, java.util.Locale.getDefault());
        final String mes = localDate.getMonth().getDisplayName(TextStyle.FULL, java.util.Locale.getDefault());

        binding.confirmHourInterval.setText(String.format("%s %s %s %s - %s",
                dia, localDate.getDayOfMonth(), mes, horaInicioString, horaFinString));
    }

    private void setTipoPlazaInfo(Reserva reserva) {
        TipoPlaza tipoPlaza = reserva.getPlaza().getTipoPlaza();
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

    private void setSelectedHourInterval(Reserva reserva) {
        Hora hora = reserva.getHora();
        final Duration duration = Duration.between(
                TimeUtils.convertEpochTolocalDateTime(hora.getHoraInicio()),
                TimeUtils.convertEpochTolocalDateTime(hora.getHoraFin()));
        final long minutesToHour = duration.toMinutes() % 60;

        if (minutesToHour == 0L) {
            binding.chipSelectedDuration.setText(String.format(Locale.getDefault(), "%s h", duration.toHours()));
        } else {
            binding.chipSelectedDuration.setText(
                    String.format(Locale.getDefault(), "%s,%s h", duration.toHours(), minutesToHour));
        }
    }
}