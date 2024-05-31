package com.lksnext.ParkingBGomez.view.adapter;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;

import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.domain.Hora;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.utils.TimeUtils;
import com.lksnext.ParkingBGomez.view.holder.ReservasViewHolder;

import java.time.Duration;
import java.util.Date;
import java.util.Locale;

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

        if (reserva.getPlaza() == null || reserva.getFecha() == null ||
                reserva.getHora() == null || reserva.getUsuario() == null) {
            return;
        }

        long plaza = reserva.getPlaza().getId();
        TextView plazaTextView = holder.itemView.findViewById(R.id.text_parking_slot);
        plazaTextView.setText(String.valueOf(plaza));

        setTipoPlazaInfo(holder, reserva);

        setTimeInfo(holder, hora);
    }

    private static void setTimeInfo(@NonNull ReservasViewHolder holder, Hora hora) {
        if (hora != null) {
            TextView hourInterval = holder.itemView.findViewById(R.id.date_info);
            DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
            final Date horaInicioDate = new Date(hora.getHoraInicio());
            final Date horaFinDate = new Date(hora.getHoraFin());
            final String horaInicioString = df.format(horaInicioDate);
            final String horaFinString = df.format(horaFinDate);
            Log.d("setTimeInfo", horaInicioString + " - " + horaFinString);
            hourInterval.setText(String.format("%s - %s", horaInicioString, horaFinString));
        }
    }

    private static void setTipoPlazaInfo(@NonNull ReservasViewHolder holder, Reserva reserva) {
        TipoPlaza tipoPlaza = reserva.getPlaza().getTipoPlaza();
        TextView tipoPlazaTextView = holder.itemView.findViewById(R.id.text_slot_type);
        ImageView tipoPlazaImageView = holder.itemView.findViewById(R.id.tipo_plaza_icon);
        if (tipoPlaza != null) {
            switch (tipoPlaza){
                case ESTANDAR:
                    tipoPlazaTextView.setText(R.string.car);
                    tipoPlazaImageView.setImageResource(R.drawable.directions_car_fill);
                    break;
                case ELECTRICO:
                    tipoPlazaTextView.setText(R.string.electric_car);
                    tipoPlazaImageView.setImageResource(R.drawable.electric_car_fill);
                    break;
                case MOTO:
                    tipoPlazaTextView.setText(R.string.motorcycle);
                    tipoPlazaImageView.setImageResource(R.drawable.motorcycle_fill);
                    break;
                case DISCAPACITADO:
                    tipoPlazaTextView.setText(R.string.accessible_car);
                    tipoPlazaImageView.setImageResource(R.drawable.accessible_forward_fill);
                    break;
            }
        }
    }

    @Nullable
    private static Hora setSelectedHourInterval(@NonNull ReservasViewHolder holder, Reserva reserva) {
        Hora hora = reserva.getHora();
        if (hora != null) {
            final Duration duration = Duration.between(
                    TimeUtils.convertEpochTolocalDateTime(hora.getHoraInicio()),
                    TimeUtils.convertEpochTolocalDateTime(hora.getHoraFin()));
            final long minutesToHour = duration.toMinutes() % 60;
            TextView durationTextView = holder.itemView.findViewById(R.id.text_duration);
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