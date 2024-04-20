package com.lksnext.ParkingBGomez.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.ParkingBGomez.domain.BottomNavState;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<BottomNavState> bottomNavState =
            new MutableLiveData<>(BottomNavState.HOME);
    private final MutableLiveData<LocalDate> selectedDate =
            new MutableLiveData<>(LocalDate.now(ZoneId.systemDefault()));

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
}
