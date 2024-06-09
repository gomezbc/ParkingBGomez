package com.lksnext.ParkingBGomez.view.adapter;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.domain.Hora;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.utils.TimeUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

public class ReservaCardAdapter extends ListAdapter<Reserva, ReservaCardAdapter.ViewHolder> {

    private static class Views {
        TextView chipSelectedDuration;
        TextView chipConfirmHourInterval;
        TextView chipParkingSlot;
        Chip chipCar;
        Chip chipElectricCar;
        Chip chipMotorcycle;
        Chip chipAccessibleCar;

        public Views(View view) {
            chipSelectedDuration = view.findViewById(R.id.chip_selected_duration);
            chipConfirmHourInterval = view.findViewById(R.id.confirm_hour_interval);
            chipCar = view.findViewById(R.id.chip_car);
            chipElectricCar = view.findViewById(R.id.chip_electric_car);
            chipMotorcycle = view.findViewById(R.id.chip_motorcycle);
            chipAccessibleCar = view.findViewById(R.id.chip_accessible_car);
            chipParkingSlot = view.findViewById(R.id.chip_parking_slot);
        }


    }

    private Views views;

    public ReservaCardAdapter() {
        super(Reserva.DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserva_card, parent, false);
        this.views = new Views(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Reserva reserva = getItem(position);

        setCardInfo(reserva);

    }

    private void setCardInfo(Reserva reserva) {
        final Hora hora = setSelectedHourInterval(reserva);
        setTipoPlazaInfo(reserva);
        setSelectedDateInfo(hora, reserva);
        this.views.chipParkingSlot.setText(String.valueOf(reserva.getPlaza().getId()));
    }

    private Hora setSelectedHourInterval(Reserva reserva) {
        Hora hora = reserva.getHora();
        final Duration duration = Duration.between(
                TimeUtils.convertEpochTolocalDateTime(hora.getHoraInicio()),
                TimeUtils.convertEpochTolocalDateTime(hora.getHoraFin()));
        final long minutesToHour = duration.toMinutes() % 60;
        if (minutesToHour == 0L) {
            views.chipConfirmHourInterval.setVisibility(View.VISIBLE);
            views.chipSelectedDuration.setText(String.format("%s h", duration.toHours()));
        } else {
            views.chipSelectedDuration.setText(
                    String.format("%s,%s h", duration.toHours(), minutesToHour));
        }

        return hora;
    }

    private void setTipoPlazaInfo(Reserva reserva) {
        TipoPlaza tipoPlaza = reserva.getPlaza().getTipoPlaza();
        if (tipoPlaza != null) {
            switch (tipoPlaza){
                case ESTANDAR:
                    views.chipCar.setVisibility(View.VISIBLE);
                    break;
                case ELECTRICO:
                    views.chipElectricCar.setVisibility(View.VISIBLE);
                    break;
                case MOTO:
                    views.chipMotorcycle.setVisibility(View.VISIBLE);
                    break;
                case DISCAPACITADO:
                    views.chipAccessibleCar.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void setSelectedDateInfo(Hora hora, Reserva reserva) {
        LocalDate fecha = TimeUtils.convertStringToDateLocalTime(reserva.getFecha()).toLocalDate();
        if (fecha != null && hora != null) {
            final String dia = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, java.util.Locale.getDefault());
            final String mes = fecha.getMonth().getDisplayName(TextStyle.FULL, java.util.Locale.getDefault());

            DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
            final Date horaInicioDate = new Date(hora.getHoraInicio() * 1000);
            final Date horaFinDate = new Date(hora.getHoraFin() * 1000);
            final String horaInicioString = df.format(horaInicioDate);
            final String horaFinString = df.format(horaFinDate);

            views.chipConfirmHourInterval.setText(String.format(Locale.getDefault(),"%s %s %s %s - %s",
                    dia, fecha.getDayOfMonth(), mes, horaInicioString, horaFinString));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView materialCardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.materialCardView);
        }
    }

}
