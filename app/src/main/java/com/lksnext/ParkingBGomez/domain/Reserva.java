package com.lksnext.ParkingBGomez.domain;

import java.time.LocalDateTime;

public record Reserva(LocalDateTime fecha,
                      String usuario,
                      long id,
                      Plaza plaza,
                      Hora hora) {
}
