package com.lksnext.ParkingBGomez.view.adapter;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private static class Views {
        private final TextView plazaNumberTextView;
        private final TextView tipoPlazaTextView;
        private final ImageView tipoPlazaImageView;
        private final TextView dateInfoTextView;
        private final TextView durationTextView;
        private final LinearLayout reservaCard;
        private final LinearLayout reservaFallbackCard;

        public Views(ReservasViewHolder holder) {
            plazaNumberTextView = holder.itemView.findViewById(R.id.text_parking_slot);
            tipoPlazaTextView = holder.itemView.findViewById(R.id.text_slot_type);
            tipoPlazaImageView = holder.itemView.findViewById(R.id.tipo_plaza_icon);
            dateInfoTextView = holder.itemView.findViewById(R.id.date_info);
            durationTextView = holder.itemView.findViewById(R.id.text_duration);
            reservaCard = holder.itemView.findViewById(R.id.reservaCard);
            reservaFallbackCard = holder.itemView.findViewById(R.id.reservaFallbackCard);
        }
    }

    private Views views;
    private static final String DIVIDER = " · ";

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
        this.views = new Views(holder);
        final Reserva reserva = getItem(position);

        Hora hora = setSelectedHourInterval(reserva);

        if (reserva.getPlaza() == null || reserva.getFecha() == null ||
                reserva.getHora() == null || reserva.getUsuario() == null) {
            this.views.reservaCard.setVisibility(View.GONE);
            this.views.reservaFallbackCard.setVisibility(View.VISIBLE);
            return;
        }

        this.views.reservaCard.setVisibility(View.VISIBLE);
        this.views.reservaFallbackCard.setVisibility(View.GONE);

        long plaza = reserva.getPlaza().getId();
        this.views.plazaNumberTextView.setText(DIVIDER + plaza);

        setTipoPlazaInfo(reserva);

        setTimeInfo(hora);
    }

    private void setTimeInfo(Hora hora) {
        if (hora != null) {
            DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
            final Date horaInicioDate = new Date(hora.getHoraInicio() * 1000);
            final Date horaFinDate = new Date(hora.getHoraFin() * 1000);
            final String horaInicioString = df.format(horaInicioDate);
            final String horaFinString = df.format(horaFinDate);
            this.views.dateInfoTextView.setText(String.format("%s - %s%s", horaInicioString, horaFinString, DIVIDER));
        }
    }

    private void setTipoPlazaInfo(Reserva reserva) {
        TipoPlaza tipoPlaza = reserva.getPlaza().getTipoPlaza();
        if (tipoPlaza != null) {
            switch (tipoPlaza){
                case ESTANDAR:
                    this.views.tipoPlazaTextView.setText(R.string.car);
                    this.views.tipoPlazaImageView.setImageResource(R.drawable.directions_car_fill);
                    break;
                case ELECTRICO:
                    this.views.tipoPlazaTextView.setText(R.string.electric_car);
                    this.views.tipoPlazaImageView.setImageResource(R.drawable.electric_car_fill);
                    break;
                case MOTO:
                    this.views.tipoPlazaTextView.setText(R.string.motorcycle);
                    this.views.tipoPlazaImageView.setImageResource(R.drawable.motorcycle_fill);
                    break;
                case DISCAPACITADO:
                    this.views.tipoPlazaTextView.setText(R.string.accessible_car);
                    this.views.tipoPlazaImageView.setImageResource(R.drawable.accessible_forward_fill);
                    break;
            }
        }
    }

    @Nullable
    private Hora setSelectedHourInterval(Reserva reserva) {
        Hora hora = reserva.getHora();
        if (hora != null) {
            final Duration duration = Duration.between(
                    TimeUtils.convertEpochTolocalDateTime(hora.getHoraInicio()),
                    TimeUtils.convertEpochTolocalDateTime(hora.getHoraFin()));
            final long minutesToHour = duration.toMinutes() % 60;

            if (minutesToHour == 0L) {
                this.views.durationTextView.setText(String.format("%s h%s", duration.toHours(), DIVIDER));
            } else {
                this.views.durationTextView.setText(
                        String.format("%s,%s h%s", duration.toHours(), minutesToHour, DIVIDER));
            }
        }
        return hora;
    }
}