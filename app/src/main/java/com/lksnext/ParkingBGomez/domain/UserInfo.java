package com.lksnext.ParkingBGomez.domain;

public class UserInfo {
    private String uuid;
    private String phone;

    public UserInfo() {
    }

    public UserInfo(String uuid, String phone) {
        this.uuid = uuid;
        this.phone = phone;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUuid() {
        return uuid;
    }

    public String getPhone() {
        return phone;
    }

}
