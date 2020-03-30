package com.example.diploma_hospital.model;

public class Desc {
    public String userName;
    public String comms;
    public String empt;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComms() {
        return comms;
    }

    public void setComms(String comms) {
        this.comms = comms;
    }

    public String getEmpt() {
        return empt;
    }

    public void setEmpt(String empt) {
        this.empt = empt;
    }

    public Desc(String userName, String comms, String empt) {
        this.userName = userName;
        this.comms = comms;
        this.empt = empt;
    }

    public Desc() {
    }
}
