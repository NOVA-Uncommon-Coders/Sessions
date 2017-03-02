package com.theironyard.novauc;

import java.util.ArrayList;

/**
 * Created by dangelojoyce on 3/1/17.
 */
public class User {

    String name;
    String password;
    ArrayList userMessages = new ArrayList();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList getMessages() {
        return userMessages;
    }

    public void setMessages(ArrayList userMessages) {
        this.userMessages = userMessages;
    }

    public User (String name, String password){

        this.name = name;
        this.password = password;
    }

}
