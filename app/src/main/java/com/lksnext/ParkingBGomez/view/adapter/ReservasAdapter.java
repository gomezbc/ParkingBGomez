package com.lksnext.ParkingBGomez.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;

import com.google.android.material.chip.Chip;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.domain.Hora;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.view.holder.ReservasViewHolder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.TextStyle;

public class ReservasAdapter extends ListAdapter<Reserva, ReservasViewHolder> {

    public ReservasAdapter() {
        super(Reserva.DIFF_CALLBACK);
    }


    @NonNull
    @Override
    public ReservasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserva, parent, false);
        return new ReservasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservasViewHolder holder, int position) {
        final Reserva reserva = getItem(position);

        Hora hora = setSelectedHourInterval(holder, reserva);

        long plaza = reserva.id();
        TextView plazaTextView = holder.itemView.findViewById(R.id.chip_parking_slot);
        plazaTextView.setText(String.valueOf(plaza));

        setTipoPlazaInfo(holder, reserva);

        setSelectedDateInfo(holder, reserva, hora);
    }

    private static void setSelectedDateInfo(@NonNull ReservasViewHolder holder, Reserva reserva, Hora hora) {
        LocalDate fecha = reserva.fecha().toLocalDate();
        if (fecha != null && hora != null) {
            final String dia = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, java.util.Locale.getDefault());
            final String mes = fecha.getMonth().getDisplayName(TextStyle.FULL, java.util.Locale.getDefault());
            TextView hourInterval = holder.itemView.findViewById(R.id.date_info);
            hourInterval.setText(String.format("%s %s %s %s:%s - %s:%s",
                    dia, fecha.getDayOfMonth(), mes, hora.horaInicio().getHour(),
                    String.format("%02d", hora.horaInicio().getMinute()),
                    hora.horaFin().getHour(),
                    String.format("%02d", hora.horaFin().getMinute())));
        }
    }

    private static void setTipoPlazaInfo(@NonNull ReservasViewHolder holder, Reserva reserva) {
        TipoPlaza tipoPlaza = reserva.plaza().tipoPlaza();
        if (tipoPlaza != null) {
            switch (tipoPlaza){
                case ESTANDAR:
                    Chip chipCar = holder.itemView.findViewById(R.id.chip_car);
                    chipCar.setVisibility(View.VISIBLE);
                    break;
                case ELECTRICO:
                    Chip chipElectricCar = holder.itemView.findViewById(R.id.chip_electric_car);
                    chipElectricCar.setVisibility(View.VISIBLE);
                    break;
                case MOTO:
                    Chip chipMotorcycle = holder.itemView.findViewById(R.id.chip_motorcycle);
                    chipMotorcycle.setVisibility(View.VISIBLE);
                    break;
                case DISCAPACITADO:
                    Chip chipAccessibleCar = holder.itemView.findViewById(R.id.chip_accessible_car);
                    chipAccessibleCar.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Nullable
    private static Hora setSelectedHourInterval(@NonNull ReservasViewHolder holder, Reserva reserva) {
        Hora hora = reserva.hora();
        if (hora != null) {
            final Duration duration = Duration.between(hora.horaInicio(), hora.horaFin());
            final long minutesToHour = duration.toMinutes() % 60;
            TextView durationTextView = holder.itemView.findViewById(R.id.chip_duration);
            if (minutesToHour == 0L) {
                durationTextView.setText(String.format("%s h", duration.toHours()));
            } else {
                durationTextView.setText(
                        String.format("%s,%s h", duration.toHours(), minutesToHour));
            }
        }
        return hora;
    }
}