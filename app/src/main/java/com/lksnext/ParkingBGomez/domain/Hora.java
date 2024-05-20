package com.lksnext.ParkingBGomez.domain;

import androidx.annotation.NonNull;

import java.time.LocalTime;
import java.util.Objects;

public class Hora {
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Hora() {
    }

    public Hora(LocalTime horaInicio, LocalTime horaFin) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
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
