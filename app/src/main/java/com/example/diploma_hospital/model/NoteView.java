package com.example.diploma_hospital.model;

public class NoteView {
    public String guestName;
    public String date;
    public String num;

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public NoteView() {
    }

    public NoteView(String guestName, String date, String num) {
        this.guestName = guestName;
        this.date = date;
        this.num = num;
    }
}
