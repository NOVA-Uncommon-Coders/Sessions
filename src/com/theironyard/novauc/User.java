package com.theironyard.novauc;


import java.util.ArrayList;

public class User {

    private String name;
    private String password;
    ArrayList<String> aVector = new ArrayList<>();

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

}
