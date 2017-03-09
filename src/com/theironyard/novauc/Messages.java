package com.theironyard.novauc;


public class Messages {
    int id;
    String post;
    String creator;

    public Messages(int id,String post, String creator) {
        this.id =id;
        this.post = post;
        this.creator = creator;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

}
