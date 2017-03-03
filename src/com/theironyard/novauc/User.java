package com.theironyard.novauc;

import java.util.ArrayList;

/**
 * Created by jerieshasmith on 3/2/17.
 */
public class User {
    String name;
    ArrayList<Form> forms = new ArrayList<>();

    public User(String name) {
        this.name = name;
    }
}

