package com.lksnext.ParkingBGomez.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.ParkingBGomez.domain.BottomNavState;

public class BottomNavViewModel extends ViewModel {

    private final MutableLiveData<BottomNavState> bottomNavState = new MutableLiveData<>(BottomNavState.HOME);

    public MutableLiveData<BottomNavState> getBottomNavState() {
        return bottomNavState;
    }

    public void setBottomNavState(BottomNavState state) {
        bottomNavState.setValue(state);
    }
}
