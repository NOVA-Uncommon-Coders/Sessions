package com.theironyard.novauc;

/**
 * Created by vtcurry on 3/27/17.
 */
public class Posts {
    int id;
    String post;
    String poster;

    public Posts(int id, String post, String poster) {
        this.id = id;
        this.post = post;
        this.poster = poster;
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

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
