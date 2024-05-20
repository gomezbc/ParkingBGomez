package com.lksnext.ParkingBGomez.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.ParkingBGomez.domain.Hora;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.enums.BottomNavState;
import com.lksnext.ParkingBGomez.enums.ReservarState;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<BottomNavState> bottomNavState =
            new MutableLiveData<>(BottomNavState.HOME);
    private final MutableLiveData<LocalDate> selectedDate =
            new MutableLiveData<>(LocalDate.now(ZoneId.systemDefault()));
    private final MutableLiveData<Integer> selectedDateDayChip =
            new MutableLiveData<>();
    private final MutableLiveData<TipoPlaza> selectedTipoPlaza =
            new MutableLiveData<>(TipoPlaza.ESTANDAR);
    private final MutableLiveData<Hora> selectedHour = new MutableLiveData<>(null);
    private final MutableLiveData<ReservarState> reservarState =
            new MutableLiveData<>(ReservarState.RESERVAR);
    private final MutableLiveData<Map<LocalDate, List<Reserva>>> reservasByDay =
            new MutableLiveData<>(new HashMap<>());

    public MutableLiveData<BottomNavState> getBottomNavState() {
        return bottomNavState;
    }

    public void setBottomNavState(BottomNavState state) {
        bottomNavState.setValue(state);
    }

    public MutableLiveData<LocalDate> getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(@NonNull LocalDate date) {
        selectedDate.setValue(date);
    }

    /**
     * Set the selectedDate day of the month to the selected day of the month
     * @param selectedDateDay the day of the month
     */
    public void setSelectedDateDay(int selectedDateDay) {
        final LocalDate newLocalDate = Objects.requireNonNull(selectedDate.getValue())
                .withDayOfMonth(selectedDateDay);
        selectedDate.setValue(newLocalDate);
    }

    public MutableLiveData<Integer> getSelectedDateDayChip() {
        return selectedDateDayChip;
    }

    public void setSelectedDateDayChip(int selectedDateDayChip) {
        this.selectedDateDayChip.setValue(selectedDateDayChip);
    }

    public MutableLiveData<TipoPlaza> getSelectedTipoPlaza() {
        return selectedTipoPlaza;
    }

    public void setSelectedTipoPlaza(TipoPlaza tipoPlaza) {
        selectedTipoPlaza.setValue(tipoPlaza);
    }

    public MutableLiveData<Hora> getSelectedHour() {
        return selectedHour;
    }

    public void setSelectedHour(Hora hora) {
        selectedHour.setValue(hora);
    }

    public MutableLiveData<ReservarState> getReservarState() {
        return reservarState;
    }

    public void setReservarState(ReservarState state) {
        reservarState.setValue(state);
    }

    public MutableLiveData<Map<LocalDate, List<Reserva>>> getReservasByDay() {
        return reservasByDay;
    }

    public void addReserva(Reserva reserva) {
        LocalDate localDate = reserva.getFecha().toLocalDate();
        if (!Objects.requireNonNull(reservasByDay.getValue()).containsKey(localDate)) {
            Objects.requireNonNull(reservasByDay.getValue()).put(localDate, List.of(reserva));
        } else {
            List<Reserva> prevReservasForDate =
                    new ArrayList<>(Objects.requireNonNull(reservasByDay.getValue()).get(localDate));

            prevReservasForDate.add(reserva);
            Objects.requireNonNull(reservasByDay.getValue()).put(localDate, prevReservasForDate);
        }
    }
}
