package com.lksnext.ParkingBGomez.view.adapter;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ListAdapter;

import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.databinding.ItemReservaBinding;
import com.lksnext.ParkingBGomez.domain.Hora;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.domain.ReservationsRefreshListener;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.utils.TimeUtils;
import com.lksnext.ParkingBGomez.view.ReservaListBottomSheet;
import com.lksnext.ParkingBGomez.view.holder.ReservasViewHolder;

import java.time.Duration;
import java.util.Date;
import java.util.Locale;

public class ReservasAdapter extends ListAdapter<Reserva, ReservasViewHolder> {

    private ItemReservaBinding binding;
    private static final String DIVIDER = " · ";
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

        Hora hora = setSelectedHourInterval(reserva);

        var modalBottomSheet = new ReservaListBottomSheet(reserva, refreshListener);

        binding.optionsButton.setOnClickListener(v ->
                modalBottomSheet.show(this.fragmentManager, ReservaListBottomSheet.TAG));


        binding.reservaCard.setVisibility(View.VISIBLE);
        binding.reservaFallbackCard.setVisibility(View.GONE);

        long plaza = reserva.getPlaza().getId();
        binding.textParkingSlot.setText(String.format(Locale.getDefault(), "%s%s", DIVIDER, plaza));

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
            binding.dateInfo.setText(String.format("%s - %s%s", horaInicioString, horaFinString, DIVIDER));
        }
    }

    private void setTipoPlazaInfo(Reserva reserva) {
        TipoPlaza tipoPlaza = reserva.getPlaza().getTipoPlaza();
        if (tipoPlaza != null) {
            switch (tipoPlaza){
                case ESTANDAR:
                    binding.textSlotType.setText(R.string.car);
                    binding.tipoPlazaIcon.setImageResource(R.drawable.directions_car_fill);
                    break;
                case ELECTRICO:
                    binding.textSlotType.setText(R.string.electric_car);
                    binding.tipoPlazaIcon.setImageResource(R.drawable.electric_car_fill);
                    break;
                case MOTO:
                    binding.textSlotType.setText(R.string.motorcycle);
                    binding.tipoPlazaIcon.setImageResource(R.drawable.motorcycle_fill);
                    break;
                case DISCAPACITADO:
                    binding.textSlotType.setText(R.string.accessible_car);
                    binding.tipoPlazaIcon.setImageResource(R.drawable.accessible_forward_fill);
                    break;
            }
        }
    }

    @Nullable
    private Hora setSelectedHourInterval(Reserva reserva) {
        Hora hora = reserva.getHora();
        final Duration duration = Duration.between(
                TimeUtils.convertEpochTolocalDateTime(hora.getHoraInicio()),
                TimeUtils.convertEpochTolocalDateTime(hora.getHoraFin()));
        final long minutesToHour = duration.toMinutes() % 60;

        if (minutesToHour == 0L) {
            binding.textDuration.setText(String.format("%s h%s", duration.toHours(), DIVIDER));
        } else {
            binding.textDuration.setText(
                    String.format("%s,%s h%s", duration.toHours(), minutesToHour, DIVIDER));
        }
        return hora;
    }
}