package com.theironyard.novauc;

/**
 * Created by souporman on 3/1/17.
 */
public class Messages {
    String post;
    String creator;
    String createdDate;

    public Messages(String post, String creator, String createdDate) {
        this.post = post;
        this.creator = creator;
        this.createdDate = createdDate;
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
