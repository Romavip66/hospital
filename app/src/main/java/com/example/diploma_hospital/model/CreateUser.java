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

    public CreateUser(String number, String uid, String password, String roleId, String name, String category, String email) {
        this.name = name;
        this.number=number;
        this.email = email;
        this.password = password;
        this.category = category;
        this.roleId = roleId;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
