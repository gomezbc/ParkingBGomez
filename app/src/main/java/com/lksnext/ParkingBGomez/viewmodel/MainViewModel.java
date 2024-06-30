package com.lksnext.ParkingBGomez.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.ParkingBGomez.domain.Hora;
import com.lksnext.ParkingBGomez.enums.ReservarState;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.utils.TimeUtils;

import java.time.LocalDate;
import java.util.Objects;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<LocalDate> selectedDate =
            new MutableLiveData<>(LocalDate.now(TimeUtils.ZONE_ID));
    private final MutableLiveData<Integer> selectedDateDayChip =
            new MutableLiveData<>();
    private final MutableLiveData<TipoPlaza> selectedTipoPlaza =
            new MutableLiveData<>(TipoPlaza.ESTANDAR);
    private final MutableLiveData<Hora> selectedHour = new MutableLiveData<>(null);
    private final MutableLiveData<ReservarState> reservarState =
            new MutableLiveData<>(ReservarState.RESERVAR);
    private final MutableLiveData<LocalDate> newSelectedDate =
            new MutableLiveData<>(LocalDate.now(TimeUtils.ZONE_ID));
    private final MutableLiveData<Integer> newSelectedDateDayChip =
            new MutableLiveData<>();
    private final MutableLiveData<TipoPlaza> newSelectedTipoPlaza =
            new MutableLiveData<>(TipoPlaza.ESTANDAR);

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
        LocalDate newLocalDate = Objects.requireNonNull(selectedDate.getValue())
                .withDayOfMonth(selectedDateDay);
        // If the selected day is in the next month, we need to add a month to avoid
        // setting the day in the current mont
        if (selectedDateDay < selectedDate.getValue().getDayOfMonth()){
            newLocalDate = newLocalDate.plusMonths(1);
        }
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

    public MutableLiveData<LocalDate> getNewSelectedDate() {
        return newSelectedDate;
    }

    public MutableLiveData<Integer> getNewSelectedDateDayChip() {
        return newSelectedDateDayChip;
    }

    public MutableLiveData<TipoPlaza> getNewSelectedTipoPlaza() {
        return newSelectedTipoPlaza;
    }

    public void setNewSelectedDateDay(int selectedDateDay) {
        final LocalDate newLocalDate = Objects.requireNonNull(newSelectedDate.getValue())
                .withDayOfMonth(selectedDateDay);
        newSelectedDate.setValue(newLocalDate);
    }

    public void setNewSelectedDateDayChip(int newSelectedDateDayChip) {
        this.newSelectedDateDayChip.setValue(newSelectedDateDayChip);
    }

    public void setNewSelectedTipoPlaza(TipoPlaza tipoPlaza) {
        newSelectedTipoPlaza.setValue(tipoPlaza);
    }
}
