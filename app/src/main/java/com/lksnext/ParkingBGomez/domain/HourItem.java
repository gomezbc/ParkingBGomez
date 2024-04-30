package com.lksnext.ParkingBGomez.domain;

import androidx.annotation.NonNull;

public class HourItem{
    private String hour;
    private boolean selected;
    private boolean enabled;
    private boolean inMiddle;

    public HourItem(String hour, boolean enabled){
        this.hour = hour;
        this.enabled = enabled;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isInMiddle() {
        return inMiddle;
    }

    public void setInMiddle(boolean inMiddle) {
        this.inMiddle = inMiddle;
    }

    @NonNull
    @Override
    public String toString() {
        return "HourItem{" +
                "hour='" + hour + '\'' +
                ", selected=" + selected +
                ", enabled=" + enabled +
                ", inMiddle=" + inMiddle +
                '}';
    }
}
