package com.lksnext.ParkingBGomez.domain;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Hora {
    private long horaInicio;
    private long horaFin;

    public Hora() {
    }

    public Hora(long horaInicio, long horaFin) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public long getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(long horaInicio) {
        this.horaInicio = horaInicio;
    }

    public long getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(long horaFin) {
        this.horaFin = horaFin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hora hora = (Hora) o;
        return Objects.equals(horaInicio, hora.horaInicio) && Objects.equals(horaFin, hora.horaFin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(horaInicio, horaFin);
    }

    @NonNull
    @Override
    public String toString() {
        return "Hora{" +
                "horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                '}';
    }
}
