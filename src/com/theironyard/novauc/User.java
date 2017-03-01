package com.theironyard.novauc;


import java.util.ArrayList;

public class User {
    private String nombre;
    private ArrayList vectorCatalog = new ArrayList<>();

    public User(String nombre, ArrayList vectorCatalog) {
        this.nombre = nombre;
        this.vectorCatalog = vectorCatalog;
    }

    public ArrayList getVectorCatalog() {
        return vectorCatalog;
    }

    public String getNombre() {
        return nombre;
    }
}
