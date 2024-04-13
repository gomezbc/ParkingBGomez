package com.lksnext.ParkingBGomez.domain;

import java.time.LocalDateTime;

public record Hora(LocalDateTime horaInicio,
                   LocalDateTime horaFin) {
}
