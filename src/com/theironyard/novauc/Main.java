package com.theironyard.novauc;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {

    public static HashMap<String, User> userAccess = new HashMap<>();

    public static void main(String[] args) {

        Spark.init();

        Spark.get("/", ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    HashMap hashLocal = new HashMap();


                    System.out.println("fresh page");
                    if(!userAccess.containsKey(name)) {
                        return new ModelAndView(hashLocal, "index.html");
                    } else {
                        //hashLocal.put("name", user.getName());
                        hashLocal.put("aVector", userAccess.get(name).aVector);
                        return new ModelAndView(hashLocal, "messages.html");
                    }
                }),
                new MustacheTemplateEngine()

        );

        Spark.post("/create-user", ((request, response) -> {
                    System.out.println("accessed create user");
                    String nomDeGuerre = request.queryParams("createUser");
                    String passwordz = request.queryParams("createPassword");

                    Session session = request.session();
                    session.attribute("userName", nomDeGuerre);

                    userAccess.put(nomDeGuerre, new User(nomDeGuerre, passwordz));
                    response.redirect("/");
                    return "";
                })
        );

        Spark.post("/create-message", (((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    System.out.println("accessed create message");
                    String handwritten = request.queryParams("createMessage");
                    userAccess.get(name).aVector.add(handwritten);
                    response.redirect("/");
                    return "";
                }))
        );
    }
}
