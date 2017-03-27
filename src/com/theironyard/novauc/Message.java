package com.theironyard.novauc;

import java.util.ArrayList;

/**
 * Created by dangelojoyce on 3/27/17.
 */
public class Message {
    private static int id = 0;
    ArrayList<Message> messages;

    public Message (){

    }

    public Message (int i, ArrayList messages){
        this.id = ++id;
        this.messages = messages;

    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        Message.id = id;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
