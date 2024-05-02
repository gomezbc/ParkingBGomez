package com.lksnext.ParkingBGomez.domain;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.time.LocalDateTime;

public record Reserva(LocalDateTime fecha,
                      String usuario,
                      long id,
                      Plaza plaza,
                      Hora hora) {
    public static final DiffUtil.ItemCallback<Reserva> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Reserva oldReserva, @NonNull Reserva newReserva) {
                    // User properties may have changed if reloaded from the DB, but ID is fixed
                    return oldReserva.id() == newReserva.id();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Reserva oldItem, @NonNull Reserva newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
