package com.app.fypfinal.models;

public class UserModel {
    String firstName;
    String lastName;
    String userName;
    String email;
    String phoneNumber;
    String type;

    public UserModel() {
    }

    public UserModel(String firstName, String lastName, String userName,
                     String email, String phoneNumber, String type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.type = type;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getType() {
        return type;
    }
}
