package com.polimi.mobileuse.model.user;


import java.io.Serializable;

public class User implements Serializable{
    private String name;
    private String surname;
    private String phoneNumber;
    private String homeAddress;
    private UserHabits userHabits;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public UserHabits getUserHabits() {
        return userHabits;
    }

    public void setUserHabits(UserHabits userHabits) {
        this.userHabits = userHabits;
    }
}
