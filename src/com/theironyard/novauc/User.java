package com.theironyard.novauc;

import java.util.ArrayList;

public class User {
    int id;
    String userName;
    String password;

    ArrayList<Messages> posts = new ArrayList<>();

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Messages> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Messages> posts) {
        this.posts = posts;
    }

    public User(int id,String username, String password){
        this.id=id;
        this.userName=username;
        this.password=password;
    }
}
