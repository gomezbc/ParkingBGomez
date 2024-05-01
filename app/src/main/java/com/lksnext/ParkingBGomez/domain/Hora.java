package com.lksnext.ParkingBGomez.domain;

import java.time.LocalTime;

public record Hora(LocalTime horaInicio,
                   LocalTime horaFin) {
}
