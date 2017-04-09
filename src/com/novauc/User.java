package com.novauc;

import sun.plugin2.message.Message;

import java.util.ArrayList;

//import java.util.ArrayList;

public class User {
    String name;
    String password;
    ArrayList<xxMessagesxx> messages = new ArrayList<>();

    public User(String name, String password, ArrayList<xxMessagesxx> messages) {
        this.name = name;
        this.password = password;
        this.messages = messages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<xxMessagesxx> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<xxMessagesxx> messages) {
        this.messages = messages;
    }

//    public ArrayList<Message> getMessages() {
//        return messages;
//    }
//
//    public void setMessages(ArrayList<Message> messages) {
//        this.messages = messages;
//    }
}