package com.example.diploma_hospital.model;

public class CreateUser
{
    public String name;
    public String number;
    public String email;
    public String password;
    public String category;
    public String roleId;
    public String uid;

    public CreateUser(){}

    public CreateUser(String name, String number, String email, String password, String category, String roleId, String uid) {
        this.name = name;
        this.number=number;
        this.email = email;
        this.password = password;
        this.category = category;
        this.roleId = roleId;
        this.uid = uid;
    }
}
