package com.theironyard.novauc;


import java.util.ArrayList;

public class User {

    private String nombre;
    ArrayList<String> aVector = new ArrayList<>();

    public User(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

}
