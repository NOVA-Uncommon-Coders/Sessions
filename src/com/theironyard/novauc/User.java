package com.theironyard.novauc;
import java.util.ArrayList;

public class User {
    ArrayList<MyMessages> messages = new ArrayList<>();
    String name;
    String password;

    public User(ArrayList<MyMessages> messages, String name, String password) {
        this.messages = messages;
        this.name = name;
        this.password = password;
    }
}