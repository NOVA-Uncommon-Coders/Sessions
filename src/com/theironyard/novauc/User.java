package com.theironyard.novauc;

import java.util.ArrayList;

public class User {
    private String name;
    private String password;
    private ArrayList<String> posts = new ArrayList<>();

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<String> posts) {
        this.posts = posts;
    }


    public User(){

    }
    public User(String name, String password){
        this.name = name;
        this.password = password;
    }
    public String getName(){
        return name;
    }
    public String getPassword(){
        return password;
    }
}
