package com.theironyard.novauc;


import java.util.ArrayList;

public class User {

    private String name;
    ArrayList<String> aVector = new ArrayList<>();

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
