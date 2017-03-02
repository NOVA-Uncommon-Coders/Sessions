package com.theironyard.novauc;

import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {

    static User user;

    public static void main(String[] args) {

        Spark.init();

        Spark.get("/", ((request, response) -> {
                    HashMap hashBrowns = new HashMap();
                    System.out.println("fresh page");
                    if(user == null) {
                        return new ModelAndView(hashBrowns, "index.html");
                    } else {
                        hashBrowns.put("nombre", user.getNombre());
                        hashBrowns.put("aVector", user.aVector);
                        return new ModelAndView(hashBrowns, "messages.html");
                    }
                }),
                new MustacheTemplateEngine()

        );

        Spark.post("/create-user", ((request, response) -> {
                    //System.out.println("accessed create user");
                    String nomDeGuerre = request.queryParams("createUser");
                    user = new User(nomDeGuerre);
                    response.redirect("/");
                    return "";
                })
        );

        Spark.post("/create-message", (((request, response) -> {
                    //System.out.println("accessed create message");
                    String handwritten = request.queryParams("createMessage");
                    user.aVector.add(handwritten);
                    response.redirect("/");
                    return "";
                }))
        );
    }
}
