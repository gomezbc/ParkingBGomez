package com.lksnext.ParkingBGomez.domain;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reserva {

    private LocalDateTime fecha;
    private String usuario;
    private String uuid;
    private Plaza plaza;
    private Hora hora;

    public Reserva() {
    }

    public Reserva(LocalDateTime fecha, String usuario, String uuid, Plaza plaza, Hora hora) {
        this.fecha = fecha;
        this.usuario = usuario;
        this.uuid = uuid;
        this.plaza = plaza;
        this.hora = hora;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getUuid() {
        return uuid;
    }

    public Plaza getPlaza() {
        return plaza;
    }

    public void setPlaza(Plaza plaza) {
        this.plaza = plaza;
    }

    public Hora getHora() {
        return hora;
    }

    public void setHora(Hora hora) {
        this.hora = hora;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        return Objects.equals(fecha, reserva.fecha) && Objects.equals(usuario, reserva.usuario) && Objects.equals(uuid, reserva.uuid) && Objects.equals(plaza, reserva.plaza) && Objects.equals(hora, reserva.hora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fecha, usuario, uuid, plaza, hora);
    }

    @NonNull
    @Override
    public String toString() {
        return "Reserva{" +
                "fecha=" + fecha +
                ", usuario='" + usuario + '\'' +
                ", uuid='" + uuid + '\'' +
                ", plaza=" + plaza +
                ", hora=" + hora +
                '}';
    }

    public static final DiffUtil.ItemCallback<Reserva> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Reserva oldReserva, @NonNull Reserva newReserva) {
                    // User properties may have changed if reloaded from the DB, but ID is fixed
                    return Objects.equals(oldReserva.getUuid(), newReserva.getUuid());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Reserva oldItem, @NonNull Reserva newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
