package com.example.diploma_hospital.model;

public class Note {
    public String time;
    public String userId;
    public String doctorId;

    public Note(){}

    public Note(String time, String userId, String doctorId) {
        this.time = time;
        this.userId = userId;
        this.doctorId = doctorId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
}
