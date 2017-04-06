package com.theironyard.novauc;

import java.util.ArrayList;

/**
 * Created by Eric on 3/5/17.
 */
public class User {

    String name;
    String password;
    ArrayList<Message> messageList = new ArrayList();
                /* key, value = user, password */
    //is this where I would put String password;  ????? or should I make another method in the User class?

    public User(String name, String password, ArrayList messageList) {
        this.name = name;
        this.password = password;
        this.messageList = messageList;



    }

    public static void storeText(String text) {
       //  messagelist //somehow I need to create a method that will allow me to store messages.

    }

}



